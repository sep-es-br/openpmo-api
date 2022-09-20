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

public class BaselineScopeDetail {

  private final List<ScopeDetailItem> scopeDetails = new ArrayList<>();
  private BigDecimal currentScopePercent = ONE_HUNDRED;
  private BigDecimal variation;
  private BigDecimal proposedScopePercent;

  public BigDecimal getVariation() {
    return this.variation;
  }

  public BigDecimal getCurrentScopePercent() {
    return this.currentScopePercent;
  }

  public BigDecimal getProposedScopePercent() {
    return this.proposedScopePercent;
  }

  public List<ScopeDetailItem> getScopeDetails() {
    return Collections.unmodifiableList(this.scopeDetails);
  }

  public void addDetail(final Collection<? extends ScopeDetailItem> itens) {
    this.scopeDetails.addAll(itens);
  }

  void addDetail(final ScopeDetailItem item) {
    this.scopeDetails.add(item);
    this.calculateVariation();
  }

  private void calculateVariation() {

    final BigDecimal sumOfTotalPartialVariation = this.scopeDetails.stream()
      .map(ScopeDetailItem::getVariationValue)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    final List<BigDecimal> totalCost = this.scopeDetails.stream()
      .map(ScopeDetailItem::getCostCurrent)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    if(totalCost.isEmpty()) {
      this.proposedScopePercent = ONE_HUNDRED;
      this.currentScopePercent = null;
      return;
    }

    final BigDecimal sumOfTotalCost = totalCost.stream()
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.variation = sumOfTotalPartialVariation
      .divide(sumOfTotalCost, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    this.proposedScopePercent = this.currentScopePercent.subtract(this.variation);
    this.currentScopePercent = ONE_HUNDRED;
  }


  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
    this.proposedScopePercent = roundOneDecimal(this.proposedScopePercent);
    this.scopeDetails.forEach(ScopeDetailItem::roundData);
  }

}
