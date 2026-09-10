package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
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
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficePermissionByIdPersonUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllOfficePermissionUsingCustomFilter;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.IsInContactBookOfService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.baselines.EvaluateBaselineService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.publicidentity.PublicIdentityValidationService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.TextSimilarityScore;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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

  private final IsCCBMemberRepository isCCBMemberRepository;

  private final EvaluateBaselineService evaluateBaselineService;

  private final PublicIdentityValidationService publicIdentityValidationService;

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
    final TokenService tokenService,
    final IsCCBMemberRepository isCCBMemberRepository,
    @Lazy final EvaluateBaselineService evaluateBaselineService,
    final PublicIdentityValidationService publicIdentityValidationService
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
    this.isCCBMemberRepository = isCCBMemberRepository;
    this.evaluateBaselineService = evaluateBaselineService;
    this.publicIdentityValidationService = publicIdentityValidationService;
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
    this.isCCBMemberRepository.deleteAllByPersonIdAndOfficeId(target.getId(), idOffice);
    this.evaluateBaselineService.handlePostMemberDeletion(idOffice);
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
    final List<RoleResource> roles = this.isAuthenticatedByService.isCitizenServerAuthentication()
      ? this.roleService.getRolesByKey(idPerson, key)
      : Collections.emptyList();

    final Office office = this.officeService.findById(idOffice);

    final List<Person> listPerson = new ArrayList<>(this.personService.personInCanAccessOffice(idOffice));
    this.isCCBMemberRepository.findAllPersonsByOfficeId(idOffice).forEach(ccmPerson -> {
      if (listPerson.stream().noneMatch(person -> person.getId().equals(ccmPerson.getId()))) {
        listPerson.add(ccmPerson);
      }
    });

    final List<CanAccessOffice> listOfficesPermission = this.listOfficesPermissions(office, key, idFilter);

    final List<OfficePermissionDto> allPermissionsOfOffice = new ArrayList<>();
    for (final Person person : listPerson) {
      final OfficePermissionDto officePermissionItem = new OfficePermissionDto();
      final List<CanAccessOffice> permissionsFilteredByPerson = listOfficesPermission.stream()
        .filter(permission -> permission.getPerson().equals(person))
        .collect(Collectors.toList());
      if (permissionsFilteredByPerson.isEmpty()
        && !this.isCCBMemberRepository.existsCCMForPersonAndTarget(person.getId(), idOffice)) {
        continue;
      }
      this.fillPersonDto(idOffice, person, officePermissionItem, roles, term);
      officePermissionItem.setIdOffice(idOffice);
      this.fillPermissions(officePermissionItem, permissionsFilteredByPerson, person.getId(), idOffice);
      allPermissionsOfOffice.add(officePermissionItem);
    }

    allPermissionsOfOffice.removeIf(dto -> {
      if (StringUtils.isBlank(term)) return false;
      return dto.getPerson().getScore() < this.appProperties.getSearchCutOffScore();
    });

    allPermissionsOfOffice.removeIf(permission -> permission.getPerson().getKey() == null);

    if (key != null) {
      return allPermissionsOfOffice.stream()
        .filter(permission -> key.equals(permission.getPerson().getKey()))
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
      final double nameScore = person.getName() == null
        ? 0.0
        : this.textSimilarityScore.execute(person.getName(), term);
      final double fullNameScore = person.getFullName() == null
        ? 0.0
        : this.textSimilarityScore.execute(person.getFullName(), term);
      final double score = Math.max(nameScore, fullNameScore);
      personDto.setScore(score);
    }
    personDto.addAllRoles(roles);
    officePermissionDto.setPerson(personDto);
  }

  private void fillPermissions(
    final OfficePermissionDto officePermissionItem,
    final Collection<CanAccessOffice> permissionsFilteredByPerson,
    final Long idPerson,
    final Long idOffice
  ) {
    List<String> ccbRoles = isCCBMemberRepository.findCcbRolesByPersonAndOffice(idPerson, idOffice);
    Set<String> ccbRolesSet = new HashSet<>(ccbRoles);

    final List<PermissionDto> permissions = new ArrayList<>(permissionsFilteredByPerson.stream()
      .map(permission -> {
        PermissionDto dto = PermissionDto.of(permission);
        boolean isCcm = ccbRolesSet.contains(permission.getRole());
        dto.setCcmMember(isCcm);
        return dto;
      })
      .collect(Collectors.toList()));

    ccbRolesSet.stream()
      .filter(role -> permissions.stream().noneMatch(permission -> role.equals(permission.getRole())))
      .forEach(role -> {
        final PermissionDto permission = new PermissionDto();
        permission.setRole(role);
        permission.setLevel(PermissionLevelEnum.NONE);
        permission.setCcmMember(true);
        permissions.add(permission);
      });

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
        rp -> !PermissionLevelEnum.NONE.equals(rp.getLevel())
          && rp.getRole().equals(permissionDatabase.getRole()))) {
        this.delete(permissionDatabase);
      }
    });
    if (request.getPermissions() != null && !(request.getPermissions()).isEmpty()) {
      request.getPermissions().forEach(permission -> {
        if (PermissionLevelEnum.NONE.equals(permission.getLevel())) {
          return;
        }
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
          CanAccessOffice officePermission = this.buildCanAccessOffice(
                  target,
                  office,
                  permission,
                  optionalCanAccessOffice.get().getId()
          );
          this.repository.updateCanAccessOffice(officePermission.getPerson().getId(),
                  officePermission.getOffice().getId(),
                  officePermission.getId(),
                  officePermission.getOrganization(),
                  officePermission.getRole(),
                  officePermission.getPermissionLevel());
          return;
        }
        if (permission.getId() != null) {
          final CanAccessOffice canAccessOffice = this.repository.findById(permission.getId()).orElseThrow(
            () -> new RegistroNaoEncontradoException(OFFICE_PERMISSION_NOT_FOUND));
          CanAccessOffice officePermission = this.buildCanAccessOffice(target, office, permission, canAccessOffice.getId());
          this.repository.updateCanAccessOffice(officePermission.getPerson().getId(),
                  officePermission.getOffice().getId(),
                  officePermission.getId(),
                  officePermission.getOrganization(),
                  officePermission.getRole(),
                  officePermission.getPermissionLevel());
        }
      });
      if (!PermissionLevelEnum.NONE.equals(request.getGratherPermissionLevel())) {
        this.journalCreator.officePermission(
          office,
          target,
          author,
          request.getGratherPermissionLevel(),
          JournalAction.EDITED
        );
      }
    }

    this.isCCBMemberRepository.deleteAllByPersonIdAndOfficeId(target.getId(), request.getIdOffice());

    for (final PermissionDto permission : request.getPermissions()) {

      if (!permission.isCcmMember()) {
        continue;
      }
    
      this.isCCBMemberRepository.createIsCCBMemberForByOffice(
          target.getId(),
          request.getIdOffice(),
          permission.getRole(),
          permission.isCcmMember()
      );
    }

    if(!this.isCCBMemberRepository.existsCCMForPersonAndTarget(target.getId(), request.getIdOffice())){
      this.evaluateBaselineService.handlePostMemberDeletion(request.getIdOffice());
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

    return this.repository.createCanAccessOffice(officePermission.getPerson().getId(),
            officePermission.getOffice().getId(),
            officePermission.getOrganization(),
            officePermission.getRole(),
            officePermission.getPermissionLevel());
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

  public void store(
    final OfficePermissionParamDto request,
    final String authorization
  ) {
    this.publicIdentityValidationService.validate(
      request.getIdentityValidation(),
      request.getKey()
    );
    final Long idOffice = Optional.of(request)
      .map(OfficePermissionParamDto::getIdOffice)
      .orElse(null);
    final Long idPerson = Optional.of(request)
      .map(OfficePermissionParamDto::getPerson)
      .map(PersonDto::getId)
      .orElse(null);
    if (this.repository.existsByIdWorkpackAndIdPerson(idOffice, idPerson)) {
      this.update(request, authorization);
      return;
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
      if (PermissionLevelEnum.NONE.equals(permission.getLevel())) {
        return;
      }
      if (canAccessOffices.stream().noneMatch(c -> c.getRole().equals(permission.getRole()))) {
        this.save(this.buildCanAccessOffice(target, office, permission, null));
      }
    });
    if (!PermissionLevelEnum.NONE.equals(request.getGratherPermissionLevel())) {
      this.journalCreator.officePermission(
        office,
        target,
        author,
        request.getGratherPermissionLevel(),
        JournalAction.CREATED
      );
    }

    for (final PermissionDto permission : request.getPermissions()) {

      if (!permission.isCcmMember()) {
        continue;
      }
    
      this.isCCBMemberRepository.createIsCCBMemberForByOffice(
          target.getId(),
          request.getIdOffice(),
          permission.getRole(),
          permission.isCcmMember()
      );
    }
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

    final List<RoleResource> roles = this.isAuthenticatedByService.isCitizenServerAuthentication()
      ? this.roleService.getRolesByKey(idPerson, key)
      : Collections.emptyList();
    this.fillPersonDto(idOffice, person, officePermissionDto, roles, null);
    this.fillPermissions(officePermissionDto, permissions, person.getId(), idOffice);
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
