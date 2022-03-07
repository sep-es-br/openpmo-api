package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class UnitCostCalculator {

    private final BigDecimal foreseenCost;
    private final BigDecimal foreseenWork;
    private final BigDecimal plannedCost;
    private final BigDecimal plannedWork;

    public UnitCostCalculator(
            final BigDecimal foreseenCost,
            final BigDecimal foreseenWork,
            final BigDecimal plannedCost,
            final BigDecimal plannedWork
    ) {
        this.foreseenCost = foreseenCost;
        this.foreseenWork = foreseenWork;
        this.plannedCost = plannedCost;
        this.plannedWork = plannedWork;
    }

    public BigDecimal calculate() {
        if (notHasCurrentAndProposedValueToCalculateUnitCost(this.foreseenCost, this.plannedCost)) {
            return BigDecimal.ONE;
        }

        if (this.foreseenCost != null) {
            return this.foreseenCost.divide(this.foreseenWork, 6, RoundingMode.HALF_UP);
        }

        return this.foreseenWork == null
                ? this.plannedCost.divide(this.plannedWork, 6, RoundingMode.HALF_UP)
                : this.plannedCost.divide(this.foreseenWork, 6, RoundingMode.HALF_UP);
    }

    private static boolean notHasCurrentAndProposedValueToCalculateUnitCost(
            final Comparable<? super BigDecimal> forseenCost,
            final Comparable<? super BigDecimal> plannedCost
    ) {
        return hasValue(forseenCost) && hasValue(plannedCost);
    }

    private static boolean hasValue(final Comparable<? super BigDecimal> value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

}
