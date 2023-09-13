package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.person.LocalWorkRequest;
import br.gov.es.openpmo.dto.person.PersonCreateRequest;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.PersonGetByIdDto;
import br.gov.es.openpmo.dto.person.PersonListDto;
import br.gov.es.openpmo.dto.person.PersonUpdateDto;
import br.gov.es.openpmo.dto.person.detail.PersonDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.OfficePermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.PlanPermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.WorkpackPermissionDetailDto;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonPermissionDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.PermissionEntityProvider;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;

@Service
public class PersonService {

  private final IsAuthenticatedByService isAuthenticatedByService;

  private final OfficeRepository officeRepository;

  private final IsInContactBookOfService isInContactBookOfService;

  private final PersonRepository repository;

  private final WorkpackRepository workpackRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  private final HashMap<Long, Boolean> containsCache;

  private final HashMap<Long, String> fontIconCache;

  private final HashMap<Long, String> nameCache;

  @Autowired
  public PersonService(
    final PersonRepository repository,
    final IsAuthenticatedByService isAuthenticatedByService,
    final OfficeRepository officeRepository,
    final IsInContactBookOfService isInContactBookOfService,
    final WorkpackRepository workpackRepository,
    final IsCCBMemberRepository ccbMemberRepository
  ) {
    this.repository = repository;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.officeRepository = officeRepository;
    this.isInContactBookOfService = isInContactBookOfService;
    this.workpackRepository = workpackRepository;
    this.ccbMemberRepository = ccbMemberRepository;
    this.containsCache = new HashMap<>();
    this.fontIconCache = new HashMap<>();
    this.nameCache = new HashMap<>();
  }

  public Person findById(final Long id) {
    return this.repository.findById(id).orElse(null);
  }

