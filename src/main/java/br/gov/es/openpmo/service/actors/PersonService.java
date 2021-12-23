package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.person.PersonCreateRequest;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.PersonGetByIdDto;
import br.gov.es.openpmo.dto.person.PersonListDto;
import br.gov.es.openpmo.dto.person.PersonUpdateDto;
import br.gov.es.openpmo.dto.person.detail.PersonDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.OfficePermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.PlanPermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.WorkpackPermissionDetailDto;
import br.gov.es.openpmo.dto.person.queries.PersonAndEmailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.dto.person.queries.WorkpackPermissionAndStakeholderQuery;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;

@Service
public class PersonService {

  private final TokenService tokenService;

  private final IsAuthenticatedByService isAuthenticatedByService;

  private final OfficeRepository officeRepository;

  private final IsInContactBookOfService isInContactBookOfService;

  private final PersonRepository repository;

  private final WorkpackRepository workpackRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  @Value("${users.administrators}")
  private List<String> administrators;

  @Autowired
  public PersonService(
    final PersonRepository repository,
    final TokenService tokenService,
    final IsAuthenticatedByService isAuthenticatedByService,
    final OfficeRepository officeRepository,
    final IsInContactBookOfService isInContactBookOfService,
    final WorkpackRepository workpackRepository,
    final IsCCBMemberRepository ccbMemberRepository
  ) {
    this.tokenService = tokenService;
    this.repository = repository;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.officeRepository = officeRepository;
    this.isInContactBookOfService = isInContactBookOfService;
    this.workpackRepository = workpackRepository;
    this.ccbMemberRepository = ccbMemberRepository;
  }

