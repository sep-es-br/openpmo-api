package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficePermissionByIdPersonUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficePermissionUsingCustomFilter;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.IsInContactBookOfService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_PERMISSION_NOT_FOUND;

@Service
public class OfficePermissionService {

  private final OfficePermissionRepository repository;
  private final CustomFilterRepository customFilterRepository;
  private final OfficeService officeService;
  private final PersonService personService;
  private final FindAllOfficePermissionUsingCustomFilter findAllOfficePermission;
  private final FindAllOfficePermissionByIdPersonUsingCustomFilter findAllOfficePermissionByIdPerson;
  private final IsInContactBookOfService isInContactBookOfService;
  private final IsAuthenticatedByService isAuthenticatedByService;
  private final IRemoteRolesFetcher remoteRolesFetcher;

  @Autowired
  public OfficePermissionService(
    final OfficePermissionRepository repository,
    final CustomFilterRepository customFilterRepository,
    final OfficeService officeService,
    final PersonService personService,
    final FindAllOfficePermissionUsingCustomFilter findALlOfficePermission,
    final FindAllOfficePermissionByIdPersonUsingCustomFilter findAllOfficePermissionByIdPerson,
    final IsInContactBookOfService isInContactBookOfService,
    final IsAuthenticatedByService isAuthenticatedByService,
    final IRemoteRolesFetcher remoteRolesFetcher
  ) {
    this.repository = repository;
    this.customFilterRepository = customFilterRepository;
    this.officeService = officeService;
    this.personService = personService;
    this.findAllOfficePermission = findALlOfficePermission;
    this.findAllOfficePermissionByIdPerson = findAllOfficePermissionByIdPerson;
    this.isInContactBookOfService = isInContactBookOfService;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.remoteRolesFetcher = remoteRolesFetcher;
  }

  public void delete(final Long idOffice, final String email) {
    final Person person = this.personService.findByEmail(email)
      .orElseThrow(() -> new RegistroNaoEncontradoException(OFFICE_PERMISSION_NOT_FOUND));

    final List<CanAccessOffice> permissionsToDelete = this.findByOfficeAndPerson(idOffice, person.getId());

    this.repository.deleteAll(permissionsToDelete);
  }

  public List<CanAccessOffice> findByOfficeAndPerson(final Long idOffice, final Long idPerson) {
    return this.repository.findByIdOfficeAndIdPerson(idOffice, idPerson);
  }

  public List<OfficePermissionDto> findAllDto(final Long idOffice, final Long idFilter, final String email) {
    final List<OfficePermissionDto> allPermissionsOfOffice = new ArrayList<>();

    final Office office = this.officeService.findById(idOffice);
    final List<Person> listPerson = this.personService.personInCanAccessOffice(idOffice);
    final List<CanAccessOffice> listOfficesPermission = this.listOfficesPermissions(office, email, idFilter);

    for(final Person person : listPerson) {
      final OfficePermissionDto officePermissionItem = new OfficePermissionDto();

      final List<CanAccessOffice> permissionsFilteredByPerson = listOfficesPermission.stream()
        .filter(permission -> permission.getPerson().equals(person))
        .collect(Collectors.toList());

      this.fillPersonDto(idOffice, person, officePermissionItem);

      officePermissionItem.setIdOffice(idOffice);

      this.fillPermissions(officePermissionItem, permissionsFilteredByPerson);

      allPermissionsOfOffice.add(officePermissionItem);
    }

    if(email != null) {
      return allPermissionsOfOffice.stream().filter(permission -> {
        if(permission.getPerson().getEmail() == null) {
          return true;
        }
        return permission.getPerson().getEmail().equals(email);
      }).collect(Collectors.toList());
    }

    return allPermissionsOfOffice;
  }

  private void fillPermissions(
    final OfficePermissionDto officePermissionItem,
    final List<CanAccessOffice> permissionsFilteredByPerson
  ) {
    final List<PermissionDto> permissions = permissionsFilteredByPerson.stream()
      .map(PermissionDto::of)
      .collect(Collectors.toList());

    officePermissionItem.setPermissions(permissions);
  }

  private void fillPersonDto(final Long idOffice, final Person person, final OfficePermissionDto officePermissionDto) {
    final Optional<IsInContactBookOf> maybeContact = this.isInContactBookOfService.findContactInformationUsingPersonIdAndOffice(
      person.getId(),
      idOffice
    );

    final Optional<IsAuthenticatedBy> maybeAuthenticatedBy = this.isAuthenticatedByService.findAuthenticatedBy(person.getId());

    officePermissionDto.setPerson(PersonDto.from(
      person,
      maybeContact,
      maybeAuthenticatedBy
    ));
  }