  public Person findByIdOrElseThrow(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  public Optional<Person> maybeFindPersonById(final Long id) {
    return this.repository.findById(id);
  }

  public PersonQuery findByIdPersonWithRelationshipAuthServiceAcessoCidadao(final Long id) {
    return this.repository
      .findByIdPersonWithRelationshipAuthServiceAcessoCidadao(id, this.isAuthenticatedByService.defaultAuthServerName())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  public List<Person> personInCanAccessOffice(final Long idOffice) {
    return this.repository.findByIdOfficeReturnDistinctPerson(idOffice);
  }

  public Person save(final Person person) {
    return this.repository.save(person);
  }

  public void createAuthenticationRelationship(
    final String key,
    final String email,
    final String guid,
    final Person person
  ) {
    final AuthService authService = this.isAuthenticatedByService.findDefaultAuthenticationServer();

    final IsAuthenticatedBy isAuthenticatedBy = new IsAuthenticatedBy();
    isAuthenticatedBy.setKey(key);
    isAuthenticatedBy.setEmail(email);
    isAuthenticatedBy.setName(person.getFullName());
    isAuthenticatedBy.setAuthService(authService);
    isAuthenticatedBy.setPerson(person);
    isAuthenticatedBy.setGuid(guid);

    this.isAuthenticatedByService.save(isAuthenticatedBy);
  }

  private void createContactRelationshipUsingEmail(
    final String email,
    final Long idOffice,
    final Person person
  ) {
    final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
    isInContactBookOf.setEmail(email);

    final Office office = this.officeRepository
      .findById(idOffice)
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    isInContactBookOf.setPerson(person);
    isInContactBookOf.setOffice(office);

    this.isInContactBookOfService.save(isInContactBookOf);
  }

  public Person saveZeroDepth(final Person person) {
    return this.repository.save(person, 0);
  }

  public Person savePerson(
    final PersonDto dto,
    final Long idOffice
  ) {
    final Person person = new Person();

    final String name = dto.getName() == null ? this.extractName(dto.getEmail()) : dto.getName();
    final String fullName = dto.getName() == null ? this.extractName(dto.getEmail()) : dto.getFullName();

    person.setName(name);
    person.setFullName(fullName);

    this.save(person);

    this.createAuthenticationRelationship(dto.getKey(), dto.getEmail(), dto.getGuid(), person);

    if (dto.hasAnyContactInformationData()) {
      this.createContactRelationshipUsingDto(idOffice, person, dto);
    } else {
      this.createContactRelationshipUsingEmail(dto.getEmail(), idOffice, person);
    }

    return person;
  }

  public Page<PersonListDto> findAll(
    final StakeholderFilterEnum stakeholderFilter,
    final UserFilterEnum userFilter,
    final CcbMemberFilterEnum ccbMemberFilter,
    final String name,
    final Long[] scope,
    final Pageable pageable,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final Streamable<Person> streamableQueryResult = this.repository.findAllFilteringBy(
      stakeholderFilter,
      userFilter,
      ccbMemberFilter,
      name.isEmpty() ? null : name,
      scope
    );
    final long total = streamableQueryResult.stream().count();
    final List<PersonListDto> response = streamableQueryResult.stream().parallel()
      .skip(pageable.getOffset())
      .limit(pageable.getPageSize())
      .map(query -> PersonListDto.of(query, uriComponentsBuilder))
      .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    return new PageImpl<>(response, pageable, total);
  }

  public PersonDetailDto findPersonDetailsById(
    final Long personId,
    final Long officeId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {

    final Optional<PersonDetailQuery> maybeQuery = this.repository.findPersonDetailsBy(personId, officeId);

    if (!maybeQuery.isPresent()) {
      return null;
    }

    final PersonDetailDto personDetailDto = new PersonDetailDto(maybeQuery.get(), uriComponentsBuilder);

    final List<PersonPermissionDetailQuery> permissions = new ArrayList<>(this.repository.findPermissions(
      personId,
      officeId
    ));

    if (permissions.isEmpty()) {
      return personDetailDto;
    }

    final OfficePermissionDetailDto officePermissionDetailDto = this.createOfficeDetail(permissions);

    personDetailDto.setOfficePermission(officePermissionDetailDto);

    return personDetailDto;
  }

  @Transactional
  public Person update(final PersonUpdateDto personUpdateDto) {
    final Person personToUpdate = this.repository.findById(personUpdateDto.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

    this.repository.save(personToUpdate, 0);
    personToUpdate.setName(personUpdateDto.getName());

    this.updateContactBook(personUpdateDto, personToUpdate);

    if (personUpdateDto.getUnify()) {
      this.unifyContactInformationsInAllOffices(personUpdateDto);
    }

    return personToUpdate;
  }

  public List<PersonDto> findPersonsByFullNameAndWorkpack(
    final String partialName,
    final Long idWorkpack
  ) {
    final Optional<PersonByFullNameQuery> maybeQueryResult =
      this.repository.findPersonsInOfficeByFullName(partialName, idWorkpack);

    if (maybeQueryResult.isPresent()) {
      final PersonByFullNameQuery query = maybeQueryResult.get();
      return query.getPersons()
        .stream()
        .map(person -> PersonDto.from(person, query.getContacts()))
        .collect(java.util.stream.Collectors.toList());
    }

    return Collections.emptyList();
  }

  public void findPersonByFullName(
    final String fullName,
    final Long idWorkpack
  ) {
    final Optional<Person> personByFullName = this.repository.findPersonByFullName(fullName, idWorkpack);
    if (personByFullName.isPresent()) {
      throw new NegocioException(ApplicationMessage.PERSON_ALREADY_EXISTS);
    }
  }

  public boolean existsPersonByFullName(
    final String fullName,
    final Long idWorkpack
  ) {
    return this.repository.existsPersonByFullName(fullName, idWorkpack);
  }

  public Optional<PersonGetByIdDto> maybeFindPersonDataByKey(
    final String key,
    final Long idOffice,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.repository.findByKey(key)
      .map(person -> this.getPersonGetByIdDto(idOffice, uriComponentsBuilder, person));
  }

  public List<ComboDto> findOfficesByPersonId(final Long personId) {
    return this.repository.findOfficesByPersonId(personId)
      .stream()
      .map(office -> new ComboDto(office.getId(), office.getName()))
      .collect(Collectors.toList());
  }

  public Person create(final PersonCreateRequest request) {
    final Person person = new Person();
    person.setName(request.getName());
    person.setFullName(request.getFullName());
    person.setAdministrator(request.getAdministrator());
    this.repository.save(person);
    this.createAuthenticationRelationship(request.getKey(), request.getEmail(), null, person);
    return person;
  }

  public Set<Person> findAllById(final Iterable<Long> responsible) {
    final Set<Person> set = new HashSet<>();
    if (responsible == null) {
      return set;
    }
    final Iterable<Person> personIterable = this.repository.findAllById(responsible);
    for (final Person person : personIterable) {
      set.add(person);
    }
    return set;
  }

  public void updateName(
    final Long idPerson,
    final String name
  ) {
    final Person person = this.repository.findById(idPerson)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

    person.setName(name);
    this.repository.save(person, 0);
  }

  public Optional<Person> findByKey(final String key) {
    return this.repository.findByKey(key);
  }

  public boolean existsByKey(final String key) {
    return this.repository.existsByKey(key);
  }

  public Person findPersonByKey(final String key) {
    return this.findByKey(key)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  private String extractName(final String email) {
    final String[] name = email.split("@");
    return name.length == 0 ? email : name[0];
  }

  private void createContactRelationshipUsingDto(
    final Long idOffice,
    final Person person,
    final PersonDto dto
  ) {
    final IsInContactBookOf isInContactBookOf = new IsInContactBookOf(dto);

    final Office office = this.officeRepository
      .findById(idOffice)
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    isInContactBookOf.setPerson(person);
    isInContactBookOf.setOffice(office);

    this.isInContactBookOfService.save(isInContactBookOf);
  }

  private OfficePermissionDetailDto createOfficeDetail(final List<PersonPermissionDetailQuery> permissions) {
    this.containsCache.clear();
    this.fontIconCache.clear();
    this.nameCache.clear();

    final PersonPermissionDetailQuery permission = permissions.get(0);
    final OfficePermissionDetailDto result = new OfficePermissionDetailDto();

    final Set<CanAccessOffice> canAccessOffices = permissions.stream()
      .map(PersonPermissionDetailQuery::getCanAccessOffice)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());

    final BitSet toRemove = new BitSet();
    final List<PlanPermissionDetailDto> planDetail = this.createPlanDetail(permissions, toRemove);

    if (canAccessOffices.isEmpty()) {
      final Set<CanAccessPlan> canAccessPlan = permissions.stream()
        .map(PersonPermissionDetailQuery::getCanAccessPlan)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

      if (canAccessPlan.isEmpty()) {
        if (planDetail.size() == 1) {
          result.setAccessLevel(planDetail.get(0).getAccessLevel());
        } else {
          final Set<PermissionLevelEnum> permissionLevels = planDetail.stream()
            .map(PlanPermissionDetailDto::getAccessLevel)
            .collect(Collectors.toSet());

          if (permissionLevels.contains(PermissionLevelEnum.BASIC_READ)) {
            result.setAccessLevel(PermissionLevelEnum.BASIC_READ);
          } else {
            result.setAccessLevel(PermissionLevelEnum.NONE);
          }
        }
      } else {
        result.setAccessLevel(PermissionLevelEnum.BASIC_READ);
      }
    } else {
      final PermissionLevelEnum accessLevel = canAccessOffices.stream()
        .map(CanAccessOffice::getPermissionLevel)
        .max(PermissionLevelEnum::compareTo)
        .orElse(null);

      result.setAccessLevel(accessLevel);
    }

    result.setId(permission.getOffice().getId());
    result.setPlanPermissions(planDetail);

    result.removeBasicRead();
    return result;
  }

  private List<PlanPermissionDetailDto> createPlanDetail(
    final List<PersonPermissionDetailQuery> permissions,
    final BitSet toRemove
  ) {
    final List<PlanPermissionDetailDto> result = new ArrayList<>();

    for (final PersonPermissionDetailQuery permission : permissions) {
      final Plan plan = permission.getPlan();

      if (plan == null || this.contains(plan, result, PlanPermissionDetailDto::getId)) {
        continue;
      }

      final PlanPermissionDetailDto item = new PlanPermissionDetailDto();

      final Set<CanAccessPlan> canAccessPlan = this.extractFrom(
        plan,
        permissions,
        PersonPermissionDetailQuery::getPlan,
        PersonPermissionDetailQuery::getCanAccessPlan,
        null
      );

      final List<WorkpackPermissionDetailDto> workpackDetail = this.createWorkpackDetail(plan, permissions, toRemove);

      if (canAccessPlan.isEmpty()) {
        final boolean allNone = workpackDetail.stream()
          .map(WorkpackPermissionDetailDto::getAccessLevel)
          .allMatch(accessLevel -> accessLevel.equals(PermissionLevelEnum.NONE));

        if (allNone) {
          item.setAccessLevel(PermissionLevelEnum.NONE);
        } else {
          item.setAccessLevel(PermissionLevelEnum.BASIC_READ);
        }
      } else {
        final PermissionLevelEnum accessLevel = canAccessPlan.stream()
          .map(CanAccessPlan::getPermissionLevel)
          .max(PermissionLevelEnum::compareTo)
          .orElse(null);

        item.setAccessLevel(accessLevel);
      }

      item.setId(plan.getId());
      item.setName(plan.getName());
      item.setWorkpacksPermission(workpackDetail);

      final boolean skip = item.getAccessLevel() == PermissionLevelEnum.NONE && workpackDetail.isEmpty();

      if (!skip) {
        result.add(item);
      }
    }

    return result;
  }

  private List<WorkpackPermissionDetailDto> createWorkpackDetail(
    final Plan plan,
    final List<PersonPermissionDetailQuery> permissions,
    final BitSet toRemove
  ) {
    final List<WorkpackPermissionDetailDto> result = new ArrayList<>();

    final Set<Workpack> workpacks = this.extractFrom(
      plan,
      permissions,
      PersonPermissionDetailQuery::getPlan,
      PersonPermissionDetailQuery::getWorkpack,
      toRemove
    );

    final List<PersonPermissionDetailQuery> permissionsFiltered = permissions.stream()
      .filter(
        personPermissionDetailQuery ->
          personPermissionDetailQuery.getCanAccessWorkpack() != null
            || personPermissionDetailQuery.getIsStakeholderIn() != null
            || personPermissionDetailQuery.getIsCCBMemberFor() != null
      )
      .collect(Collectors.toList());

    for (final PersonPermissionDetailQuery permission : permissionsFiltered) {
      final Workpack workpack = permission.getWorkpack();

      if (workpack == null
        || this.contains(workpack, result, WorkpackPermissionDetailDto::getId)
        || !workpacks.contains(workpack)
      ) {
        continue;
      }

      final Set<CanAccessWorkpack> canAccessWorkpacks = this.extractFrom(
        workpack,
        permissions,
        PersonPermissionDetailQuery::getWorkpack,
        PersonPermissionDetailQuery::getCanAccessWorkpack,
        null
      );

      final Set<IsCCBMemberFor> isCCBMemberFors = this.extractFrom(
        workpack,
        permissions,
        PersonPermissionDetailQuery::getWorkpack,
        PersonPermissionDetailQuery::getIsCCBMemberFor,
        null
      );

      final Set<IsStakeholderIn> isStakeholderIns = this.extractFrom(
        workpack,
        permissions,
        PersonPermissionDetailQuery::getWorkpack,
        PersonPermissionDetailQuery::getIsStakeholderIn,
        null
      );

      final WorkpackPermissionDetailDto item = new WorkpackPermissionDetailDto();
      item.setId(workpack.getId());
      item.setCcbMember(Boolean.FALSE);

      if (canAccessWorkpacks.isEmpty()) {
        if (!isStakeholderIns.isEmpty()) {
          item.setName(this.getName(workpack));
          item.setRoles(this.getRoles(isStakeholderIns));
          item.setIcon(this.getFontIcon(workpack));
          item.setAccessLevel(PermissionLevelEnum.NONE);
          result.add(item);
        }
      } else {
        final PermissionLevelEnum accessLevel = canAccessWorkpacks.stream()
          .map(CanAccessWorkpack::getPermissionLevel)
          .max(PermissionLevelEnum::compareTo)
          .orElse(null);

        item.setName(this.getName(workpack));
        item.setRoles(this.getRoles(isStakeholderIns));
        item.setIcon(this.getFontIcon(workpack));
        item.setAccessLevel(accessLevel);
        result.add(item);
      }

      if (!isCCBMemberFors.isEmpty()) {
        final WorkpackPermissionDetailDto item2 = new WorkpackPermissionDetailDto();
        item2.setId(workpack.getId());
        item2.setName(this.getName(workpack));
        item2.setRoles(null);
        item2.setIcon(this.getFontIcon(workpack));
        item2.setCcbMember(Boolean.TRUE);
        item2.setAccessLevel(PermissionLevelEnum.NONE);
        result.add(item2);
      }
    }

    return result;
  }

  private String getFontIcon(final Workpack workpack) {
    final Long id = workpack.getId();
    final String cache = this.fontIconCache.get(id);
    if (cache != null) {
      return cache;
    }
    final String fontIcon = this.workpackRepository.findWorkpackModelByWorkpackId(id)
      .map(WorkpackModel::getFontIcon)
      .orElse(null);
    this.fontIconCache.put(id, fontIcon);
    return fontIcon;
  }

  private List<String> getRoles(final Collection<IsStakeholderIn> isStakeholderIns) {
    return isStakeholderIns.stream()
      .map(IsStakeholderIn::getRole)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  private String getName(final Workpack workpack) {
    final Long id = workpack.getId();
    final String cache = this.nameCache.get(id);
    if (cache != null) {
      return cache;
    }
    final String name = this.workpackRepository.findWorkpackNameAndFullname(id)
      .map(WorkpackName::getName)
      .orElse(null);
    this.nameCache.put(id, name);
    return name;
  }

  private <T> boolean contains(
    final Entity entity,
    final Iterable<T> collection,
    final ToLongFunction<T> longFunction
  ) {
    final Long id = entity.getId();
    final Boolean hasCache = this.containsCache.get(id);
    if (hasCache != null) {
      return true;
    }
    for (final T element : collection) {
      if (Objects.equals(id, longFunction.applyAsLong(element))) {
        this.containsCache.put(id, true);
        return true;
      }
    }
    return false;
  }

  private <R> Set<R> extractFrom(
    @NotNull final Entity entity,
    final List<PersonPermissionDetailQuery> permissions,
    final Function<PersonPermissionDetailQuery, Entity> entitySupplier,
    final Function<PersonPermissionDetailQuery, R> relationshipSupplier,
    final BitSet toRemove
  ) {
    final Long id = entity.getId();
    final Set<R> result = new HashSet<>();

    for (int i = 0, permissionsSize = permissions.size(); i < permissionsSize; i++) {
      if (toRemove != null && toRemove.get(i)) continue;
      final PersonPermissionDetailQuery permission = permissions.get(i);
      final Entity other = entitySupplier.apply(permission);
      if (other == null) {
        continue;
      }
      final boolean isSame = Objects.equals(id, other.getId());
      final R relationship = relationshipSupplier.apply(permission);
      if (isSame && relationship != null) {
        if (relationship instanceof PermissionEntityProvider) {
          if (entity == ((PermissionEntityProvider) relationship).getPermissionEntity()) {
            result.add(relationship);
            if (toRemove != null) toRemove.set(i);
          }
        } else {
          result.add(relationship);
          if (toRemove != null) toRemove.set(i);
        }
      }
    }

    return result;
  }

  private void updateContactBook(
    final PersonUpdateDto personUpdateDto,
    final Person person
  ) {
    final Optional<IsInContactBookOf> personContactData = this.repository
      .findContactBookBy(personUpdateDto.getId(), personUpdateDto.getIdOffice());

    if (personContactData.isPresent()) {
      personContactData.get().update(personUpdateDto);
      this.isInContactBookOfService.save(personContactData.get());
      return;
    }

    if (!person.getAdministrator()) {
      throw new NegocioException(ApplicationMessage.CONTACT_DATA_NOT_FOUND);
    }

    final Office office = this.officeRepository.findById(personUpdateDto.getIdOffice())
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
    isInContactBookOf.setEmail(personUpdateDto.getContactEmail());
    isInContactBookOf.setAddress(personUpdateDto.getAddress());
    isInContactBookOf.setPhoneNumber(personUpdateDto.getPhoneNumber());
    isInContactBookOf.setOffice(office);
    isInContactBookOf.setPerson(person);
    this.isInContactBookOfService.save(isInContactBookOf);
  }

  private void unifyContactInformationsInAllOffices(final PersonUpdateDto personUpdateDto) {
    final Set<IsInContactBookOf> allContactInformationByPersonId = this.repository.findAllContactInformationByPersonId(
      personUpdateDto.getId());

    allContactInformationByPersonId.forEach(contact -> contact.update(personUpdateDto));

    this.isInContactBookOfService.saveAll(allContactInformationByPersonId);
  }

  private PersonGetByIdDto getPersonGetByIdDto(
    final Long idOffice,
    final UriComponentsBuilder uriComponentsBuilder,
    final Person person
  ) {
    final Optional<IsInContactBookOf> maybeContact =
      this.isInContactBookOfService.findContactInformationUsingPersonIdAndOffice(person.getId(), idOffice);

    final PersonGetByIdDto personGetByIdDto = PersonGetByIdDto.from(person, maybeContact, uriComponentsBuilder);
    personGetByIdDto.setCcbMember(this.ccbMemberRepository.isActive(person.getId()));
    return personGetByIdDto;
  }

  public void updateLocalWork(Long idPerson, LocalWorkRequest request) {
    final Person person = this.repository.findById(idPerson)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

    person.setIdOffice(request.getIdOffice());
    person.setIdPlan(request.getIdPlan());
    person.setIdWorkpack(request.getIdWorkpack());
    person.setIdWorkpackModelLinked(request.getIdWorkpackModelLinked());
    this.repository.save(person, 0);
  }
}
