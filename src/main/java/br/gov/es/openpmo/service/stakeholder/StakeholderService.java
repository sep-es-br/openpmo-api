package br.gov.es.openpmo.service.stakeholder;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.dto.stakeholder.*;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Actor;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.*;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.IsAuthenticatedByRepository;
import br.gov.es.openpmo.repository.StakeholderRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import br.gov.es.openpmo.repository.custom.filters.FindStakeholderAndPermissionUsingCustomFilter;
import br.gov.es.openpmo.service.actors.IsInContactBookOfService;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.permissions.IRemoteRolesFetcher;
import br.gov.es.openpmo.service.permissions.OfficePermissionService;
import br.gov.es.openpmo.service.permissions.PlanPermissionService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import static java.lang.Boolean.TRUE;

@Service
public class StakeholderService {

    private final PersonService personService;
    private final OrganizationService serviceOrganization;
    private final WorkpackService serviceWorkpack;
    private final ModelMapper modelMapper;
    private final OfficeService officeService;
    private final FindStakeholderAndPermissionUsingCustomFilter findStakeHolderAndPermission;
    private final StakeholderRepository repository;
    private final IsInContactBookOfService isInContactBookOfService;
    private final WorkpackPermissionRepository workpackPermissionRepository;
    private final CustomFilterRepository customFilterRepository;
    private final PlanPermissionService planPermissionService;
    private final OfficePermissionService officePermissionService;
    private final IsAuthenticatedByRepository authenticationRepository;
    private final AcessoCidadaoApi acessoCidadaoApi;
    private final IRemoteRolesFetcher remoteRolesFetcher;
    @Value("${users.administrators}")
    private List<String> administrators;
    @Value("${app.login.server.name}")
    private String authenticationServer;

    @Autowired
    public StakeholderService(
            final PersonService personService,
            final OrganizationService serviceOrganization,
            final WorkpackService serviceWorkpack,
            final ModelMapper modelMapper,
            final OfficeService officeService,
            final FindStakeholderAndPermissionUsingCustomFilter findStakeHolderAndPermission,
            final IsInContactBookOfService isInContactBookOfService,
            final StakeholderRepository repository,
            final WorkpackPermissionRepository workpackPermissionRepository,
            final CustomFilterRepository customFilterRepository,
            final PlanPermissionService planPermissionService,
            final OfficePermissionService officePermissionService,
            final IsAuthenticatedByRepository authenticationRepository,
            final AcessoCidadaoApi acessoCidadaoApi,
            final IRemoteRolesFetcher remoteRolesFetcher
    ) {
        this.personService = personService;
        this.serviceOrganization = serviceOrganization;
        this.serviceWorkpack = serviceWorkpack;
        this.officeService = officeService;
        this.repository = repository;
        this.workpackPermissionRepository = workpackPermissionRepository;
        this.modelMapper = modelMapper;
        this.customFilterRepository = customFilterRepository;
        this.findStakeHolderAndPermission = findStakeHolderAndPermission;
        this.isInContactBookOfService = isInContactBookOfService;
        this.planPermissionService = planPermissionService;
        this.officePermissionService = officePermissionService;
        this.authenticationRepository = authenticationRepository;
        this.acessoCidadaoApi = acessoCidadaoApi;
        this.remoteRolesFetcher = remoteRolesFetcher;
    }

    private static Map<Person, List<PermissionDto>> mapAndExtractPermissions(final StakeholderAndPermissionQuery query) {
        final Map<Person, List<PermissionDto>> mappedPermissions = new HashMap<>();
        query.getWorkpackPermissions().forEach(canAccessWorkpack -> {
            mappedPermissions.computeIfAbsent(canAccessWorkpack.getPerson(), key -> new ArrayList<>());
            mappedPermissions.get(canAccessWorkpack.getPerson()).add(
                    PermissionDto.of(canAccessWorkpack)
            );
        });
        return mappedPermissions;
    }

