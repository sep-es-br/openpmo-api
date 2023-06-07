package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.IsInContactBookOfService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.utils.TextSimilarityScore;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PLAN_PERMISSION_NOT_FOUND;

@Service
public class PlanPermissionService {

  private final PlanPermissionRepository repository;

  private final PlanService planService;

  private final PersonService personService;

  private final OfficeService officeService;

  private final IsAuthenticatedByService isAuthenticatedByService;

  private final IsInContactBookOfService isInContactBookOfService;

  private final RoleService roleService;

  private final AppProperties appProperties;

  private final TextSimilarityScore textSimilarityScore;

  private final JournalCreator journalCreator;

  private final TokenService tokenService;

  @Autowired
  public PlanPermissionService(
    final PlanPermissionRepository repository,
    final PlanService planService,
    final PersonService personService,
    final OfficeService officeService,
    final IsAuthenticatedByService isAuthenticatedByService,
    final IsInContactBookOfService isInContactBookOfService,
    final RoleService roleService,
    final AppProperties appProperties,
    final TextSimilarityScore textSimilarityScore,
    final JournalCreator journalCreator,
    final TokenService tokenService
  ) {
    this.repository = repository;
    this.planService = planService;
    this.personService = personService;
    this.officeService = officeService;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.isInContactBookOfService = isInContactBookOfService;
    this.roleService = roleService;
    this.appProperties = appProperties;
    this.textSimilarityScore = textSimilarityScore;
    this.journalCreator = journalCreator;
    this.tokenService = tokenService;
  }

  public void delete(
    final Long idPlan,
    final String key,
    final String authorization
  ) {
    final Person author = this.getPersonByAuthorization(authorization);
    final Optional<Person> personOptional = this.personService.findByKey(key);
    if (!personOptional.isPresent()) {
      throw new RegistroNaoEncontradoException(PLAN_PERMISSION_NOT_FOUND);
    }
    final Plan plan = this.planService.findById(idPlan);
    final Person target = personOptional.get();
    final List<CanAccessPlan> permissions = this.repository.findByIdPlanAndIdPerson(idPlan, target.getId());
    this.repository.deleteAll(permissions);
    this.journalCreator.planPermission(
      plan,
      target,
      author,
      this.getGratherPermissionLevel(permissions),
      JournalAction.REMOVED
    );
  }

  private Person getPersonByAuthorization(final String authorization) {
    final Long idAuthor = this.tokenService.getUserId(authorization);
    return this.personService.findById(idAuthor);
  }

  private PermissionLevelEnum getGratherPermissionLevel(final List<? extends CanAccessPlan> permissions) {
    final boolean hasEdit = permissions.stream()
      .map(CanAccessPlan::getPermissionLevel)
      .anyMatch(level -> level.equals(PermissionLevelEnum.EDIT));
    if (hasEdit) return PermissionLevelEnum.EDIT;
    return PermissionLevelEnum.READ;
  }

  public List<PlanPermissionDto> findAllDto(
    final Long idPlan,
    final String key,
    final Long idPerson,
    final String term
  ) {
    final List<RoleResource> resources = this.roleService.getRolesByKey(idPerson, key);
    final List<PlanPermissionDto> plansPermissionDto = new ArrayList<>();
    final Plan plan = this.planService.findById(idPlan);
    final List<CanAccessPlan> listPlansPermission = this.listPlansPermissions(plan, key);
    final Map<Person, List<PermissionDto>> mapPermission = new HashMap<>();
    listPlansPermission.forEach(p -> {
      final PermissionDto dto = new PermissionDto();
      dto.setId(p.getId());
      dto.setLevel(p.getPermissionLevel());
      dto.setRole(p.getRole());
      mapPermission.computeIfAbsent(p.getPerson(), k -> new ArrayList<>());
      mapPermission.get(p.getPerson()).add(dto);
    });
    mapPermission.keySet().forEach(person -> {
      final PlanPermissionDto planPermissionDto = new PlanPermissionDto();
      planPermissionDto.setIdPlan(idPlan);
      planPermissionDto.setPermissions(mapPermission.get(person));
      final Optional<IsAuthenticatedBy> maybeAuthenticatedBy =
        this.isAuthenticatedByService.findAuthenticatedBy(person.getId());
      planPermissionDto.setPerson(PersonDto.from(
        person,
        Optional.empty(),
        maybeAuthenticatedBy
      ));
      plansPermissionDto.add(planPermissionDto);
      planPermissionDto.getPerson().addAllRoles(resources);
    });

    plansPermissionDto.removeIf(dto -> !StringUtils.isBlank(term)
                                       && this.textSimilarityScore.execute(
      dto.getPerson().getName() + dto.getPerson().getFullName(),
      term
    ) <= this.appProperties.getSearchCutOffScore());

    return plansPermissionDto;
  }

