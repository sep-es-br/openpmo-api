package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class ScopeDetailItem {

  private final String icon;
  private final String description;
  private final BigDecimal currentValue;
  private final BigDecimal proposedValue;
  @JsonIgnore
  private final BigDecimal costCurrent;
  @JsonIgnore
  private final BigDecimal costProposed;
  private final String unitName;
  private BigDecimal variation;
  @JsonIgnore
  private BigDecimal variationValue;
  @JsonIgnore
  private BigDecimal difference;
  @JsonIgnore
  private BigDecimal unitCost;

  public ScopeDetailItem(
    final String icon,
    final String description,
    final String unitName,
    final StepCollectedData stepCollectedData
  ) {
    this.icon = icon;
    this.description = description;
    this.currentValue = stepCollectedData.step.getCurrentValue();
    this.proposedValue = stepCollectedData.step.getProposedValue();
    this.costCurrent = stepCollectedData.cost.getCurrentValue();
    this.costProposed = stepCollectedData.cost.getProposedValue();
    this.unitName = unitName;
    this.unitCost = new UnitCostCalculator(
      this.costCurrent,
      this.currentValue,
      this.costProposed,
      this.proposedValue
    ).calculate();
    this.calculateVariation();
  }

  private void calculateVariation() {
    if((this.proposedValue == null && this.currentValue == null)) {
      this.variation = null;
      return;
    }

    this.difference = this.currentValue == null ? this.proposedValue.negate() : this.currentValue.subtract(this.proposedValue);

    this.variationValue = this.unitCost.multiply(this.difference);

    if(this.currentValue == null) return;

    this.variation = this.difference
      .divide(this.currentValue, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);
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

  public BigDecimal getCurrentValue() {
    return this.currentValue;
  }

  public BigDecimal getProposedValue() {
    return this.proposedValue;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public BigDecimal getCostCurrent() {
    return this.costCurrent;
  }

  public BigDecimal getDifference() {
    return this.difference;
  }

  public BigDecimal getCostProposed() {
    return this.costProposed;
  }

  public BigDecimal getUnitCost() {
    return this.unitCost;
  }

  public BigDecimal getVariationValue() {
    return this.variationValue;
  }

  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
  }
}
