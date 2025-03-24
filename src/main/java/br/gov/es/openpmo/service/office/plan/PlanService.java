package br.gov.es.openpmo.service.office.plan;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllPlanUsingCustomFilter;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.permissions.OfficePermissionService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PLAN_NOT_FOUND;

@Service
public class PlanService {

  private final PlanRepository planRepository;
  private final PersonService personService;
  private final PlanPermissionRepository planPermissionRepository;
  private final OfficePermissionService officePermissionService;
  private final OfficeService officeService;
  private final CustomFilterRepository customFilterRepository;
  private final FindAllPlanUsingCustomFilter findAllPlan;
  private final WorkpackRepository workpackRepository;
  private final AppProperties appProperties;
  private final IAsyncDashboardService dashboardService;

  @Autowired
  public PlanService(
    final PlanRepository planRepository,
    final PersonService personService,
    final PlanPermissionRepository planPermissionRepository,
    final OfficePermissionService officePermissionService,
    final OfficeService officeService,
    final CustomFilterRepository customFilterRepository,
    final FindAllPlanUsingCustomFilter findAllPlan,
    final WorkpackRepository workpackRepository,
    final IAsyncDashboardService dashboardService,
    final AppProperties appProperties
  ) {
    this.planRepository = planRepository;
    this.personService = personService;
    this.planPermissionRepository = planPermissionRepository;
    this.officePermissionService = officePermissionService;
    this.officeService = officeService;
    this.customFilterRepository = customFilterRepository;
    this.findAllPlan = findAllPlan;
    this.workpackRepository = workpackRepository;
    this.dashboardService = dashboardService;
    this.appProperties = appProperties;
  }

  public List<Plan> findAll() {
    final List<Plan> plans = new ArrayList<>();
    this.planRepository.findAll().forEach(plans::add);
    return plans;
  }

  public List<Plan> findAllInOffice(
    final Long idOffice,
    final Long idFilter,
    final String term
  ) {
    if(idFilter == null) {
    	if (StringUtils.isBlank(term)) return this.findAllInOffice(idOffice);
    	else return findAllInOfficeByTerm(idOffice, term);
    }
    final CustomFilter filter = this.customFilterRepository
      .findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", idOffice);
    params.put("term", term);
    params.put("searchCutOffScore", appProperties.getSearchCutOffScore());

    if (StringUtils.isNotBlank(term)) filter.setSimilarityFilter(true);

    return this.findAllPlan.execute(filter, params);
  }

  public List<Long> findAllIdsInOfficeOrderByStartDesc(final Long idOffice) {
    return this.planRepository.findAllIdsInOfficeOrderByStartDesc(idOffice);
  }

  public List<Long> findAllUserHasPermission(Long idOffice,  Long idPerson, Long idPlan) {
    return planRepository.findAllWithPermissionByUserAndOffice(idOffice, idPerson, idPlan);
  }

  public List<Plan> findAllInOffice(final Long idOffice) {
    final List<Plan> plans = this.planRepository.findAllInOffice(idOffice);
    plans.sort(Comparator.comparing(Plan::getStart).reversed());
    return plans;
  }

  public List<Plan> findAllInOfficeByTerm(final Long idOffice, final String term) {
    return this.planRepository.findAllInOfficeByTerm(idOffice, term, this.appProperties.getSearchCutOffScore());
  }

  public Plan save(final Plan plan) {
    Plan saved = this.planRepository.save(plan);
    this.dashboardService.calculate();
    return saved;
  }

  public void delete(final Plan plan) {
    if(this.workpackRepository.existsByPlanId(plan.getId())) {
      throw new NegocioException(ApplicationMessage.PLAN_DELETE_RELATIONSHIP_ERROR);
    }
    this.planRepository.delete(plan);
  }

  public Plan getPlan(final PlanUpdateDto planUpdateDto) {
    final Plan plan = this.findById(planUpdateDto.getId());
    plan.setStart(planUpdateDto.getStart());
    plan.setFinish(planUpdateDto.getFinish());
    plan.setName(planUpdateDto.getName());
    plan.setFullName(planUpdateDto.getFullName());
    return plan;
  }

  public Plan findById(final Long idPlan) {
    return this.planRepository.findById(idPlan)
      .orElseThrow(() -> new NegocioException(PLAN_NOT_FOUND));
  }

  public Optional<Plan> maybeFindById(final Long idPlan) {
    return this.planRepository.findById(idPlan);
  }

