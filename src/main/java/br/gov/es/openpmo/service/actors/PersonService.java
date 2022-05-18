package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.person.*;
import br.gov.es.openpmo.dto.person.detail.PersonDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.OfficePermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.PlanPermissionDetailDto;
import br.gov.es.openpmo.dto.person.detail.permissions.WorkpackPermissionDetailDto;
import br.gov.es.openpmo.dto.person.queries.PersonAndEmailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonPermissionDetailQuery;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.enumerator.*;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.*;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        person.setName(name.length == 0 ? email : name[0]);
        person.setFullName(name.length == 0 ? email : name[0]);
        this.save(person);
        this.createAuthenticationRelationship(email, null, person);

        if (idOffice != null) {
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

        this.isInContactBookOfService.save(isInContactBookOf);
    }

    public Person saveZeroDepth(final Person person) {
        return this.repository.save(person, 0);
    }

    public Person savePerson(final String email, final PersonDto dto, final Long idOffice) {
        final Person person = new Person();

        final String name = dto.getName() == null ? this.extractName(email) : dto.getName();
        final String fullName = dto.getName() == null ? this.extractName(email) : dto.getFullName();

        person.setName(name);
        person.setFullName(fullName);

        this.save(person);

        this.createAuthenticationRelationship(email, dto.getGuid(), person);

        if (dto.hasAnyContactInformationData()) {
            this.createContactRelationshipUsingDto(idOffice, person, dto);
        } else {
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
            final CcbMemberFilterEnum ccbMemberFilter,
            final String name,
            final Long officeScope,
            final Long[] planScope,
            final Long[] workpackScope,
            final UriComponentsBuilder uriComponentsBuilder
    ) {
        return this.repository.findAllFilteringBy(
                        stakeholderFilter,
                        userFilter,
                        ccbMemberFilter,
                        name.isEmpty() ? null : name,
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

        final Optional<PersonDetailQuery> maybeQuery = this.repository.findPersonDetailsBy(personId, officeId);

        if (!maybeQuery.isPresent()) {
            return null;
        }

        final PersonDetailDto personDetailDto = new PersonDetailDto(maybeQuery.get(), uriComponentsBuilder);

        final List<PersonPermissionDetailQuery> permissions = this.repository.findPermissions(personId, officeId);

        if (permissions.isEmpty()) {
            return personDetailDto;
        }

        final OfficePermissionDetailDto officePermissionDetailDto = this.createOfficeDetail(permissions);

        personDetailDto.setOfficePermission(officePermissionDetailDto);

        return personDetailDto;
    }

    private OfficePermissionDetailDto createOfficeDetail(final List<PersonPermissionDetailQuery> permissions) {
        final PersonPermissionDetailQuery permission = permissions.get(0);
        final OfficePermissionDetailDto result = new OfficePermissionDetailDto();

        final List<CanAccessOffice> canAccessOffices = permissions.stream()
                .map(PersonPermissionDetailQuery::getCanAccessOffice)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final List<PlanPermissionDetailDto> planDetail = createPlanDetail(permissions);

        if (canAccessOffices.isEmpty()) {
            final List<CanAccessPlan> canAccessPlan = permissions.stream()
                    .map(PersonPermissionDetailQuery::getCanAccessPlan)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (canAccessPlan.isEmpty()) {
                if (planDetail.size() == 1) {
                    result.setAccessLevel(planDetail.get(0).getAccessLevel());
                } else {
                    final List<PermissionLevelEnum> permissionLevels = planDetail.stream()
                            .map(PlanPermissionDetailDto::getAccessLevel)
                            .collect(Collectors.toList());

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
        return result;
    }

    private List<PlanPermissionDetailDto> createPlanDetail(final List<PersonPermissionDetailQuery> permissions) {
        List<PlanPermissionDetailDto> result = new ArrayList<>();

        for (PersonPermissionDetailQuery permission : permissions) {
            Plan plan = permission.getPlan();

            if (plan == null || contains(plan, result, PlanPermissionDetailDto::getId)) {
                continue;
            }

            PlanPermissionDetailDto item = new PlanPermissionDetailDto();

            List<CanAccessPlan> canAccessPlan = extractFrom(plan, permissions,
                    PersonPermissionDetailQuery::getPlan, PersonPermissionDetailQuery::getCanAccessPlan);

            final List<WorkpackPermissionDetailDto> workpackDetail = createWorkpackDetail(plan, permissions);

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
                PermissionLevelEnum accessLevel = canAccessPlan.stream()
                        .map(CanAccessPlan::getPermissionLevel)
                        .max(PermissionLevelEnum::compareTo)
                        .orElse(null);

                item.setAccessLevel(accessLevel);
            }

            item.setId(plan.getId());
            item.setName(plan.getName());
            item.setWorkpacksPermission(workpackDetail);

            boolean skip = item.getAccessLevel() == PermissionLevelEnum.NONE && workpackDetail.isEmpty();

            if (!skip) {
                result.add(item);
            }
        }

        return result;
    }

    private List<WorkpackPermissionDetailDto> createWorkpackDetail(Plan plan, List<PersonPermissionDetailQuery> permissions) {
        List<WorkpackPermissionDetailDto> result = new ArrayList<>();

        final List<Workpack> workpacks = extractFrom(plan, permissions,
                PersonPermissionDetailQuery::getPlan, PersonPermissionDetailQuery::getWorkpack);

        for (PersonPermissionDetailQuery permission : permissions) {
            final Workpack workpack = permission.getWorkpack();

            if (workpack == null
                    || contains(workpack, result, WorkpackPermissionDetailDto::getId)
                    || !workpacks.contains(workpack)
            ) {
                continue;
            }

            final List<CanAccessWorkpack> canAccessWorkpacks = extractFrom(workpack, permissions,
                    PersonPermissionDetailQuery::getWorkpack, PersonPermissionDetailQuery::getCanAccessWorkpack);

            final List<IsCCBMemberFor> isCCBMemberFors = extractFrom(workpack, permissions,
                    PersonPermissionDetailQuery::getWorkpack, PersonPermissionDetailQuery::getIsCCBMemberFor);

            final List<IsStakeholderIn> isStakeholderIns = extractFrom(workpack, permissions,
                    PersonPermissionDetailQuery::getWorkpack, PersonPermissionDetailQuery::getIsStakeholderIn);

            WorkpackPermissionDetailDto item = new WorkpackPermissionDetailDto();
            item.setId(workpack.getId());
            item.setName(getName(workpack));
            item.setRoles(getRoles(isStakeholderIns));
            item.setIcon(getFontIcon(workpack));
            item.setCcbMember(Boolean.FALSE);

            if (canAccessWorkpacks.isEmpty()) {
                if (!isStakeholderIns.isEmpty()) {
                    item.setAccessLevel(PermissionLevelEnum.NONE);
                    result.add(item);
                }
            } else {
                final PermissionLevelEnum accessLevel = canAccessWorkpacks.stream()
                        .map(CanAccessWorkpack::getPermissionLevel)
                        .max(PermissionLevelEnum::compareTo)
                        .orElse(null);

                item.setAccessLevel(accessLevel);
                result.add(item);
            }

            if (!isCCBMemberFors.isEmpty()) {
                WorkpackPermissionDetailDto item2 = new WorkpackPermissionDetailDto();
                item2.setId(workpack.getId());
                item2.setName(getName(workpack));
                item2.setRoles(null);
                item2.setIcon(getFontIcon(workpack));
                item2.setCcbMember(Boolean.TRUE);
                item2.setAccessLevel(PermissionLevelEnum.NONE);
                result.add(item2);
            }
        }

        return result;
    }

    private String getFontIcon(Workpack workpack) {
        return this.workpackRepository.findWorkpackModelByWorkpackId(workpack.getId())
                .map(WorkpackModel::getFontIcon)
                .orElse(null);
    }

    private List<String> getRoles(List<IsStakeholderIn> isStakeholderIns) {
        return isStakeholderIns.stream()
                .map(IsStakeholderIn::getRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getName(Workpack workpack) {
        return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
                .map(WorkpackName::getName)
                .orElse(null);
    }

    private <T> boolean contains(Entity entity, List<T> collection, Function<T, Long> longFunction) {
        return collection.stream()
                .anyMatch(element -> Objects.equals(entity.getId(), longFunction.apply(element)));
    }

    private <R> List<R> extractFrom(
            @NotNull Entity entity,
            List<PersonPermissionDetailQuery> permissions,
            Function<PersonPermissionDetailQuery, Entity> entitySupplier,
            Function<PersonPermissionDetailQuery, R> relationshipSupplier
    ) {
        List<R> result = new ArrayList<>();

        for (PersonPermissionDetailQuery permission : permissions) {
            final Entity other = entitySupplier.apply(permission);

            if (other == null) {
                continue;
            }

            final boolean isSame = Objects.equals(entity.getId(), other.getId());
            final R relationship = relationshipSupplier.apply(permission);

            if (isSame && relationship != null) {
                result.add(relationship);
            }
        }

        return result;
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

    private void updateContactBook(final PersonUpdateDto personUpdateDto, Person person) {
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

    public List<PersonDto> findPersonsByFullNameAndWorkpack(final String partialName, final Long idWorkpack) {
        final Optional<PersonByFullNameQuery> maybeQueryResult =
                this.repository.findPersonsInOfficeByFullName(partialName, idWorkpack);

        if (maybeQueryResult.isPresent()) {
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

        this.createAuthenticationRelationship(request.getEmail(), null, person);

        return person;
    }

    public Set<Person> findAllById(final Iterable<Long> responsible) {
        Set<Person> set = new HashSet<>();
        if (responsible == null) {
            return set;
        }
        final Iterable<Person> personIterable = this.repository.findAllById(responsible);
        for (Person person : personIterable) {
            set.add(person);
        }
        return set;
    }

    public void updateName(Long idPerson, String name) {
        final Person person = this.repository.findById(idPerson)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));

        person.setName(name);
        this.repository.save(person, 0);
    }
}
