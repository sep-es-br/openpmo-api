package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.ScheduleDataChart;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.INTERVAL_DATE_IN_BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
@Deprecated
public class GetTripleConstraintData implements IGetTripleConstraintData {

  private final BaselineRepository repository;
  private final WorkpackRepository workpackRepository;
  private final ScheduleRepository scheduleRepository;
  private final IGetCostAndScope getStepTotalCost;

  @Autowired
  public GetTripleConstraintData(
    final BaselineRepository repository,
    final WorkpackRepository workpackRepository,
    final ScheduleRepository scheduleRepository,
    final IGetCostAndScope getStepTotalCost
  ) {
    this.repository = repository;
    this.getStepTotalCost = getStepTotalCost;
    this.workpackRepository = workpackRepository;
    this.scheduleRepository = scheduleRepository;
  }

  @Override
  public TripleConstraintDataChart get(final DashboardParameters parameters) {
    final Long idProject = this.findIdProjectInParentsOf(parameters.getWorkpackId());

    if(Objects.isNull(idProject)) {
      return null;
    }

    final Long idBaseline = this.useIdBaselineOrFetchActiveIdBaselineIfNull(
      parameters,
      idProject
    );

    final Set<Workpack> deliverables = this.findMasterDeliverables(idProject);

    final TripleConstraintDataChart tripleConstraint = new TripleConstraintDataChart();

    this.buildTripleConstraintData(
      parameters,
      idBaseline,
      idProject,
      deliverables,
      tripleConstraint
    );

    return tripleConstraint;
  }

  private Long findIdProjectInParentsOf(final Long idWorkpack) {
    final Workpack workpack = this.workpackRepository.findById(idWorkpack, 0)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));

    if(workpack instanceof Project) {
      return workpack.getId();
    }

    return this.workpackRepository.findProjectInParentsOf(idWorkpack)
      .map(Workpack::getId)
      .orElse(null);
  }

  private Long useIdBaselineOrFetchActiveIdBaselineIfNull(
    final DashboardParameters parameters,
    final Long idProject
  ) {
    return Optional.ofNullable(parameters.getBaselineId())
      .orElseGet(() -> this.findBaselineActive(idProject).getId());
  }

  private Set<Workpack> findMasterDeliverables(final Long idProject) {
    return this.repository.findDeliverableWorkpacksOfProjectMaster(idProject);
  }

  private void buildTripleConstraintData(
    final DashboardParameters parameters,
    final Long idBaseline,
    final Long idProject,
    final Iterable<? extends Workpack> deliverables,
    final TripleConstraintDataChart tripleConstraint
  ) {

    this.buildScheduleDataChart(
      idBaseline,
      idProject,
      tripleConstraint,
      parameters.getYearMonth()
    );

    for(final Workpack deliverable : deliverables) {
      final Optional<Schedule> maybeSchedule;
      maybeSchedule = this.scheduleRepository.findScheduleByWorkpackId(deliverable.getId());

      if(!maybeSchedule.isPresent()) continue;

      final Schedule schedule = maybeSchedule.get();

      this.sumCostAndWorkOfSteps(
        parameters,
        idBaseline,
        tripleConstraint,
        schedule.getSteps()
      );
    }
  }

  private Baseline findBaselineActive(final Long idProject) {
    return this.repository.findActiveBaseline(idProject)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private void buildScheduleDataChart(
    final Long idBaseline,
    final Long idProject,
    final TripleConstraintDataChart tripleConstraint,
    final YearMonth yearMonth
  ) {
    final DateIntervalQuery plannedInterval = this.findIntervalInSnapshots(idBaseline);
    final DateIntervalQuery foreseenInterval = this.findIntervalInProject(idProject);

    tripleConstraint.setSchedule(
      ScheduleDataChart.ofIntervals(
        plannedInterval,
        foreseenInterval,
        yearMonth
      )
    );
  }

  private void sumCostAndWorkOfSteps(
    final DashboardParameters parameters,
    final Long idBaseline,
    final TripleConstraintDataChart tripleConstraint,
    final Collection<? extends Step> steps
  ) {
    final CostAndScopeData costAndScopeData = this.getStepTotalCost.get(
      idBaseline,
      parameters.getYearMonth(),
      steps
    );

    tripleConstraint.sumCostData(costAndScopeData.getCostDataChart());
    tripleConstraint.sumScopeData(costAndScopeData.getScopeDataChart());
  }

  private DateIntervalQuery findIntervalInSnapshots(final Long idBaseline) {
    return this.repository.findScheduleIntervalInSnapshotsOfBaseline(idBaseline)
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
  }

  private DateIntervalQuery findIntervalInProject(final Long idProject) {
    return this.workpackRepository.findIntervalInSchedulesChildrenOf(idProject)
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
  }

}
