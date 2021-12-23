package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class BaselineCostDetail {

  private final List<CostDetailItem> costDetails = new ArrayList<>();
  private BigDecimal variation;
  private BigDecimal currentValue;
  private BigDecimal proposedValue;

  public BaselineCostDetail() {
    this.updateValues();
  }

  private void updateValues() {
    this.updateCurrentValue();
    this.updateProposedValue();
    if(this.hasCostVariablesInvalid()) {
      this.variation = null;
      return;
    }
    this.calculateVariation();
  }

  private boolean hasCostVariablesInvalid() {
    return this.currentValue == null || this.proposedValue == null;
  }

  private void calculateVariation() {
    final BigDecimal difference = this.currentValue
      .subtract(this.proposedValue);

    if(difference.compareTo(BigDecimal.ZERO) == 0) {
      this.variation = null;
      return;
    }

    this.variation = difference
      .divide(this.currentValue, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);
  }

  private void updateProposedValue() {
    final List<BigDecimal> allProposedValues = this.costDetails.stream()
      .map(CostDetailItem::getProposedValue)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    this.proposedValue = allProposedValues.isEmpty() ? null : allProposedValues
      .stream()
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void updateCurrentValue() {
    final List<BigDecimal> allCurrentValues = this.costDetails.stream()
      .map(CostDetailItem::getCurrentValue)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    this.currentValue = allCurrentValues.isEmpty() ? null : allCurrentValues
      .stream()
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public BigDecimal getCurrentValue() {
    return this.currentValue;
  }

  public BigDecimal getProposedValue() {
    return this.proposedValue;
  }

  public List<CostDetailItem> getCostDetails() {
    return Collections.unmodifiableList(this.costDetails);
  }

  public void addDetail(final Collection<? extends CostDetailItem> itens) {
    this.costDetails.addAll(itens);
    this.updateValues();
  }

  public void addDetail(final CostDetailItem item) {
    this.costDetails.add(item);
    this.updateValues();
  }

  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
    this.costDetails.forEach(CostDetailItem::roundData);
  }

}