  private List<CanAccessOffice> listOfficesPermissions(final Office office, final String email, final Long idFilter) {

    if(idFilter == null) {
      return this.listOfficesPermissions(office, email);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", office.getId());

    if(email == null || email.isEmpty()) {
      return this.findAllOfficePermission.execute(filter, params);
    }

    final Person person = this.personService.findPersonByEmail(email);

    params.put("idPerson", person.getId());

    return this.findAllOfficePermissionByIdPerson.execute(filter, params);
  }

  private List<CanAccessOffice> listOfficesPermissions(final Office office, final String email) {
    if(email == null || email.isEmpty()) {
      return this.repository.findByIdOffice(office.getId());
    }

    final Person person = this.personService.findPersonByEmail(email);

    return this.findByOfficeAndPerson(office.getId(), person.getId());
  }

  public CanAccessOffice findById(final Long id) {
    return this.repository.findById(id).orElseThrow(
      () -> new NegocioException(OFFICE_PERMISSION_NOT_FOUND));
  }

  public void deleteAll(final Set<CanAccessOffice> permissions) {
    this.repository.deleteAll(permissions);
  }

  public void update(final OfficePermissionParamDto request) {
    final Person person = this.returnPersonOrCreateIfNotExists(
      request.getEmail(),
      request.getPerson(),
      request.getIdOffice()
    );
    final Office office = this.officeService.findById(request.getIdOffice());
    final List<CanAccessOffice> officesPermissionsDataBase = this.findByOfficeAndPerson(office.getId(), person.getId());

    officesPermissionsDataBase.forEach(permissionDatabase -> {
      if(request.getPermissions() == null || request.getPermissions().stream().noneMatch(
        rp -> rp.getRole().equals(permissionDatabase.getRole()))) {
        this.delete(permissionDatabase);
      }
    });
    if(!CollectionUtils.isEmpty(request.getPermissions())) {
      request.getPermissions().forEach(permission -> {
        if(permission.getId() == null && officesPermissionsDataBase.stream().noneMatch(
          pbd -> permission.getRole() != null && permission.getRole().equals(pbd.getRole()))) {
          this.save(this.buildCanAccessOffice(person, office, permission, null));
          return;
        }
        final Optional<CanAccessOffice> optionalCanAccessOffice = officesPermissionsDataBase.stream().filter(
          pbd -> permission.getRole() != null && permission.getRole().equals(
            pbd.getRole())).findFirst();
        if(optionalCanAccessOffice.isPresent()) {
          this.save(this.buildCanAccessOffice(
            person,
            office,
            permission,
            optionalCanAccessOffice.get().getId()
          ));
          return;
        }
        if(permission.getId() != null) {
          final CanAccessOffice canAccessOffice = this.repository.findById(permission.getId()).orElseThrow(
            () -> new RegistroNaoEncontradoException(OFFICE_PERMISSION_NOT_FOUND));
          this.save(this.buildCanAccessOffice(person, office, permission, canAccessOffice.getId()));
        }
      });

    }

  }

  private Person returnPersonOrCreateIfNotExists(final String email, final PersonDto person, final Long idOffice) {
    final Optional<Person> personOptional = this.personService.findByEmail(email);
    return personOptional.orElseGet(() -> this.storePerson(email, person, idOffice));
  }

  private Person storePerson(final String email, final PersonDto person, final Long idOffice) {
    return this.personService.savePerson(email, person, idOffice);
  }

  public void delete(final CanAccessOffice office) {
    this.repository.delete(office);
  }

  public CanAccessOffice save(final CanAccessOffice officePermission) {
    return this.repository.save(officePermission);
  }

  private CanAccessOffice buildCanAccessOffice(final Person person, final Office office, final PermissionDto request, final Long id) {
    return new CanAccessOffice(id, "", request.getRole(), request.getLevel(), person, office);
  }

  public Entity store(final OfficePermissionParamDto request) {
    final Person person = this.returnPersonOrCreateIfNotExists(
      request.getEmail(),
      request.getPerson(),
      request.getIdOffice()
    );

    final Office office = this.officeService.findById(request.getIdOffice());

    final List<CanAccessOffice> canAccessOffices = this.findByOfficeAndPerson(office.getId(), person.getId());

    request.getPermissions().forEach(permission -> {
      if(canAccessOffices.stream().noneMatch(c -> c.getRole().equals(permission.getRole()))) {
        this.save(this.buildCanAccessOffice(person, office, permission, null));
      }
    });
    return person;
  }

  public Set<CanAccessOffice> findInheritedPermission(final Long workpackId, final Long personId) {
    return this.repository.findInheritedPermission(workpackId, personId);
  }

  public OfficePermissionDto findOfficePermissionsByEmail(final Long idOffice, final String email) {

    final OfficePermissionDto officePermissionDto = new OfficePermissionDto();

    final Person person = this.personService.findPersonByEmail(email);

    final List<CanAccessOffice> permissions = this.findByOfficeAndPerson(idOffice, person.getId());

    this.fillPersonDto(idOffice, person, officePermissionDto);
    this.fillPermissions(officePermissionDto, permissions);
    this.fillPersonRoles(officePermissionDto, person.getId());

    return officePermissionDto;
  }

  private void fillPersonRoles(final OfficePermissionDto officePermissionDto, final Long personId) {
    final List<RoleResource> roles = this.remoteRolesFetcher.fetch(personId);
    officePermissionDto.addAllRoles(roles);
  }
}
