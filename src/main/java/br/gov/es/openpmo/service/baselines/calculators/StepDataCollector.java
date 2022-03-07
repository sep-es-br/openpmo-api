package br.gov.es.openpmo.service.baselines.calculators;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ConsumesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

@Component
public class StepDataCollector implements IStepDataCollector {

    private final ConsumesRepository consumesRepository;

    @Autowired
    public StepDataCollector(final ConsumesRepository consumesRepository) {
        this.consumesRepository = consumesRepository;
    }

    @Override
    public StepCollectedData collect(
            final Long idBaseline,
            final Long idBaselineReference,
            final Iterable<? extends Step> steps
    ) {
        final StepCollectedData stepCollectedData = new StepCollectedData();
        for (final Step stepMaster : steps) {
            addStepDataToCollector(
                    stepCollectedData,
                    this.findConsumesSnapshotByStepMasterAndBaselineId(idBaseline, stepMaster),
                    this.findConsumesSnapshotByStepMasterAndBaselineId(idBaselineReference, stepMaster)
            );
        }

        return stepCollectedData;
    }

    private static void addStepDataToCollector(
            final StepCollectedData stepCollectedData,
            final Collection<? extends Consumes> consumesProposed,
            final Collection<? extends Consumes> consumesCurrent
    ) {
        stepCollectedData.step.addCurrentValue(getTotalPlannedWorkOfStep(consumesCurrent));
        stepCollectedData.step.addProposedValue(getTotalPlannedWorkOfStep(consumesProposed));
        stepCollectedData.cost.addCurrentValue(getTotalCostOfStep(consumesCurrent));
        stepCollectedData.cost.addProposedValue(getTotalCostOfStep(consumesProposed));
    }

    private Set<Consumes> findConsumesSnapshotByStepMasterAndBaselineId(final Long idBaseline, final Step stepMaster) {
        return this.consumesRepository.findAllSnapshotConsumesOfStepMaster(idBaseline, stepMaster.getId());
    }

    private static BigDecimal getTotalPlannedWorkOfStep(final Collection<? extends Consumes> consumesProposed) {
        if (consumesProposed.isEmpty()) {
            return null;
        }
        return consumesProposed.stream()
                .map(Consumes::getStep)
                .map(Step::getPlannedWork)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal getTotalCostOfStep(final Collection<? extends Consumes> consumes) {
        if (consumes.isEmpty()) {
            return null;
        }
        return consumes.stream()
                .map(Consumes::getPlannedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