  public List<PlanDto> checkPermission(
    final List<PlanDto> plans,
    final Long idUser,
    final Long idOffice
  ) {
    final Person person = this.personService.findById(idUser);
    if(person.getAdministrator()) {
      return plans;
    }
    final Office office = this.officeService.findById(idOffice);
    final List<PermissionDto> officePermissions = this.getOfficePermissionDto(office, person);
    for(final Iterator<PlanDto> it = plans.iterator(); it.hasNext(); ) {
      final PlanDto planDto = it.next();
      final List<CanAccessPlan> canAccessPlans = this.planPermissionRepository.findByIdPlanAndIdPerson(
        planDto.getId(),
        idUser
      );
      canAccessPlans.removeIf(c -> c.getPermissionLevel() == PermissionLevelEnum.NONE);
      final List<PermissionDto> planPermissions = new ArrayList<>();
      if(!canAccessPlans.isEmpty()) {
        canAccessPlans.forEach(p -> {
          final PermissionDto dto = new PermissionDto();
          dto.setId(p.getId());
          dto.setLevel(p.getPermissionLevel());
          dto.setRole(p.getRole());
          planPermissions.add(dto);
        });
      }
      List<PermissionDto> permissions = this.getPermissions(planPermissions, officePermissions);
      permissions.removeIf(c -> c.getLevel() == PermissionLevelEnum.NONE);
      if(permissions.isEmpty()) {
        permissions = this.getPermissionReadWorkpack(planDto.getId(), idUser);
      }
      if(permissions.isEmpty()) {
        it.remove();
        continue;
      }
      planDto.setPermissions(permissions);
    }
    return plans;
  }

  private List<PermissionDto> getOfficePermissionDto(
    final Office office,
    final Person person
  ) {
    final List<CanAccessOffice> canAccessOffices = this.officePermissionService.findByOfficeAndPerson(
      office.getId(),
      person.getId()
    );
    return canAccessOffices.stream().map(c -> {
      final PermissionDto permissionDto = new PermissionDto();
      permissionDto.setRole(c.getRole());
      permissionDto.setLevel(c.getPermissionLevel());
      permissionDto.setId(c.getId());
      return permissionDto;
    }).collect(Collectors.toList());
  }

  private List<PermissionDto> getPermissions(
    final List<PermissionDto> planPermissions,
    final List<PermissionDto> officePermissions
  ) {
    if(planPermissions != null && planPermissions.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getLevel()))) {
      return planPermissions;
    }
    if(officePermissions.stream().anyMatch(c -> PermissionLevelEnum.EDIT.equals(c.getLevel()))) {
      return officePermissions;
    }
    return (planPermissions == null || planPermissions.isEmpty()) ? officePermissions : planPermissions;
  }

  private List<PermissionDto> getPermissionReadWorkpack(
    final Long idPlan,
    final Long idUser
  ) {
    final List<PermissionDto> permissions = new ArrayList<>();
    if(this.hasPermissionReadWorkpack(idPlan, idUser)) {
      final PermissionDto dto = new PermissionDto();
      dto.setId(0L);
      dto.setLevel(PermissionLevelEnum.READ);
      dto.setRole("user");
      permissions.add(dto);
    }
    return permissions;
  }

  private boolean hasPermissionReadWorkpack(
    final Long idPlan,
    final Long idUser
  ) {
    return this.planPermissionRepository.hasWorkpackPermission(idPlan, idUser);
  }

  public boolean hasPermissionPlanWorkpack(
    final Long idPlan,
    final Long idUser
  ) {
    if(this.hasPermissionPlan(idPlan, idUser)) {
      return true;
    }
    return this.hasPermissionReadWorkpack(idPlan, idUser);
  }

  public boolean hasPermissionPlan(
    final Long idPlan,
    final Long idUser
  ) {
    return this.planPermissionRepository.hasPermissionPlan(idPlan, idUser);
  }

  public Plan findNotLinkedBelongsTo(final Long idWorkpack) {
    return this.planRepository.findPlanWithNotLinkedBelongsToRelationship(idWorkpack)
      .orElseThrow(() -> new NegocioException(PLAN_NOT_FOUND));
  }

  public boolean hasLinkWithWorkpack(
    final Long idWorkpack,
    final Long idPlan
  ) {
    final List<BelongsTo> belongsTos = this.planRepository.hasLinkWithWorkpack(idWorkpack, idPlan);
    return Optional.ofNullable(belongsTos)
      .map(relation -> relation.stream().anyMatch(a -> Boolean.TRUE.equals(a.getLinked())))
      .orElse(false);
  }

  public boolean existsById(Long idPlan) {
    return this.planRepository.existsById(idPlan);
  }
}

