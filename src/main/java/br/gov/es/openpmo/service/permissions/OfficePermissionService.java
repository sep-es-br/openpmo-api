package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.journals.JournalAction;
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
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.TextSimilarityScore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  private final RoleService roleService;

  private final AppProperties appProperties;

  private final TextSimilarityScore textSimilarityScore;

  private final JournalCreator journalCreator;

  private final TokenService tokenService;

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
    final IRemoteRolesFetcher remoteRolesFetcher,
    final RoleService roleService,
    final AppProperties appProperties,
    final TextSimilarityScore textSimilarityScore,
    final JournalCreator journalCreator,
    final TokenService tokenService
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
    this.roleService = roleService;
    this.appProperties = appProperties;
    this.textSimilarityScore = textSimilarityScore;
    this.journalCreator = journalCreator;
    this.tokenService = tokenService;
  }

  public void delete(
    final Long idOffice,
    final String key,
    final String authorization
  ) {
    final Person author = this.getPersonByAuthorization(authorization);
    final Person target = this.personService.findByKey(key)
      .orElseThrow(() -> new RegistroNaoEncontradoException(OFFICE_PERMISSION_NOT_FOUND));
    final Office office = this.officeService.findById(idOffice);
    final List<CanAccessOffice> permissionsToDelete = this.findByOfficeAndPerson(idOffice, target.getId());
    this.repository.deleteAll(permissionsToDelete);
    this.journalCreator.officePermission(
      office,
      target,
      author,
      this.getGratherPermissionLevel(permissionsToDelete),
      JournalAction.REMOVED
    );
  }

  private Person getPersonByAuthorization(final String authorization) {
    final Long idAuthor = this.tokenService.getUserId(authorization);
    return this.personService.findById(idAuthor);
  }

  public List<CanAccessOffice> findByOfficeAndPerson(
    final Long idOffice,
    final Long idPerson
  ) {
    return this.repository.findByIdOfficeAndIdPerson(idOffice, idPerson);
  }

  private PermissionLevelEnum getGratherPermissionLevel(final List<? extends CanAccessOffice> permissions) {
    final boolean hasEdit = permissions.stream()
      .map(CanAccessOffice::getPermissionLevel)
      .anyMatch(level -> level.equals(PermissionLevelEnum.EDIT));
    if (hasEdit) return PermissionLevelEnum.EDIT;
    return PermissionLevelEnum.READ;
  }

  public List<OfficePermissionDto> findAllDto(
    final Long idOffice,
    final Long idFilter,
    final String key,
    final Long idPerson,
    final String term
  ) {
    final List<RoleResource> roles = this.roleService.getRolesByKey(idPerson, key);

    final Office office = this.officeService.findById(idOffice);

    final List<Person> listPerson = this.personService.personInCanAccessOffice(idOffice);

    final List<CanAccessOffice> listOfficesPermission = this.listOfficesPermissions(office, key, idFilter);

    final List<OfficePermissionDto> allPermissionsOfOffice = new ArrayList<>();
    for (final Person person : listPerson) {
      final OfficePermissionDto officePermissionItem = new OfficePermissionDto();
      final List<CanAccessOffice> permissionsFilteredByPerson = listOfficesPermission.stream()
        .filter(permission -> permission.getPerson().equals(person))
        .collect(Collectors.toList());
      if (permissionsFilteredByPerson.isEmpty()) {
        continue;
      }
      this.fillPersonDto(idOffice, person, officePermissionItem, roles, term);
      officePermissionItem.setIdOffice(idOffice);
      this.fillPermissions(officePermissionItem, permissionsFilteredByPerson);
      allPermissionsOfOffice.add(officePermissionItem);
    }

    allPermissionsOfOffice.removeIf(dto -> {
      if (StringUtils.isBlank(term)) return false;
      return dto.getPerson().getScore() < this.appProperties.getSearchCutOffScore();
    });

    if (key != null) {
      return allPermissionsOfOffice.stream()
        .filter(permission -> {
          if (permission.getPerson().getKey() == null) return true;
          return permission.getPerson().getKey().equals(key);
        })
        .sorted(Comparator.comparing(p -> p.getPerson().getScore()))
        .collect(Collectors.toList());
    }
    return allPermissionsOfOffice.stream()
      .sorted(Comparator.comparing(p -> p.getPerson().getScore()))
      .collect(Collectors.toList());
  }

  private List<CanAccessOffice> listOfficesPermissions(
    final Office office,
    final String key,
    final Long idFilter
  ) {
    if (idFilter == null) {
      return this.listOfficesPermissions(office, key);
    }

    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", office.getId());

    if (key == null || key.isEmpty()) {
      return this.findAllOfficePermission.execute(filter, params);
    }

    final Person person = this.personService.findPersonByKey(key);

    params.put("idPerson", person.getId());

    return this.findAllOfficePermissionByIdPerson.execute(filter, params);
  }

  private void fillPersonDto(
    final Long idOffice,
    final Person person,
    final OfficePermissionDto officePermissionDto,
    final Collection<RoleResource> roles,
    final String term
  ) {
    final Optional<IsInContactBookOf> maybeContact =
      this.isInContactBookOfService.findContactInformationUsingPersonIdAndOffice(person.getId(), idOffice);
    final Optional<IsAuthenticatedBy> maybeAuthenticatedBy =
      this.isAuthenticatedByService.findAuthenticatedBy(person.getId());
    final PersonDto personDto = PersonDto.from(
      person,
      maybeContact,
      maybeAuthenticatedBy
    );
    if (Objects.nonNull(term)) {
      final double nameScore = this.textSimilarityScore.execute(person.getName(), term);
      final double fullNameScore = this.textSimilarityScore.execute(person.getFullName(), term);
      final double score = Math.max(nameScore, fullNameScore);
      personDto.setScore(score);
    }
    personDto.addAllRoles(roles);
    officePermissionDto.setPerson(personDto);
  }

  private void fillPermissions(
    final OfficePermissionDto officePermissionItem,
    final Collection<CanAccessOffice> permissionsFilteredByPerson
  ) {
    final List<PermissionDto> permissions = permissionsFilteredByPerson.stream()
      .map(PermissionDto::of)
      .collect(Collectors.toList());

    officePermissionItem.setPermissions(permissions);
  }

  private List<CanAccessOffice> listOfficesPermissions(
    final Office office,
    final String key
  ) {
    if (key == null || key.isEmpty()) {
      return this.repository.findByIdOffice(office.getId());
    }
    final Person person = this.personService.findPersonByKey(key);
    return this.findByOfficeAndPerson(office.getId(), person.getId());
  }

  public CanAccessOffice findById(final Long id) {
    return this.repository.findById(id).orElseThrow(
      () -> new NegocioException(OFFICE_PERMISSION_NOT_FOUND));
  }

  public void deleteAll(final Iterable<CanAccessOffice> permissions) {
    this.repository.deleteAll(permissions);
  }

  public void update(
    final OfficePermissionParamDto request,
    final String authorization
  ) {
    final Person author = this.getPersonByAuthorization(authorization);
    final Person target = this.returnPersonOrCreateIfNotExists(
      request.getKey(),
      request.getPerson(),
      request.getIdOffice()
    );
    final Office office = this.officeService.findById(request.getIdOffice());
    final List<CanAccessOffice> officesPermissionsDataBase = this.findByOfficeAndPerson(office.getId(), target.getId());

    officesPermissionsDataBase.forEach(permissionDatabase -> {
      if (request.getPermissions() == null || request.getPermissions().stream().noneMatch(
        rp -> rp.getRole().equals(permissionDatabase.getRole()))) {
        this.delete(permissionDatabase);
      }
    });
    if (request.getPermissions() != null && !(request.getPermissions()).isEmpty()) {
      request.getPermissions().forEach(permission -> {
        if (permission.getId() == null && officesPermissionsDataBase.stream().noneMatch(
          pbd -> permission.getRole() != null && permission.getRole().equals(pbd.getRole()))) {
          this.save(this.buildCanAccessOffice(target, office, permission, null));
          this.journalCreator.officePermission(
            office,
            target,
            author,
            request.getGratherPermissionLevel(),
            JournalAction.EDITED
          );
          return;
        }
        final Optional<CanAccessOffice> optionalCanAccessOffice = officesPermissionsDataBase.stream().filter(
          pbd -> permission.getRole() != null && permission.getRole().equals(
            pbd.getRole())).findFirst();
        if (optionalCanAccessOffice.isPresent()) {
          this.save(this.buildCanAccessOffice(
            target,
            office,
            permission,
            optionalCanAccessOffice.get().getId()
          ));
          return;
        }
        if (permission.getId() != null) {
          final CanAccessOffice canAccessOffice = this.repository.findById(permission.getId()).orElseThrow(
            () -> new RegistroNaoEncontradoException(OFFICE_PERMISSION_NOT_FOUND));
          this.save(this.buildCanAccessOffice(target, office, permission, canAccessOffice.getId()));
        }
      });
      this.journalCreator.officePermission(
        office,
        target,
        author,
        request.getGratherPermissionLevel(),
        JournalAction.EDITED
      );
    }

  }

  private Person returnPersonOrCreateIfNotExists(
    final String key,
    final PersonDto person,
    final Long idOffice
  ) {
    final Optional<Person> personOptional = this.personService.findByKey(key);
    return personOptional.orElseGet(() -> this.storePerson(person, idOffice));
  }

  public void delete(final CanAccessOffice office) {
    this.repository.delete(office);
  }

  public CanAccessOffice save(final CanAccessOffice officePermission) {
    final Person person = officePermission.getPerson();
    final Office office = officePermission.getOffice();

    if (!this.isInContactBookOfService.existsByPersonIdAndOfficeId(person.getId(), office.getId())) {
      final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
      isInContactBookOf.setPerson(person);
      isInContactBookOf.setOffice(office);
      this.isInContactBookOfService.save(isInContactBookOf);
    }

    return this.repository.save(officePermission, 0);
  }

  private CanAccessOffice buildCanAccessOffice(
    final Person person,
    final Office office,
    final PermissionDto request,
    final Long id
  ) {
    return new CanAccessOffice(id, "", request.getRole(), request.getLevel(), person, office);
  }

  private Person storePerson(
    final PersonDto person,
    final Long idOffice
  ) {
    return this.personService.savePerson(person, idOffice);
  }

  public Entity store(
    final OfficePermissionParamDto request,
    final String authorization
  ) {
    final Long idOffice = Optional.of(request)
      .map(OfficePermissionParamDto::getIdOffice)
      .orElse(null);
    final Long idPerson = Optional.of(request)
      .map(OfficePermissionParamDto::getPerson)
      .map(PersonDto::getId)
      .orElse(null);
    if (this.repository.existsByIdWorkpackAndIdPerson(idOffice, idPerson)) {
      throw new NegocioException(ApplicationMessage.ALREADY_EXISTS_PERMISSION);
    }
    final Person author = this.getPersonByAuthorization(authorization);
    final Person target = this.returnPersonOrCreateIfNotExists(
      request.getKey(),
      request.getPerson(),
      request.getIdOffice()
    );
    final Office office = this.officeService.findById(request.getIdOffice());
    final List<CanAccessOffice> canAccessOffices = this.findByOfficeAndPerson(office.getId(), target.getId());
    request.getPermissions().forEach(permission -> {
      if (canAccessOffices.stream().noneMatch(c -> c.getRole().equals(permission.getRole()))) {
        this.save(this.buildCanAccessOffice(target, office, permission, null));
      }
    });
    this.journalCreator.officePermission(
      office,
      target,
      author,
      request.getGratherPermissionLevel(),
      JournalAction.CREATED
    );
    return target;
  }

  public Set<CanAccessOffice> findInheritedPermission(
    final Long workpackId,
    final Long personId
  ) {
    return this.repository.findInheritedPermission(workpackId, personId);
  }

  public OfficePermissionDto findOfficePermissionsByKey(
    final Long idOffice,
    final String key,
    final Long idPerson
  ) {
    final OfficePermissionDto officePermissionDto = new OfficePermissionDto();
    final Person person = this.personService.findPersonByKey(key);
    final List<CanAccessOffice> permissions = this.findByOfficeAndPerson(idOffice, person.getId());

    final List<RoleResource> roles = this.roleService.getRolesByKey(idPerson, key);
    this.fillPersonDto(idOffice, person, officePermissionDto, roles, null);
    this.fillPermissions(officePermissionDto, permissions);
    this.fillPersonRoles(officePermissionDto, person.getId());

    return officePermissionDto;
  }

  private void fillPersonRoles(
    final OfficePermissionDto officePermissionDto,
    final Long personId
  ) {
    final List<RoleResource> roles = this.remoteRolesFetcher.fetch(personId);
    officePermissionDto.addAllRoles(roles);
  }

}