    private static void applyOrdering(final List<? extends StakeholderDto> dto, final CustomFilter filter) {
        final StakeholderSorter sorter = StakeholderSorter.find(filter.getSortBy());

        if (filter.getDirection().equals(SortByDirectionEnum.ASC)) {
            dto.sort(sorter.getComparator());
        } else {
            dto.sort(Collections.reverseOrder(sorter.getComparator()));
        }

    }

    @Transactional
    public Person storeStakeholderPerson(final StakeholderParamDto request) {

        final Person person = this.createOrUpdatePerson(request);

        final Workpack workpack = this.serviceWorkpack.findById(request.getIdWorkpack());

        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> this.repository.save(
                    this.buildIsStakeholderIn(
                            person,
                            null,
                            workpack,
                            role.getRole(),
                            role.getFrom(),
                            role.getTo(),
                            role.isActive()
                    )));
        }

        if (!CollectionUtils.isEmpty(request.getPermissions())) {
            saveIsInContactBook(request, person);
            request.getPermissions().forEach(permission -> this.workpackPermissionRepository.save(
                    this.buildCanAccessWorkpack(
                            person,
                            workpack,
                            permission,
                            permission.getId(),
                            request.getIdPlan()
                    )));
        }
        return person;
    }

    private Person createOrUpdatePerson(final StakeholderParamDto request) {
        final PersonStakeholderParamDto personDto = request.getPerson();

        if (this.isInvalidNonUser(personDto)) {
            throw new NegocioException(EMAIL_NOT_NULL);
        }

        final Long personId = personDto.getId();
        final Long workpackId = request.getIdWorkpack();

        if (personId != null) {
            final Optional<Person> maybePerson = this.personService.maybeFindPersonById(personId);

            if (maybePerson.isPresent()) {
                final Person person = this.buildPerson(maybePerson.get(), request);

                this.createOrUpdateContactInformation(
                        request,
                        personId,
                        workpackId,
                        person
                );

                return person;
            }
        }

        final Person person = this.buildPerson(new Person(), request);
        this.throwExceptionIfFullNameAlreadyExists(person, workpackId);

        this.personService.save(person);

        final String email = personDto.getEmail();
        if (TRUE.equals(personDto.getIsUser()) && this.authenticationNotExists(email)) {
            this.personService.createAuthenticationRelationship(
                    email,
                    request.getPerson().getGuid(),
                    person
            );
        }

        this.createContactRelationshipWithOffice(person, request, new IsInContactBookOf());
        return person;
    }

    private boolean authenticationNotExists(final String email) {
        final Optional<Person> maybePerson = this.personService.findByEmail(email);
        return !maybePerson.isPresent();
    }

    private boolean isInvalidNonUser(final PersonStakeholderParamDto personDto) {
        return !personDto.getIsUser() && personDto.getContactEmail() == null;
    }

    private Person buildPerson(final Person person, final StakeholderParamDto request) {
        final PersonStakeholderParamDto personDto = request.getPerson();

        person.setAdministrator(this.administrators.contains(personDto.getEmail()));
        person.setFullName(personDto.getFullName());

        final String name = personDto.getName() != null ? personDto.getName() : personDto.firstNameFromFullName();
        person.setName(name);

        return person;
    }

    private void createOrUpdateContactInformation(
            final StakeholderParamDto request,
            final Long personId,
            final Long workpackId,
            final Person person
    ) {
        final Optional<IsInContactBookOf> maybeContactInformation = this.isInContactBookOfService.findContactInformationUsingPersonIdAndWorkpackId(
                personId,
                workpackId
        );

        if (maybeContactInformation.isPresent()) {
            this.createContactRelationshipWithOffice(
                    person,
                    request,
                    maybeContactInformation.get()
            );
        } else {
            this.createContactRelationshipWithOffice(
                    person,
                    request,
                    new IsInContactBookOf()
            );
        }
    }

    private void createContactRelationshipWithOffice(
            final Person person,
            final StakeholderParamDto request,
            final IsInContactBookOf isInContactBookOf
    ) {

        final PersonStakeholderParamDto dto = request.getPerson();
        isInContactBookOf.setAddress(dto.getAddress());
        isInContactBookOf.setEmail(dto.getContactEmail());
        isInContactBookOf.setPhoneNumber(dto.getPhoneNumber());
        isInContactBookOf.setPerson(person);

        final Office officeByWorkpack = this.officeService
                .findOfficeByWorkpack(request.getIdWorkpack(), request.getIdPlan())
                .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

        isInContactBookOf.setOffice(officeByWorkpack);

        this.isInContactBookOfService.save(isInContactBookOf);
    }

    private void throwExceptionIfFullNameAlreadyExists(final Person person, final Long idWorkpack) {
        this.personService.findPersonByFullName(person.getFullName(), idWorkpack);
    }

    private IsStakeholderIn buildIsStakeholderIn(
            final Person person, final Organization organization, final Workpack workpack,
            final String role, final LocalDate from, final LocalDate to, final boolean isActive
    ) {
        final Actor actor = person == null ? organization : person;
        final IsStakeholderIn isStakeholderIn = new IsStakeholderIn();

        isStakeholderIn.setActor(actor);
        isStakeholderIn.setFrom(from);
        isStakeholderIn.setTo(to);
        isStakeholderIn.setRole(role);
        isStakeholderIn.setWorkpack(workpack);
        isStakeholderIn.setActive(isActive);
        return isStakeholderIn;
    }

    private CanAccessWorkpack buildCanAccessWorkpack(
            final Person person,
            final Workpack workpack,
            final PermissionDto permissionDto,
            final Long id,
            final Long planId
    ) {
        return new CanAccessWorkpack(
                id,
                "",
                permissionDto.getRole(),
                permissionDto.getLevel(),
                person,
                workpack,
                planId
        );
    }

    public void updateStakeholderPerson(final StakeholderParamDto request) {
        final Person person = this.createOrUpdatePerson(request);

        final Workpack workpack = this.serviceWorkpack.findById(request.getIdWorkpack());

        final List<IsStakeholderIn> stakeholderIns = this.returnListStakeholderIn(
                workpack.getId(),
                null,
                person.getId()
        );

        final List<CanAccessWorkpack> canAccessWorkpacks = this.workpackPermissionRepository.findByIdWorkpackAndIdPerson(
                workpack.getId(),
                person.getId()
        );

        stakeholderIns.forEach(stakeholderDatabase -> {
            if (request.getRoles() == null || request.getRoles().stream().noneMatch(
                    r -> r.getRole() != null && r.getRole().equals(stakeholderDatabase.getRole()))) {
                this.repository.delete(stakeholderDatabase);
            }
        });
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> {
                if (stakeholderIns.stream().noneMatch(
                        rb -> rb.getRole() != null && rb.getRole().equals(role.getRole()))) {
                    this.repository.save(
                            this.buildIsStakeholderIn(person, null, workpack, role.getRole(), role.getFrom(), role.getTo(),
                                    role.isActive()
                            ));
                } else {
                    final IsStakeholderIn isStakeholderIn = stakeholderIns.stream().filter(
                            s -> s.getId().equals(role.getId())).findFirst().orElse(null);
                    if (isStakeholderIn != null) {
                        isStakeholderIn.setTo(role.getTo());
                        isStakeholderIn.setFrom(role.getFrom());
                        isStakeholderIn.setActive(role.isActive());
                        this.repository.save(isStakeholderIn);
                    }
                }
            });
        }

        canAccessWorkpacks.forEach(canAccessWorkpack -> {
            if (request.getPermissions() == null || request.getPermissions().stream().noneMatch(
                    p -> p.getRole() != null && p.getRole().equals(canAccessWorkpack.getRole()))) {
                this.workpackPermissionRepository.delete(canAccessWorkpack);
            }
        });
        if (!CollectionUtils.isEmpty(request.getPermissions())) {
            request.getPermissions().forEach(permission -> {
                if (this.isNewWorkpackPermission(canAccessWorkpacks, permission)) {
                    saveIsInContactBook(request, person);

                    this.workpackPermissionRepository.save(
                            this.buildCanAccessWorkpack(
                                    person,
                                    workpack,
                                    permission,
                                    permission.getId(),
                                    request.getIdPlan()
                            ));
                } else {

                    final CanAccessWorkpack canAccessWorkpack = canAccessWorkpacks.stream()
                            .filter(ca -> this.hasSameRole(permission, ca))
                            .findFirst()
                            .orElse(null);

                    if (canAccessWorkpack != null) {
                        canAccessWorkpack.setPermissionLevel(permission.getLevel());

                        saveIsInContactBook(request, person);
                        this.workpackPermissionRepository.save(canAccessWorkpack);
                    }
                }
            });
        }
    }

    private void saveIsInContactBook(StakeholderParamDto request, Person person) {
        final Office office = this.officeService
                .findOfficeByWorkpack(request.getIdWorkpack(), request.getIdPlan())
                .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

        if (!this.isInContactBookOfService.existsByPersonIdAndOfficeId(person.getId(), office.getId())) {
            final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
            isInContactBookOf.setPerson(person);
            isInContactBookOf.setOffice(office);
            this.isInContactBookOfService.save(isInContactBookOf);
        }
    }

    private boolean isNewWorkpackPermission(
            final Collection<? extends CanAccessWorkpack> canAccessWorkpacks,
            final PermissionDto permission
    ) {
        return canAccessWorkpacks.stream().noneMatch(ca -> ca.getRole() != null && ca.getRole().equals(permission.getRole()));
    }

    private boolean hasSameRole(final PermissionDto permission, final CanAccessWorkpack ca) {
        return ca.getRole() != null && ca.getRole().equals(permission.getRole());
    }

    private List<IsStakeholderIn> returnListStakeholderIn(
            final Long idWorkpack,
            final Long idOrganization,
            final Long idPerson
    ) {
        Long id = null;

        if ((idOrganization != null) && (idPerson != null)) {
            throw new NegocioException(FILTER_SHOULD_BE_PERSON_OR_ORGANIZATION);
        }
        if (idOrganization != null) {
            id = idOrganization;
        }

        if (idPerson != null) {
            id = idPerson;
        }

        return this.repository.findByIdWorkpackAndIdActor(idWorkpack, id);
    }

    public void storeStakeholderOrganization(final OrganizationStakeholderParamDto request) {
        final Organization organization = this.serviceOrganization.findById(request.getIdOrganization());
        final Workpack workpack = this.serviceWorkpack.findById(request.getIdWorkpack());
        if (request.getRoles() != null) {
            request.getRoles().forEach(role -> this.repository.save(
                    this.buildIsStakeholderIn(null, organization, workpack, role.getRole(), role.getFrom(), role.getTo(),
                            role.isActive()
                    )));
        }
    }

    public void updateStakeholderOrganization(@Valid final OrganizationStakeholderParamDto request) {
        final Organization organization = this.serviceOrganization.findById(request.getIdOrganization());
        final Workpack workpack = this.serviceWorkpack.findById(request.getIdWorkpack());
        final List<IsStakeholderIn> rolesBd = this.repository.findByIdWorkpackAndIdActor(
                workpack.getId(),
                request.getIdOrganization()
        );

        rolesBd.forEach(r -> {
            if (request.getRoles() == null || request.getRoles().stream().noneMatch(
                    rr -> rr.getRole() != null && rr.getRole().equals(r.getRole()))) {
                this.repository.delete(r);
            }
        });
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            request.getRoles().forEach(role -> {
                if (rolesBd.stream().noneMatch(rb -> rb.getRole() != null && rb.getRole().equals(role.getRole()))) {
                    this.repository.save(
                            this.buildIsStakeholderIn(
                                    null,
                                    organization,
                                    workpack,
                                    role.getRole(),
                                    role.getFrom(),
                                    role.getTo(),
                                    role.isActive()
                            ));
                } else {
                    final IsStakeholderIn stakeholderIn = rolesBd.stream().filter(s -> role.getId() != null && s.getId().equals(
                            role.getId())).findFirst().orElse(null);
                    if (stakeholderIn != null) {
                        stakeholderIn.setTo(role.getTo());
                        stakeholderIn.setFrom(role.getFrom());
                        stakeholderIn.setActive(role.isActive());
                        this.repository.save(stakeholderIn);
                    }
                }
            });
        }
    }

    public void deleteOrganization(final Long idWorkpack, final Long idOrganization) {
        final Workpack workpack = this.serviceWorkpack.findById(idWorkpack);
        final Organization organization = this.serviceOrganization.findById(idOrganization);
        final List<IsStakeholderIn> stakeholders = this.repository.findByIdWorkpackAndIdActor(
                workpack.getId(),
                organization.getId()
        );
        this.repository.deleteAll(stakeholders);
    }

    public void deletePerson(final Long idWorkpack, final Long idPerson, final Long idPlan) {
        final Workpack workpack = this.serviceWorkpack.findById(idWorkpack);
        final Person person = this.personService.findById(idPerson);
        final List<IsStakeholderIn> stakeholders = this.repository.findByIdWorkpackAndIdPerson(
                workpack.getId(),
                person.getId()
        );
        this.repository.deleteAll(stakeholders);
        final List<CanAccessWorkpack> canAccessWorkpacks = this.workpackPermissionRepository.findByIdWorkpackAndIdPerson(
                workpack.getId(),
                person.getId()
        );
        this.workpackPermissionRepository.deleteAll(canAccessWorkpacks);
    }

    public StakeholderPersonDto findPerson(final Long workpackId, final Long personId) {
        final Workpack workpack = this.serviceWorkpack.findById(workpackId);

        final Optional<Person> maybePerson = this.personService.maybeFindPersonById(personId);
        if (!maybePerson.isPresent()) {
            return null;
        }
        final Person person = maybePerson.get();

        final StakeholderPersonDto stakeholderPersonDto = new StakeholderPersonDto(workpackId);

        this.fillPerson(workpack.getId(), person, stakeholderPersonDto);
        this.fillRoles(stakeholderPersonDto, workpack.getId(), person.getId());
        this.fillPermissions(workpack.getId(), person.getId(), stakeholderPersonDto);

        return stakeholderPersonDto;
    }

    private void fillPerson(
            final Long workpackId,
            final Person person,
            final StakeholderPersonDto stakeholderPersonDto
    ) {
        final Optional<IsAuthenticatedBy> maybeAuthentication = person.getAuthentications().stream()
                .filter(p -> p.getAuthService().getServer().equals(this.authenticationServer))
                .findFirst();

        final Optional<IsInContactBookOf> maybeContactInformation =
                this.isInContactBookOfService.findContactInformationUsingPersonIdAndWorkpackId(person.getId(), workpackId);

        stakeholderPersonDto.setPerson(PersonDto.from(person, maybeContactInformation, maybeAuthentication));
    }

    private void fillRoles(
            final StakeholderPersonDto stakeholderPersonDto,
            final Long workpackId,
            final Long personId
    ) {
        final List<IsStakeholderIn> stakeholderIns = this.repository.findByIdWorkpackAndIdActor(
                workpackId,
                personId
        );


        stakeholderIns.forEach(s -> stakeholderPersonDto.getRoles().add(
                this.modelMapper.map(s, RoleDto.class))
        );

        final List<RoleResource> remoteRoles = this.remoteRolesFetcher.fetch(personId);

        stakeholderPersonDto.getPerson().addAllRoles(remoteRoles);
    }

    private void fillPermissions(
            final Long workpackId,
            final Long personId,
            final StakeholderPersonDto stakeholderPersonDto
    ) {

        stakeholderPersonDto.getPermissions().addAll(this.fillInheritedOfficePermissions(workpackId, personId));
        stakeholderPersonDto.getPermissions().addAll(this.fillInheritedPlanPermissions(workpackId, personId));
        stakeholderPersonDto.getPermissions().addAll(this.fillInheritedWorkpackPermissions(workpackId, personId));

        final List<CanAccessWorkpack> permissionWorkpack = this.workpackPermissionRepository.findByIdWorkpackAndIdPerson(
                workpackId,
                personId
        );

        permissionWorkpack.forEach(permission -> {
            final PermissionDto dto = this.modelMapper.map(permission, PermissionDto.class);
            dto.setRole(permission.getRole());
            dto.setLevel(permission.getPermissionLevel());
            dto.setIdPlan(permission.getIdPlan());
            stakeholderPersonDto.getPermissions().add(dto);
        });

    }

    private List<PermissionDto> fillInheritedOfficePermissions(final Long workpackId, final Long personId) {
        final Set<CanAccessOffice> permissions = this.officePermissionService.findInheritedPermission(
                workpackId,
                personId
        );

        return permissions.stream().map(permission -> {
            final PermissionDto dto = this.modelMapper.map(permission, PermissionDto.class);
            dto.setRole(permission.getRole());
            dto.setLevel(permission.getPermissionLevel());
            dto.setInheritedFrom(permission.getOfficeName());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<PermissionDto> fillInheritedPlanPermissions(final Long workpackId, final Long personId) {
        final Set<CanAccessPlan> inheritedPlanPermission = this.planPermissionService.findInheritedPermission(
                workpackId,
                personId
        );

        return inheritedPlanPermission.stream().map(permission -> {
            final PermissionDto dto = this.modelMapper.map(permission, PermissionDto.class);
            dto.setRole(permission.getRole());
            dto.setLevel(permission.getPermissionLevel());
            dto.setInheritedFrom(permission.getPlanName());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<PermissionDto> fillInheritedWorkpackPermissions(final Long workpackId, final Long personId) {
        final Set<CanAccessWorkpack> inheritedWorkpackPermissions = this.workpackPermissionRepository.findInheritedPermission(
                workpackId,
                personId
        );

        return inheritedWorkpackPermissions.stream().map(permission -> {
            final PermissionDto dto = this.modelMapper.map(permission, PermissionDto.class);
            dto.setRole(permission.getRole());
            dto.setLevel(permission.getPermissionLevel());
            dto.setInheritedFrom(permission.getWorkpackName());
            dto.setIdPlan(permission.getIdPlan());
            return dto;
        }).collect(Collectors.toList());
    }

    public StakeholderOrganizationDto findOrganization(final Long idWorkpack, final Long idOrganization) {
        final Workpack workpack = this.serviceWorkpack.findById(idWorkpack);
        final Organization organization = this.serviceOrganization.findById(idOrganization);
        final List<IsStakeholderIn> stakeholderIns = this.repository.findByIdWorkpackAndIdActor(
                workpack.getId(),
                idOrganization
        );
        final StakeholderOrganizationDto stakeholderOrganizationDto = new StakeholderOrganizationDto();
        stakeholderOrganizationDto.setIdWorkpack(idWorkpack);
        stakeholderOrganizationDto.setOrganization(this.modelMapper.map(organization, OrganizationDto.class));
        stakeholderIns.forEach(s -> stakeholderOrganizationDto.getRoles().add(this.modelMapper.map(s, RoleDto.class)));
        return stakeholderOrganizationDto;
    }

    public List<StakeholderDto> findAll(final Long idWorkpack, final Long idFilter) {
        final Workpack workpack = this.serviceWorkpack.findById(idWorkpack);
        return this.findStakeholderAndPermissions(
                workpack.getId(),
                idFilter
        );
    }

    private List<StakeholderDto> findStakeholderAndPermissions(final Long idWorkpack, final Long idFilter) {

        if (idFilter == null) {
            return this.createDto(
                    idWorkpack,
                    this.repository.findByIdWorkpack(idWorkpack)
            );
        }

        final CustomFilter filter = this.customFilterRepository
                .findById(idFilter)
                .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

        final Map<String, Object> params = new HashMap<>();
        params.put("idWorkpack", idWorkpack);

        final Optional<StakeholderAndPermissionQuery> query = this.findStakeHolderAndPermission.execute(
                filter,
                params
        );

        if (!query.isPresent()) {
            return new ArrayList<>();
        }

        final List<StakeholderDto> dto = this.createDto(
                idWorkpack,
                query.get()
        );

        StakeholderService.applyOrdering(dto, filter);

        return dto;
    }

    private List<StakeholderDto> createDto(
            final Long idWorkpack,
            final StakeholderAndPermissionQuery query
    ) {

        final Map<Actor, List<RoleDto>> mappedRoles = this.mapAndExtractRoles(query);
        final Map<Person, List<PermissionDto>> mappedPermissions = StakeholderService.mapAndExtractPermissions(query);

        final Set<StakeholderDto> stakeholderDtos = new HashSet<>();
        mappedRoles.keySet().forEach(actor -> {
            final StakeholderDto stakeholderDto = new StakeholderDto();
            stakeholderDto.setIdWorkpack(idWorkpack);
            if (actor instanceof Person) {
                stakeholderDto.setPerson(this.modelMapper.map(actor, PersonDto.class));
            } else {
                stakeholderDto.setOrganization(this.modelMapper.map(actor, OrganizationDto.class));
            }
            stakeholderDto.getRoles().addAll(mappedRoles.get(actor));
            stakeholderDtos.add(stakeholderDto);
        });

        mappedPermissions.keySet().forEach(person -> {
            final Optional<StakeholderDto> maybeStakeholderDto = stakeholderDtos.stream()
                    .filter(dto -> dto.getPerson() != null)
                    .filter(p -> person.getId().equals(p.getPerson().getId()))
                    .findFirst();

            if (maybeStakeholderDto.isPresent()) {
                maybeStakeholderDto.get()
                        .getPermissions()
                        .addAll(mappedPermissions.get(person));
            } else {
                final StakeholderDto stakeholderDto = new StakeholderDto();
                stakeholderDto.setIdWorkpack(idWorkpack);
                stakeholderDto.setPerson(this.modelMapper.map(person, PersonDto.class));
                stakeholderDto
                        .getPermissions()
                        .addAll(mappedPermissions.get(person));
                stakeholderDtos.add(stakeholderDto);
            }
        });
        return new ArrayList<>(stakeholderDtos);
    }

    private Map<Actor, List<RoleDto>> mapAndExtractRoles(final StakeholderAndPermissionQuery query) {
        final Map<Actor, List<RoleDto>> mappedRoles = new HashMap<>();
        query.getStakeholderIn().stream().filter(Objects::nonNull).forEach(stakeholderIn -> {
            mappedRoles.computeIfAbsent(stakeholderIn.getActor(), key -> new ArrayList<>());
            mappedRoles.get(stakeholderIn.getActor()).add(this.modelMapper.map(stakeholderIn, RoleDto.class));
        });
        return mappedRoles;
    }

    public Set<StakeholderCardViewDto> findAllPersonStakeholderByWorkpackId(final Long id) {
        return this.repository.findStakeholdersAndAscendingByWorkpackId(id).stream()
                .map(StakeholderCardViewDto::of)
                .collect(Collectors.toSet());
    }
}
