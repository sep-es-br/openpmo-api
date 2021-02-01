package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class OfficePermissionService {

    private final OfficePermissionRepository repository;
    private final OfficeService officeService;
    private final PersonService personService;
    private final ModelMapper modelMapper;

    @Autowired
    public OfficePermissionService(OfficePermissionRepository repository, OfficeService officeService,
                                   PersonService personService, ModelMapper modelMapper) {
        this.repository = repository;
        this.officeService = officeService;
        this.personService = personService;
        this.modelMapper = modelMapper;
    }

    public void delete(Long idOffice, String email) {
        Optional<Person> personOptional = personService.findByEmail(email);
        if (!personOptional.isPresent())
            throw new RegistroNaoEncontradoException(ApplicationMessage.OFFICE_PERMISSION_NOT_FOUND);
        List<CanAccessOffice> list = repository.findByIdOfficeAndIdPerson(idOffice, personOptional.get().getId());
        repository.deleteAll(list);
    }

    public List<OfficePermissionDto> findAllDto(Long idOffice, String email) {
        List<OfficePermissionDto> officesPermissionDto = new ArrayList<>();

        Office office = officeService.findById(idOffice);
        List<Person> listPerson = personService.personInCanAccessOffice(idOffice);
        List<CanAccessOffice> listOfficesPermission = listOfficesPermissions(office, email);

        listPerson.forEach(person -> {
            OfficePermissionDto officePermissionDto = new OfficePermissionDto();
            List<PermissionDto> permissions = new ArrayList<>();

            List<CanAccessOffice> listPersonFiltred = listOfficesPermission.stream().filter(
                c -> c.getPerson() == person).collect(Collectors.toList());

            officePermissionDto.setPerson(this.modelMapper.map(person, PersonDto.class));
            officePermissionDto.setIdOffice(idOffice);
            listPersonFiltred.forEach(canAcessOffice -> {
                PermissionDto dto = new PermissionDto();
                dto.setId(canAcessOffice.getId());
                dto.setLevel(canAcessOffice.getPermissionLevel());
                dto.setRole(canAcessOffice.getPermitedRole());
                permissions.add(dto);
            });

            officePermissionDto.setPermissions(permissions);
            officesPermissionDto.add(officePermissionDto);
        });
        if (email != null) {
            officesPermissionDto.removeIf(o -> !o.getPerson().getEmail().equals(email));
        }

        return officesPermissionDto;
    }

    private List<CanAccessOffice> listOfficesPermissions(Office office, String email) {
        if (email == null || email.isEmpty())
            return repository.findByIdOffice(office.getId());

        Person person = personService.findByEmailWithException(email);

        return findByOfficeAndPerson(office, person);
    }

    public List<CanAccessOffice> findByOfficeAndPerson(Office office, Person person) {
        return repository.findByIdOfficeAndIdPerson(office.getId(), person.getId());
    }

    public CanAccessOffice save(CanAccessOffice officePermission) {
        return repository.save(officePermission);
    }

    public CanAccessOffice findById(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new NegocioException(ApplicationMessage.OFFICE_PERMISSION_NOT_FOUND));
    }

    public void delete(CanAccessOffice office) {
        repository.delete(office);
    }

    public void update(OfficePermissionParamDto request) {
        Person person = returnPersonBuild(request.getEmail());
        Office office = officeService.findById(request.getIdOffice());
        List<CanAccessOffice> officesPermissionsDataBase = findByOfficeAndPerson(office, person);

        officesPermissionsDataBase.forEach(permissionDatabase -> {
            if (request.getPermissions() == null || request.getPermissions().stream().noneMatch(
                rp -> rp.getRole().equals(permissionDatabase.getPermitedRole()))) {
                delete(permissionDatabase);
            }
        });
        if (!CollectionUtils.isEmpty(request.getPermissions())) {
            request.getPermissions().forEach(permission -> {
                if (permission.getId() == null && officesPermissionsDataBase.stream().noneMatch(
                    pbd -> permission.getRole() != null && permission.getRole().equals(pbd.getPermitedRole()))) {
                    save(buildCanAcessOffice(person, office, permission, null));
                    return;
                }
                Optional<CanAccessOffice> optionalCanAccessOffice = officesPermissionsDataBase.stream().filter(
                    pbd -> permission.getRole() != null && permission.getRole().equals(
                        pbd.getPermitedRole())).findFirst();
                if (optionalCanAccessOffice.isPresent()) {
                    save(buildCanAcessOffice(person, office, permission, optionalCanAccessOffice.get().getId()));
                    return;
                }
                if (permission.getId() != null) {
                    CanAccessOffice canAccessOffice = repository.findById(permission.getId()).orElseThrow(
                        () -> new RegistroNaoEncontradoException(ApplicationMessage.OFFICE_PERMISSION_NOT_FOUND));
                    save(buildCanAcessOffice(person, office, permission, canAccessOffice.getId()));
                }
            });

        }

    }

    public void store(OfficePermissionParamDto request) {
        Person person = returnPersonBuild(request.getEmail());
        Office office = officeService.findById(request.getIdOffice());
        List<CanAccessOffice> canAccessOffices = repository.findByIdOfficeAndIdPerson(office.getId(), person.getId());
        request.getPermissions().forEach(permission -> {
            if (canAccessOffices.stream().noneMatch(c -> c.getPermitedRole().equals(permission.getRole()))) {
                save(buildCanAcessOffice(person, office, permission, null));
            }
        });
    }

    private CanAccessOffice buildCanAcessOffice(Person person, Office office, PermissionDto request, Long id) {
        return new CanAccessOffice(id, "", request.getRole(), request.getLevel(), person, office);
    }

    private Person returnPersonBuild(String email) {
        Optional<Person> personOptional = personService.findByEmail(email);
        return personOptional.orElseGet(() -> storePerson(email));

    }

    private Person storePerson(String email) {
        return personService.savePersonByEmail(email);
    }

}
