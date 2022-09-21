package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.getOneIfValueZero;

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

  private static boolean hasNoCurrentNorProposedValueToCalculateUnitCost(
    final Comparable<? super BigDecimal> foreseenCost,
    final Comparable<? super BigDecimal> plannedCost
  ) {
    return !hasValue(foreseenCost) && !hasValue(plannedCost);
  }

  private static boolean hasValue(final Comparable<? super BigDecimal> value) {
    return value != null && value.compareTo(BigDecimal.ZERO) != 0;
  }


  public BigDecimal calculate() {
    if(hasNoCurrentNorProposedValueToCalculateUnitCost(this.foreseenCost, this.plannedCost)) {
      return BigDecimal.ONE;
    }
    if(hasValue(this.plannedCost)) {
      return this.plannedCost.divide(getOneIfValueZero(this.plannedWork), 6, RoundingMode.HALF_UP);
    }
    if(hasValue(this.foreseenCost)) {
      return this.foreseenCost.divide(getOneIfValueZero(this.foreseenWork), 6, RoundingMode.HALF_UP);
    }
    return BigDecimal.ONE;
  }

}