  public Person findByAuthorizationHeader(final String authorizationHeader) {

    final Claims user = this.tokenService.getUser(authorizationHeader.split(" ")[1], TokenType.AUTHENTICATION);

    final String email = (String) user.get("email");

    return this.repository.findByEmail(email)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  public Optional<Person> findByEmail(final String email) {
    return this.repository.findByEmail(email);
  }

  public Person findPersonByEmail(final String email) {
    return this.repository.findByEmail(email)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
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

  public PersonAndEmailQuery findByIdPersonWithRelationshipAuthServiceAcessoCidadao(final Long id) {
    return this.repository.findByIdPersonWithRelationshipAuthServiceAcessoCidadao(
      id,
      this.isAuthenticatedByService.defaultAuthServerName()
    ).orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  public List<Person> personInCanAccessOffice(final Long idOffice) {
    return this.repository.findByIdOfficeReturnDistinctPerson(idOffice);
  }

  public List<Person> personInCanAccessPlan(final Long idPlan) {
    return this.repository.findByIdPlanReturnDistinctPerson(idPlan);
  }

  public List<Person> personInIsStakeholderIn(final Long idWorkpack) {
    return this.repository.findByIdWorkpackReturnDistinctPerson(idWorkpack);
  }

  public Person savePersonByEmail(final String email, final Long idOffice) {
    final String[] name = email.split("@");
    final Person person = new Person();

    person.setAdministrator(this.administrators.contains(email));
    person.setName(name.length == 0 ? email : name[0]);
    person.setFullName(name.length == 0 ? email : name[0]);
    this.save(person);
    this.createAuthenticationRelationship(email, null, person);

    if(idOffice != null) {
      this.createContactRelationshipUsingEmail(email, idOffice, person);
    }
    return person;
  }

  public Person save(final Person person) {
    return this.repository.save(person);
  }

  public void createAuthenticationRelationship(final String email, final String guid, final Person person) {
    final AuthService authService = this.isAuthenticatedByService.findDefaultAuthenticationServer();

    final IsAuthenticatedBy isAuthenticatedBy = new IsAuthenticatedBy();
    isAuthenticatedBy.setEmail(email);
    isAuthenticatedBy.setName(person.getFullName());
    isAuthenticatedBy.setAuthService(authService);
    isAuthenticatedBy.setPerson(person);
    isAuthenticatedBy.setGuid(guid);

    this.isAuthenticatedByService.save(isAuthenticatedBy);
  }

  private void createContactRelationshipUsingEmail(final String email, final Long idOffice, final Person person) {
    final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
    isInContactBookOf.setEmail(email);

    final Office office = this.officeRepository
      .findById(idOffice)
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    isInContactBookOf.setPerson(person);
    isInContactBookOf.setOffice(office);

    this.isInContactBookOfService.saveZeroDepth(isInContactBookOf);
  }

  public Person saveZeroDepth(final Person person) {
    return this.repository.save(person, 0);
  }

  public Person savePerson(final String email, final PersonDto dto, final Long idOffice) {
    final Person person = new Person();

    final String name = dto.getName() == null ? this.extractName(email) : dto.getName();
    final String fullName = dto.getName() == null ? this.extractName(email) : dto.getFullName();

    person.setAdministrator(this.administrators.contains(email));
    person.setName(name);
    person.setFullName(fullName);

    this.save(person);

    this.createAuthenticationRelationship(email, dto.getGuid(), person);

    if(dto.hasAnyContactInformationData()) {
      this.createContactRelationshipUsingDto(idOffice, person, dto);
    }
    else {
      this.createContactRelationshipUsingEmail(email, idOffice, person);
    }

    return person;
  }

  private String extractName(final String email) {
    final String[] name = email.split("@");
    return name.length == 0 ? email : name[0];
  }

  private void createContactRelationshipUsingDto(final Long idOffice, final Person person, final PersonDto dto) {
    final IsInContactBookOf isInContactBookOf = new IsInContactBookOf(dto);

    final Office office = this.officeRepository
      .findById(idOffice)
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    isInContactBookOf.setPerson(person);
    isInContactBookOf.setOffice(office);

    this.isInContactBookOfService.save(isInContactBookOf);
  }

  public List<PersonListDto> findAll(
    final StakeholderFilterEnum stakeholderFilter,
    final UserFilterEnum userFilter,
    final String name,
    final Long officeScope,
    final Long[] planScope,
    final Long[] workpackScope,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.repository.findAllFilteringBy(
        stakeholderFilter.toString(),
        userFilter.toString(),
        name,
        officeScope,
        planScope,
        workpackScope
      )
      .stream()
      .map(query -> new PersonListDto(query, uriComponentsBuilder))
      .collect(Collectors.toList());
  }

  public PersonDetailDto findPersonDetailsById(
    final Long personId,
    final Long officeId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {

    final PersonDetailQuery query = this.repository
      .findPersonDetailsBy(personId, officeId)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

    final PersonDetailDto personDetailDto = new PersonDetailDto(query, uriComponentsBuilder);

    final OfficePermissionDetailDto officePermissionDetailDto = this.createOfficeDetail(query);

    personDetailDto.setOfficePermission(officePermissionDetailDto);

    return personDetailDto;
  }

  private OfficePermissionDetailDto createOfficeDetail(final PersonDetailQuery query) {
    final OfficePermissionDetailDto officePermissionDetailDto = new OfficePermissionDetailDto(query.getCanAccessOffice());
    final List<PlanPermissionDetailDto> planPermissionDetailDto = this.createPermissionPlanDetail(query);
    officePermissionDetailDto.setPlanPermissions(planPermissionDetailDto);
    return officePermissionDetailDto;
  }

  private List<PlanPermissionDetailDto> createPermissionPlanDetail(final PersonDetailQuery query) {
    return query.getCanAccessPlans().stream()
      .map(canAccessPlan -> this.mapToPlanPermissionDetailDto(query, canAccessPlan))
      .collect(Collectors.toList());
  }

  private PlanPermissionDetailDto mapToPlanPermissionDetailDto(
    final PersonDetailQuery query,
    final CanAccessPlan canAccessPlan
  ) {
    final PlanPermissionDetailDto planPermissionDetailDto = new PlanPermissionDetailDto();
    planPermissionDetailDto.setId(canAccessPlan.getId());
    planPermissionDetailDto.setAccessLevel(canAccessPlan.getPermissionLevel());
    planPermissionDetailDto.setName(canAccessPlan.getPlanName());

    final List<WorkpackPermissionDetailDto> permissionWorkpackDetail = this.createPermissionWorkpackDetail(
      query.getIdPerson(),
      canAccessPlan.getIdPlan()
    );

    planPermissionDetailDto.setWorkpacksPermission(permissionWorkpackDetail);

    return planPermissionDetailDto;
  }

  private List<WorkpackPermissionDetailDto> createPermissionWorkpackDetail(final Long idPerson, final Long idPlan) {
    final WorkpackPermissionAndStakeholderQuery query = this.workpackRepository.findAllByPersonAndPlan(
      idPerson,
      idPlan
    );

    return query.getWorkpacks().stream()
      .map(workpack -> new WorkpackPermissionDetailDto(workpack, query))
      .collect(Collectors.toList());
  }

  @Transactional
  public Person update(final PersonUpdateDto personUpdateDto) {
    final Person personToUpdate = this.repository.findById(personUpdateDto.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

    this.repository.save(personToUpdate, 0);
    personToUpdate.setName(personUpdateDto.getName());

    this.updateContactBook(personUpdateDto);

    if(personUpdateDto.getUnify()) {
      this.unifyContactInformationsInAllOffices(personUpdateDto);
    }

    return personToUpdate;
  }

  private void updateContactBook(final PersonUpdateDto personUpdateDto) {
    final IsInContactBookOf personContactData = this.repository.findContactBookBy(
      personUpdateDto.getId(),
      personUpdateDto.getIdOffice()
    ).orElseThrow(() -> new NegocioException(ApplicationMessage.CONTACT_DATA_NOT_FOUND));

    personContactData.update(personUpdateDto);

    this.isInContactBookOfService.saveZeroDepth(personContactData);
  }

  private void unifyContactInformationsInAllOffices(final PersonUpdateDto personUpdateDto) {
    final Set<IsInContactBookOf> allContactInformationByPersonId = this.repository.findAllContactInformationByPersonId(
      personUpdateDto.getId());

    allContactInformationByPersonId.forEach(contact -> contact.update(personUpdateDto));

    this.isInContactBookOfService.saveAll(allContactInformationByPersonId);
  }

  public List<PersonDto> findPersonsByFullNameAndWorkpack(final String partialName, final Long idWorkpack) {

    final Optional<PersonByFullNameQuery> maybeQueryResult = this.repository.findPersonsInOfficeByFullName(
      partialName,
      idWorkpack
    );

    if(maybeQueryResult.isPresent()) {
      final PersonByFullNameQuery query = maybeQueryResult.get();
      return query.getPersons()
        .stream()
        .map(person -> PersonDto.from(person, query.getContacts()))
        .collect(java.util.stream.Collectors.toList());
    }

    return null;
  }

  public Optional<Person> findPersonByFullName(final String fullName, final Long idWorkpack) {
    return this.repository.findPersonByFullName(fullName, idWorkpack);
  }

  public Optional<PersonGetByIdDto> maybeFindPersonDataByEmail(
    final String email,
    final Long idOffice,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.repository.findByEmail(email)
      .map(person -> {
        final Optional<IsInContactBookOf> maybeContact = this.isInContactBookOfService.findContactInformationUsingPersonIdAndOffice(
          person.getId(),
          idOffice
        );
        final PersonGetByIdDto personGetByIdDto = PersonGetByIdDto.from(person, maybeContact, uriComponentsBuilder);
        personGetByIdDto.setCcbMember(this.ccbMemberRepository.isActive(person.getId()));
        return personGetByIdDto;
      });
  }

  public List<ComboDto> findOfficesByPersonId(final Long personId) {
    return this.repository.findAllContactInformationByPersonId(personId)
      .stream()
      .map(contact -> new ComboDto(contact.getOfficeId(), contact.getOffice().getName()))
      .collect(Collectors.toList());
  }

  public Person create(final PersonCreateRequest request) {
    final Person person = new Person();
    person.setName(request.getName());
    person.setFullName(request.getFullName());
    person.setAdministrator(request.getAdministrator());

    this.repository.save(person);

    this.createAuthenticationRelationship(request.getEmail(), null, person);

    return person;
  }

  public Set<Person> findAllById(final Iterable<Long> responsible) {
    final Iterable<Person> personIterable = this.repository.findAllById(responsible);
    return StreamSupport.stream(personIterable.spliterator(), false)
      .collect(Collectors.toSet());
  }

}
