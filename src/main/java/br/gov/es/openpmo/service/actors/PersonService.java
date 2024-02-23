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
import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.dto.person.detail.permissions.CanAccessPlanResultDto;
import br.gov.es.openpmo.dto.person.detail.permissions.PlanResultDto;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;

@Service
public class PersonService {

  private final IsAuthenticatedByService isAuthenticatedByService;

  private final OfficeRepository officeRepository;

  private final PlanRepository planRepository;

  private final IsInContactBookOfService isInContactBookOfService;

  private final PersonRepository repository;


  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public PersonService(
    final PersonRepository repository,
    final IsAuthenticatedByService isAuthenticatedByService,
    final OfficeRepository officeRepository,
    final PlanRepository planRepository,
    final IsInContactBookOfService isInContactBookOfService,
    final IsCCBMemberRepository ccbMemberRepository
  ) {
    this.repository = repository;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.officeRepository = officeRepository;
    this.planRepository = planRepository;
    this.isInContactBookOfService = isInContactBookOfService;
    this.ccbMemberRepository = ccbMemberRepository;
  }

  public Person findById(final Long id) {
    return this.repository.findById(id).orElse(null);
  }

  public Person findByIdThinElseThrow(final Long id) {
    return this.repository.findByIdThin(id)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
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
    final List<PersonListDto> response = streamableQueryResult.stream()
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

    final List<CanAccessOffice> canAccessOffice = officeRepository.findAllCanAccessOfficeByIdPerson(personId, officeId);
    final List<PlanResultDto> listPlans = planRepository.findAllPlanResultByIdOffice(officeId);
    final List<CanAccessPlanResultDto> canAccessPlan = planRepository.findAllCanAccessPlanResultDtoByIdPerson(personId, officeId);
    final List<WorkpackPermissionDetailDto> canAccessWorkpack = repository.findAllWorkpackPermissionDetailDtoByIdPerson(personId, officeId);
    final List<IsStakeholderIn> stakeholderIns = repository.findAllIsStakeholderInByIdPerson(personId, officeId);
    final List<IsCCBMemberFor> isCCBMemberFors = repository.findAllIsCCBMemberForByIdPerson(personId, officeId);

    final Set<Long> idsWorkpack = stakeholderIns
        .stream().map(IsStakeholderIn::getIdWorkpack).filter(idWorkpack -> canAccessWorkpack
        .stream().noneMatch(w -> w.getId().equals(idWorkpack))).collect(
        Collectors.toSet());
    idsWorkpack.addAll(isCCBMemberFors.stream().map(IsCCBMemberFor::getWorkpackId).collect(Collectors.toSet()));
    if (CollectionUtils.isNotEmpty(idsWorkpack)) {
      canAccessWorkpack.addAll(repository.findAllWorkpackPermissionDetailDtoByIdWorkpack(new ArrayList<>(idsWorkpack)));
    }


    final OfficePermissionDetailDto officePermissionDetailDto = this.createOfficeDetail(canAccessOffice, listPlans,
                                                                                        canAccessPlan,
                                                                                        canAccessWorkpack,
                                                                                        stakeholderIns,
                                                                                        isCCBMemberFors);
    officePermissionDetailDto.setId(officeId);

    personDetailDto.setOfficePermission(officePermissionDetailDto);

    return personDetailDto;
  }

  private OfficePermissionDetailDto createOfficeDetail(
      final List<CanAccessOffice> canAccessOffice,
      final List<PlanResultDto> listPlans,
      final List<CanAccessPlanResultDto> canAccessPlan,
      final List<WorkpackPermissionDetailDto> canAccessWorkpack,
      final List<IsStakeholderIn> stakeholderIns,
      final List<IsCCBMemberFor> isCCBMemberFors) {
    OfficePermissionDetailDto dto = new OfficePermissionDetailDto();
    if (CollectionUtils.isNotEmpty(canAccessOffice)) {
      if (canAccessOffice.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getPermissionLevel()))) {
        dto.setAccessLevel(PermissionLevelEnum.EDIT);
      } else if (canAccessOffice.stream().anyMatch(c -> PermissionLevelEnum.READ.equals(c.getPermissionLevel()))) {
        dto.setAccessLevel(PermissionLevelEnum.READ);
      }
    }
    listPlans.forEach(plan -> {
      PlanPermissionDetailDto planDto = new PlanPermissionDetailDto();
      planDto.setId(plan.getId());
      planDto.setName(plan.getName());
      boolean add = false;
      if (CollectionUtils.isNotEmpty(canAccessPlan)) {
        List<CanAccessPlanResultDto> cap = canAccessPlan.stream().filter(c -> c.getIdPlan().equals(plan.getId())).collect(
            Collectors.toList());
        if (CollectionUtils.isNotEmpty(cap)) {
          if (cap.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getPermissionLevel()))) {
            planDto.setAccessLevel(PermissionLevelEnum.EDIT);
            add = true;
          }
          if ( !add && cap.stream().anyMatch(c -> PermissionLevelEnum.READ.equals(c.getPermissionLevel()))) {
            planDto.setAccessLevel(PermissionLevelEnum.READ);
            add = true;
          }
        }
      }
      List<WorkpackPermissionDetailDto> caw = canAccessWorkpack.stream().filter(w -> w.getIdPlan().equals(plan.getId())).collect(
          Collectors.toList());
      if (CollectionUtils.isNotEmpty(caw)) {
        add = true;
        for (WorkpackPermissionDetailDto worpackPermission : caw) {
          worpackPermission.setRoles(stakeholderIns.stream().filter(st -> st.getIdWorkpack().equals(worpackPermission.getId())).map(IsStakeholderIn::getRole).collect(
              Collectors.toList()));
          if (PermissionLevelEnum.NONE.equals(worpackPermission.getAccessLevel())) {
            worpackPermission.setCcbMember(isCCBMemberFors.stream().anyMatch(c -> c.getWorkpackId().equals(worpackPermission.getId())));
          }
          planDto.getWorkpacksPermission().add(worpackPermission);
        }
      }
      if (add) {
        dto.getPlanPermissions().add(planDto);
      }
    });
    return dto;
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
