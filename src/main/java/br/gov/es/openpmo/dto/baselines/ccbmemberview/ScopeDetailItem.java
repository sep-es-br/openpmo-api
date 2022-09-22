package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

public class ScopeDetailItem {

  private final String icon;
  private final String description;
  @JsonProperty("currentValue")
  private final BigDecimal currentWork;
  @JsonProperty("proposedValue")
  private final BigDecimal proposedWork;
  @JsonIgnore
  private final BigDecimal currentCost;
  @JsonIgnore
  private final BigDecimal proposedCost;
  private final String unitName;
  @JsonIgnore
  private final BigDecimal unitCost;
  @JsonIgnore
  private final boolean hasPreviousBaseline;
  private BigDecimal variation;
  @JsonIgnore
  private BigDecimal variationValue;


  public ScopeDetailItem(
    final String icon,
    final String description,
    final String unitName,
    final StepCollectedData stepCollectedData,
    final boolean hasPreviousBaseline
  ) {
    this.icon = icon;
    this.description = description;
    this.currentWork = stepCollectedData.work.getCurrentValue();
    this.proposedWork = stepCollectedData.work.getProposedValue();
    this.currentCost = stepCollectedData.cost.getCurrentValue();
    this.proposedCost = stepCollectedData.cost.getProposedValue();
    this.unitName = unitName;
    this.hasPreviousBaseline = hasPreviousBaseline;
    this.unitCost = new UnitCostCalculator(
      this.proposedCost,
      this.proposedWork,
      this.currentCost,
      this.currentWork
    ).calculate();
    this.calculateVariation();
  }

  private void calculateVariation() {
    if((this.proposedWork == null && this.currentWork == null)) {
      this.variation = null;
      return;
    }

    if(this.currentWork == null || this.currentWork.compareTo(ZERO) == 0) {
      this.variation = this.hasPreviousBaseline ? ONE_HUNDRED : ZERO;
      this.variationValue = this.unitCost;
      return;
    }

    final BigDecimal plannedProportional = Optional.ofNullable(this.proposedWork)
      .orElse(ZERO)
      .divide(this.currentWork, 6, RoundingMode.HALF_UP);

    this.variationValue = plannedProportional.subtract(ONE)
      .multiply(this.unitCost);
    this.variation = plannedProportional.subtract(ONE).multiply(ONE_HUNDRED);
  }

  public BigDecimal getVariationValue() {
    return this.variationValue;
  }

  public String getUnitName() {
    return this.unitName;
  }

  public String getIcon() {
    return this.icon;
  }

  public String getDescription() {
    return this.description;
  }

  public BigDecimal getCurrentWork() {
    return this.currentWork;
  }

  public BigDecimal getProposedWork() {
    return this.proposedWork;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public BigDecimal getCurrentCost() {
    return this.currentCost;
  }


  public BigDecimal getProposedCost() {
    return this.proposedCost;
  }

  public BigDecimal getUnitCost() {
    return this.unitCost;
  }


  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
  }

}