  private List<CanAccessPlan> listPlansPermissions(
    final Plan plan,
    final String key
  ) {
    if (key == null || key.isEmpty()) {
      return this.findByIdPlan(plan.getId());
    }

    final Person person = this.personService.findPersonByKey(key);

    return this.findByPlanAndPerson(plan, person);
  }

  public List<CanAccessPlan> findByIdPlan(final Long idPlan) {
    return this.repository.findByIdPlan(idPlan);
  }

  public List<CanAccessPlan> findByPlanAndPerson(
    final Plan plan,
    final Person person
  ) {
    return this.repository.findByIdPlanAndIdPerson(plan.getId(), person.getId());
  }

  public CanAccessPlan findById(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(PLAN_PERMISSION_NOT_FOUND));
  }

  public void deleteAll(final Set<CanAccessPlan> permissions) {
    this.repository.deleteAll(permissions);
  }

  public void update(
    final PlanPermissionParamDto request,
    final String authorization
  ) {
    final Person author = this.getPersonByAuthorization(authorization);
    final Office office = this.officeService.findOfficeByPlan(request.getIdPlan())
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));
    final Person target = this.returnPersonOrCreateIfNotExists(
      request.getPerson(),
      office.getId()
    );
    final Plan plan = this.planService.findById(request.getIdPlan());
    final List<CanAccessPlan> plansPermissionsDataBase = this.findByPlanAndPerson(plan, target);
    plansPermissionsDataBase.forEach(permissionDatabase -> {
      final boolean find = request.getPermissions() != null && request.getPermissions().stream()
        .anyMatch(filtro -> permissionDatabase.getId().equals(filtro.getId()));
      if (find) {
        return;
      }
      this.delete(permissionDatabase);
    });
    if (request.getPermissions() != null) {
      request.getPermissions().forEach(permission -> {
        if (permission.getId() == null) {
          this.save(this.buildCanAccessPlan(target, plan, permission, null), target);

          return;
        }
        final Optional<CanAccessPlan> optionalCanAccessPlan = this.repository.findById(permission.getId());
        if (!optionalCanAccessPlan.isPresent()) {
          throw new RegistroNaoEncontradoException(PLAN_PERMISSION_NOT_FOUND);
        }
        this.save(this.buildCanAccessPlan(target, plan, permission, optionalCanAccessPlan.get().getId()), target);
      });
      this.journalCreator.planPermission(
        plan,
        target,
        author,
        request.getGratherPermissionLevel(),
        JournalAction.EDITED
      );
    }
  }

  private Person returnPersonOrCreateIfNotExists(
    final PersonDto person,
    final Long idOffice
  ) {
    final Optional<Person> maybePerson = this.personService.findByKey(person.getKey());
    return maybePerson.orElseGet(() -> this.storePerson(person, idOffice));

  }

  public void delete(final CanAccessPlan plan) {
    this.repository.delete(plan);
  }

  public CanAccessPlan save(
    final CanAccessPlan planPermission,
    final Person person
  ) {
    final Office office = this.officeService.findOfficeByPlan(planPermission.getIdPlan())
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));

    if (!this.isInContactBookOfService.existsByPersonIdAndOfficeId(person.getId(), office.getId())) {
      final IsInContactBookOf isInContactBookOf = new IsInContactBookOf();
      isInContactBookOf.setPerson(person);
      isInContactBookOf.setOffice(office);
      this.isInContactBookOfService.save(isInContactBookOf);
    }

    return this.repository.save(planPermission, 0);
  }

  private CanAccessPlan buildCanAccessPlan(
    final Person person,
    final Plan plan,
    final PermissionDto request,
    final Long id
  ) {
    return new CanAccessPlan(
      id,
      "",
      request.getRole(),
      request.getLevel(),
      person,
      plan
    );
  }

  private Person storePerson(
    final PersonDto person,
    final Long idOffice
  ) {
    return this.personService.savePerson(person, idOffice);
  }

  public void store(
    final PlanPermissionParamDto request,
    final String authorization
  ) {
    final Person author = this.getPersonByAuthorization(authorization);
    final Office office = this.officeService.findOfficeByPlan(request.getIdPlan())
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));
    final Person target = this.returnPersonOrCreateIfNotExists(request.getPerson(), office.getId());
    final Plan plan = this.planService.findById(request.getIdPlan());
    for (final PermissionDto permission : request.getPermissions()) {
      final CanAccessPlan planPermission = this.buildCanAccessPlan(target, plan, permission, null);
      this.save(planPermission, target);
    }
    this.journalCreator.planPermission(
      plan,
      target,
      author,
      request.getGratherPermissionLevel(),
      JournalAction.CREATED
    );
  }

  public Set<CanAccessPlan> findInheritedPermission(
    final Long workpackId,
    final Long personId
  ) {
    return this.repository.findInheritedPermission(workpackId, personId);
  }

}
