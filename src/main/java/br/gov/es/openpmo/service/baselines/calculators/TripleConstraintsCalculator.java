package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.CostDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ProposedAndCurrentValue;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScopeDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintOutput;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class TripleConstraintsCalculator implements ITripleConstraintsCalculator {


  private final BaselineRepository repository;
  private final ConsumesRepository consumesRepository;
  private final WorkpackRepository workpackRepository;
  private final ScheduleRepository scheduleRepository;
  private final IStepDataCollector stepDataCollector;

  @Autowired
  public TripleConstraintsCalculator(
    final BaselineRepository repository,
    final ConsumesRepository consumesRepository,
    final WorkpackRepository workpackRepository,
    final ScheduleRepository scheduleRepository,
    final IStepDataCollector stepDataCollector
  ) {
    this.repository = repository;
    this.consumesRepository = consumesRepository;
    this.workpackRepository = workpackRepository;
    this.scheduleRepository = scheduleRepository;
    this.stepDataCollector = stepDataCollector;
  }

  private static ScopeDetailItem buildScopeItem(
    final Workpack master,
    final String name,
    final StepCollectedData stepCollectedData,
    final String unitName,
    final boolean hasPreviousBaseline
  ) {
    return new ScopeDetailItem(
      master.getIcon(),
      name,
      unitName,
      stepCollectedData,
      hasPreviousBaseline
    );
  }

  private static CostDetailItem buildCostItem(
    final Workpack master,
    final ProposedAndCurrentValue proposedAndCurrentValue,
    final String name
  ) {
    return new CostDetailItem(
      master.getIcon(),
      name,
      proposedAndCurrentValue.getCurrentValue(),
      proposedAndCurrentValue.getProposedValue()
    );
  }

  private static void addStepDataToCollector(
    final StepCollectedData stepCollectedData,
    final Collection<? extends Consumes> consumesProposed,
    final Collection<? extends Consumes> consumesCurrent
  ) {
    stepCollectedData.work.addCurrentValue(getTotalPlannedWorkOfStep(consumesCurrent));
    stepCollectedData.work.addProposedValue(getTotalPlannedWorkOfStep(consumesProposed));

    stepCollectedData.cost.addCurrentValue(getTotalCostOfStep(consumesCurrent));
    stepCollectedData.cost.addProposedValue(getTotalCostOfStep(consumesProposed));
  }

  private static BigDecimal getTotalPlannedWorkOfStep(final Collection<? extends Consumes> consumesProposed) {

    if(consumesProposed.isEmpty()) {
      return null;
    }

    BigDecimal acc = BigDecimal.ZERO;
    for (Consumes consumes : consumesProposed) {
      Step step = consumes.getStep();
      BigDecimal plannedWork = step.getPlannedWork();
      if (plannedWork != null) {
        acc = acc.add(plannedWork);
      }
    }
    return acc;
  }

  private static BigDecimal getTotalCostOfStep(final Collection<? extends Consumes> consumes) {
    if(consumes.isEmpty()) {
      return null;
    }
    BigDecimal acc = BigDecimal.ZERO;
    for (Consumes consume : consumes) {
      BigDecimal plannedCost = consume.getPlannedCost();
      if (plannedCost != null) {
        acc = acc.add(plannedCost);
      }
    }
    return acc;
  }

  @Override
  public TripleConstraintOutput calculate(final Long idBaseline) {
    final Workpack master = this.findProjectMasterOfBaseline(idBaseline);

    final boolean isCancelationBaseline = this.isCancelationBaseline(idBaseline);

    final Long idBaselineReference = this.findPreviousBaseline(idBaseline, master)
      .map(Baseline::getId)
      .orElse(null);

    final Set<Workpack> masterDeliverables = this.findDeliverablesChildrensId(master);

    return this.buildCostDetail(
      masterDeliverables,
      idBaseline,
      idBaselineReference,
      isCancelationBaseline
    );
  }

  private boolean isCancelationBaseline(final Long idBaseline) {
    return this.repository.isCancelBaseline(idBaseline);
  }

  private Set<Workpack> findDeliverablesChildrensId(final Workpack master) {
    return this.repository.findDeliverableWorkpacksOfProjectMaster(master.getId());
  }

  private Optional<Baseline> findPreviousBaseline(
    final Long idBaseline,
    final Workpack master
  ) {
    return this.repository.findPreviousBaseline(idBaseline, master.getId());
  }

  private Workpack findProjectMasterOfBaseline(final Long idBaseline) {
    return this.repository.findWorkpackByBaselineId(idBaseline)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private TripleConstraintOutput buildCostDetail(
    final Iterable<? extends Workpack> masterDeliverables,
    final Long idBaseline,
    final Long idBaselineReference,
    final boolean isCancelationBaseline
  ) {
    final TripleConstraintOutput output = new TripleConstraintOutput();

    for(final Workpack master : masterDeliverables) {
      final Optional<Schedule> maybeSchedule = this.scheduleRepository.findScheduleByWorkpackId(master.getId());

      if(!maybeSchedule.isPresent()) continue;

      if(isCancelationBaseline) {
        this.addItemOfWorkpack(
          master,
          idBaselineReference,
          maybeSchedule.get(),
          output
        );
      }
      else {
        this.addItemOfBaseline(
          idBaseline,
          idBaselineReference,
          master,
          maybeSchedule.get(),
          output
        );
      }

    }

    return output;
  }

  private void addItemOfWorkpack(
    final Workpack master,
    final Long idBaselineReference,
    final Schedule schedule,
    final TripleConstraintOutput tripleConstraint
  ) {
    if(master.isDeleted()) return;

    final String name = master.getName();

    final StepCollectedData stepCollectedData = this.collectStepDataOfMaster(
      idBaselineReference,
      schedule.getSteps()
    );

    if(stepCollectedData.isNull()) return;

    final ScheduleInterval proposedInterval = ScheduleInterval.ofSchedule(schedule);
    final ScheduleInterval currentInterval = this.findSnapshotOfScheduleAsScheduleInterval(
      idBaselineReference,
      schedule
    );

    tripleConstraint.addScheduleDetail(new ScheduleDetailItem(
      master.getIcon(),
      name,
      proposedInterval,
      currentInterval
    ));
    this.addCostAndScopeItem(master, tripleConstraint, name, stepCollectedData, idBaselineReference != null);
  }

  private void addCostAndScopeItem(
    final Workpack master,
    final TripleConstraintOutput tripleConstraint,
    final String name,
    final StepCollectedData stepCollectedData,
    final boolean hasPreviousBaseline
  ) {
    final CostDetailItem costItem = buildCostItem(master, stepCollectedData.cost, name);
    tripleConstraint.addCostDetail(costItem);

    final String unitName = this.findUnitMeasureOfWorkpack(master);
    final ScopeDetailItem scopeItem = buildScopeItem(
      master,
      name,
      stepCollectedData,
      unitName,
      hasPreviousBaseline
    );
    tripleConstraint.addScopeDetail(scopeItem);
  }

  private String findUnitMeasureOfWorkpack(final Workpack master) {
    return this.workpackRepository.findUnitMeasureNameOfDeliverableWorkpack(master.getId())
      .orElse(null);
  }

  private ScheduleInterval findSnapshotOfScheduleAsScheduleInterval(
    final Long idBaseline,
    final Schedule master
  ) {
    return this.scheduleRepository.findSnapshotByMasterIdAndBaselineId(master.getId(), idBaseline)
      .map(ScheduleInterval::ofSchedule)
      .orElse(null);
  }

  private StepCollectedData collectStepDataOfMaster(
    final Long idBaselineReference,
    final Iterable<? extends Step> steps
  ) {

    final StepCollectedData stepCollectedData = new StepCollectedData();

    for(final Step stepMaster : steps) {

      final Set<Consumes> consumesProposed = stepMaster.getConsumes();

      final Set<Consumes> consumesCurrent = this.findConsumesSnapshotByStepMasterAndBaselineId(
        idBaselineReference,
        stepMaster
      );
      addStepDataToCollector(stepCollectedData, consumesProposed, consumesCurrent);
    }
    return stepCollectedData;
  }

  private Set<Consumes> findConsumesSnapshotByStepMasterAndBaselineId(
    final Long idBaseline,
    final Step stepMaster
  ) {
    return this.consumesRepository.findAllSnapshotConsumesOfStepMaster(
      idBaseline,
      stepMaster.getId()
    );
  }

  private void addItemOfBaseline(
    final Long idBaseline,
    final Long idBaselineReference,
    final Workpack master,
    final Schedule masterSchedule,
    final TripleConstraintOutput tripleConstraint
  ) {
    final String name = master.getName();

    final StepCollectedData stepCollectedData = this.stepDataCollector.collect(
      idBaseline,
      idBaselineReference,
      masterSchedule.getId()
    );

    if(stepCollectedData.isNull()) return;

    final ScheduleDetailItem scheduleItem = this.buildScheduleItemOfBaseline(
      idBaseline,
      idBaselineReference,
      master,
      name,
      masterSchedule
    );

    tripleConstraint.addScheduleDetail(scheduleItem);

    this.addCostAndScopeItem(
      master,
      tripleConstraint,
      name,
      stepCollectedData,
      idBaselineReference != null
    );
  }

  private ScheduleDetailItem buildScheduleItemOfBaseline(
    final Long idBaseline,
    final Long idBaselineReference,
    final Workpack master,
    final String name,
    final Schedule masterSchedule
  ) {
    final ScheduleInterval proposedInterval = this.findSnapshotOfScheduleAsScheduleInterval(
      idBaseline,
      masterSchedule
    );

    final ScheduleInterval currentInterval = this.findSnapshotOfScheduleAsScheduleInterval(
      idBaselineReference,
      masterSchedule
    );

    return new ScheduleDetailItem(
      master.getIcon(),
      name,
      proposedInterval,
      currentInterval
    );
  }

}
