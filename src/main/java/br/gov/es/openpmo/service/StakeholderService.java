package br.gov.es.openpmo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.PersonStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.RoleDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Actor;
import br.gov.es.openpmo.model.Organization;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.repository.CanAccessWorkpackRepository;
import br.gov.es.openpmo.repository.StakeholderRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class StakeholderService {

    @Autowired
    private PersonService servicePerson;
    @Autowired
    private OrganizationService serviceOrganization;
    @Autowired
    private WorkpackService serviceWorkpack;
    @Autowired
    private StakeholderRepository repository;
    @Autowired
    private CanAccessWorkpackRepository canAccessWorkpackRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${users.administrators}")
    private List<String> administrators;

    public void storeStakeholderPerson(PersonStakeholderParamDto request) {
        Optional<Person> personOptional = servicePerson.findByEmail(request.getEmail());
        Person person = createOrUpdatePerson(personOptional, request);
        Workpack workpack = serviceWorkpack.findById(request.getIdWorkpack());
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> repository.save(
                buildIsStakeholderIn(person, null, workpack, role.getRole(), role.getFrom(), role.getTo(),
                                     role.isActive())));
        }
        if (!CollectionUtils.isEmpty(request.getPermissions())) {
            request.getPermissions().forEach(permission -> canAccessWorkpackRepository.save(
                buildCanAccessWorkpack(person, workpack, permission, permission.getId())));
        }
    }

    public void updateStakeholderPerson(PersonStakeholderParamDto request) {
        Optional<Person> personOptional = servicePerson.findByEmail(request.getEmail());
        Person person = createOrUpdatePerson(personOptional, request);
        Workpack workpack = serviceWorkpack.findById(request.getIdWorkpack());
        List<IsStakeholderIn> stakeholderIns = returnListStakeholderIn(workpack.getId(), null, person.getId());
        List<CanAccessWorkpack> canAccessWorkpacks = canAccessWorkpackRepository.findByIdWorkpackAndIdPerson(workpack.getId(), person.getId());


        stakeholderIns.forEach(stakeholderDatabase -> {
            if (request.getRoles() == null || request.getRoles().stream().noneMatch(
                r -> r.getRole() != null && r.getRole().equals(stakeholderDatabase.getRole()))) {
                repository.delete(stakeholderDatabase);
            }
        });
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> {
                if (stakeholderIns.stream().noneMatch(
                    rb -> rb.getRole() != null && rb.getRole().equals(role.getRole()))) {
                    repository.save(
                        buildIsStakeholderIn(person, null, workpack, role.getRole(), role.getFrom(), role.getTo(),
                                             role.isActive()));
                } else {
                    IsStakeholderIn isStakeholderIn = stakeholderIns.stream().filter(
                        s -> s.getId().equals(role.getId())).findFirst().orElse(null);
                    if (isStakeholderIn != null) {
                        isStakeholderIn.setTo(role.getTo());
                        isStakeholderIn.setFrom(role.getFrom());
                        isStakeholderIn.setActive(role.isActive());
                        repository.save(isStakeholderIn);
                    }
                }
            });
        }

        canAccessWorkpacks.forEach(canAccessWorkpack -> {
            if (request.getPermissions() == null || request.getPermissions().stream().noneMatch(
                p -> p.getRole() != null && p.getRole().equals(canAccessWorkpack.getPermitedRole()))) {
                canAccessWorkpackRepository.delete(canAccessWorkpack);
            }
        });
        if (!CollectionUtils.isEmpty(request.getPermissions())) {
            request.getPermissions().forEach(permission -> {
                if (canAccessWorkpacks.stream().noneMatch(
                    ca -> ca.getPermitedRole() != null && ca.getPermitedRole().equals(permission.getRole()))) {
                    canAccessWorkpackRepository.save(
                        buildCanAccessWorkpack(person, workpack, permission, permission.getId()));
                } else {
                    CanAccessWorkpack canAccessWorkpack = canAccessWorkpacks.stream().filter(
                        ca -> ca.getPermitedRole() != null && ca.getPermitedRole().equals(permission.getRole())).findFirst().orElse(null);
                    if (canAccessWorkpack != null) {
                        canAccessWorkpack.setPermissionLevel(permission.getLevel());
                        canAccessWorkpackRepository.save(canAccessWorkpack);
                    }
                }
            });
        }

    }

    public void storeStakeholderOrganization(OrganizationStakeholderParamDto request) {
        Organization organization = serviceOrganization.findById(request.getIdOrganization());
        Workpack workpack = serviceWorkpack.findById(request.getIdWorkpack());
        if (request.getRoles() != null) {
            request.getRoles().forEach(role -> repository.save(
                buildIsStakeholderIn(null, organization, workpack, role.getRole(), role.getFrom(), role.getTo(),
                                     role.isActive())));
        }
    }

    public void updateStakeholderOrganization(@Valid OrganizationStakeholderParamDto request) {
        Organization organization = serviceOrganization.findById(request.getIdOrganization());
        Workpack workpack = serviceWorkpack.findById(request.getIdWorkpack());
        List<IsStakeholderIn> rolesBd = repository.findByIdWorkpackAndIdActor(workpack.getId(),
                                                                                     request.getIdOrganization());

        rolesBd.forEach(r -> {
            if (request.getRoles() == null || request.getRoles().stream().noneMatch(
                rr -> rr.getRole() != null && rr.getRole().equals(r.getRole()))) {
                repository.delete(r);
            }
        });
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> {
                if (rolesBd.stream().noneMatch(rb -> rb.getRole() != null && rb.getRole().equals(role.getRole()))) {
                    repository.save(
                        buildIsStakeholderIn(null, organization, workpack, role.getRole(), role.getFrom(), role.getTo(),
                                             role.isActive()));
                } else {
                    IsStakeholderIn stakeholderIn = rolesBd.stream().filter(s -> role.getId() != null && s.getId().equals(role.getId())).findFirst().orElse(null);
                    if (stakeholderIn != null) {
                        stakeholderIn.setTo(role.getTo());
                        stakeholderIn.setFrom(role.getFrom());
                        stakeholderIn.setActive(role.isActive());
                        repository.save(stakeholderIn);
                    }
                }
            });
        }
    }

    public void deleteOrganization(Long idWorkpack, Long idOrganization) {
        Workpack workpack = serviceWorkpack.findById(idWorkpack);
        Organization organization = serviceOrganization.findById(idOrganization);
        List<IsStakeholderIn> stakeholders = repository.findByIdWorkpackAndIdActor(workpack.getId(),
                                                                                          organization.getId());
        repository.deleteAll(stakeholders);
    }

    public void deletePerson(Long idWorkpack, Long idPerson) {
        Workpack workpack = serviceWorkpack.findById(idWorkpack);
        Person person = servicePerson.findById(idPerson);
        List<IsStakeholderIn> stakeholders = repository.findByIdWorkpackAndIdPerson(workpack.getId(), person.getId());
        repository.deleteAll(stakeholders);
        List<CanAccessWorkpack> canAccessWorkpacks = canAccessWorkpackRepository.findByIdWorkpackAndIdPerson(workpack.getId(), person.getId());
        canAccessWorkpackRepository.deleteAll(canAccessWorkpacks);
    }

    public StakeholderPersonDto findPerson(Long idWorkpack, String email) {
        Workpack workpack = serviceWorkpack.findById(idWorkpack);
        Optional<Person> personFilter = servicePerson.findByEmail(email);
        if (!personFilter.isPresent()) {
            return null;
        }
        Person person = personFilter.get();
        List<IsStakeholderIn> stakeholderIns = repository.findByIdWorkpackAndIdActor(idWorkpack, person.getId());
        List<CanAccessWorkpack> canAccessWorkpacks = canAccessWorkpackRepository.findByIdWorkpackAndIdPerson(workpack.getId(), person.getId());
        StakeholderPersonDto stakeholderPersonDto = new StakeholderPersonDto();
        stakeholderPersonDto.setIdWorkpack(idWorkpack);
        stakeholderPersonDto.setPerson(modelMapper.map(person, PersonDto.class));
        stakeholderIns.forEach(s -> stakeholderPersonDto.getRoles().add(modelMapper.map(s, RoleDto.class)));
        canAccessWorkpacks.forEach(c -> {
            PermissionDto dto = modelMapper.map(c, PermissionDto.class);
            dto.setRole(c.getPermitedRole());
            dto.setLevel(c.getPermissionLevel());
            stakeholderPersonDto.getPermissions().add(dto);
        });
        return stakeholderPersonDto;
    }

    public StakeholderOrganizationDto findOrganization(Long idWorkpack, Long idOrganization) {
        Workpack workpack = serviceWorkpack.findById(idWorkpack);
        Organization organization = serviceOrganization.findById(idOrganization);
        List<IsStakeholderIn> stakeholderIns = repository.findByIdWorkpackAndIdActor(workpack.getId(), idOrganization);
        StakeholderOrganizationDto stakeholderOrganizationDto = new StakeholderOrganizationDto();
        stakeholderOrganizationDto.setIdWorkpack(idWorkpack);
        stakeholderOrganizationDto.setOrganization(modelMapper.map(organization, OrganizationDto.class));
        stakeholderIns.forEach(s -> stakeholderOrganizationDto.getRoles().add(modelMapper.map(s, RoleDto.class)));
        return stakeholderOrganizationDto;
    }

    public List<StakeholderDto> findAll(Long idWorkpack) {
        Workpack workpack = serviceWorkpack.findById(idWorkpack);
        List<StakeholderDto> stakeholderDtos = new ArrayList<>();
        List<IsStakeholderIn> listStakeholder = repository.findByIdWorkpack(workpack.getId());
        Map<Actor, List<RoleDto>> mapRoles = new HashMap<>();
        listStakeholder.forEach(s -> {
            mapRoles.computeIfAbsent(s.getActor(), k -> new ArrayList<>());
            mapRoles.get(s.getActor()).add(modelMapper.map(s, RoleDto.class));
        });
        mapRoles.keySet().forEach(k -> {
            StakeholderDto stakeholderDto = new StakeholderDto();
            stakeholderDto.setIdWorkpack(idWorkpack);
            if (k instanceof Person) {
                stakeholderDto.setPerson(modelMapper.map(k, PersonDto.class));
            } else {
                stakeholderDto.setOrganization(modelMapper.map(k, OrganizationDto.class));
            }
            stakeholderDto.setRoles(mapRoles.get(k));
            stakeholderDtos.add(stakeholderDto);
        });
        return stakeholderDtos;
    }

    private List<IsStakeholderIn> returnListStakeholderIn(Long idWorkpack, Long idOrganization, Long idPerson) {
        Long id = null;

        if ((idOrganization != null) && (idPerson != null)) {
            throw new NegocioException(ApplicationMessage.FILTER_SHOULD_BE_PERSON_OR_ORGANIZATION);
        }
        if (idOrganization != null)
            id = idOrganization;

        if (idPerson != null)
            id = idPerson;

        return repository.findByIdWorkpackAndIdActor(idWorkpack, id);
    }

    private Person createOrUpdatePerson(Optional<Person> personOptional, PersonStakeholderParamDto request) {
        Person person = personOptional.orElse(new Person());

        return buildPerson(person, request);
    }

    private Person buildPerson(Person person, PersonStakeholderParamDto request) {
        person.setAddress(request.getAddress());
        person.setAdministrator(administrators.contains(request.getEmail()));
        person.setContactEmail(request.getContactEmail());
        person.setEmail(request.getEmail());
        person.setFullName(request.getFullName());
        person.setName(request.getName());
        person.setPhoneNumber(request.getPhoneNumber());
        return person;

    }

    private IsStakeholderIn buildIsStakeholderIn(Person person, Organization organization, Workpack workpack,
                                                 String role, LocalDate from, LocalDate to, boolean isActive) {
        Actor actor = person == null ? organization : person;
        IsStakeholderIn isStakeholderIn = new IsStakeholderIn();

        isStakeholderIn.setActor(actor);
        isStakeholderIn.setFrom(from);
        isStakeholderIn.setTo(to);
        isStakeholderIn.setRole(role);
        isStakeholderIn.setWorkpack(workpack);
        isStakeholderIn.setActive(isActive);
        return isStakeholderIn;
    }

    private CanAccessWorkpack buildCanAccessWorkpack(Person person, Workpack workpack, PermissionDto permissionDto,
                                                     Long id) {
        return new CanAccessWorkpack(id, "", permissionDto.getRole(), permissionDto.getLevel(), person, workpack);
    }
}
