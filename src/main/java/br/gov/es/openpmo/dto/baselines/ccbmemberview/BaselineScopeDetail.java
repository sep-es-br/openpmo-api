package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class BaselineScopeDetail {

  private final List<ScopeDetailItem> scopeDetails = new ArrayList<>();
  private BigDecimal currentScopePercent = ONE_HUNDRED;
  private BigDecimal variation;
  private BigDecimal proposedScopePercent;
  @JsonIgnore
  private BigDecimal totalUnitCost;
  @JsonIgnore
  private BigDecimal totalVariation;
  @JsonIgnore
  private BigDecimal totalCurrentValue;

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

  public void addDetail(final ScopeDetailItem item) {
    this.scopeDetails.add(item);
    this.calculateVariation();
  }

  private void calculateVariation() {

    this.totalVariation = this.scopeDetails.stream()
      .map(ScopeDetailItem::getVariationValue)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.totalUnitCost = this.scopeDetails.stream()
      .map(ScopeDetailItem::getUnitCost)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.totalCurrentValue = this.scopeDetails.stream()
      .map(ScopeDetailItem::getCurrentWork)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.variation = this.totalVariation.divide(this.totalUnitCost, 6, RoundingMode.HALF_UP)
      .multiply(ONE_HUNDRED);

    if(this.totalCurrentValue.compareTo(BigDecimal.ZERO) == 0) {
      this.proposedScopePercent = ONE_HUNDRED;
      this.currentScopePercent = null;
      this.variation = null;
    }
    else {
      this.currentScopePercent = ONE_HUNDRED;
      this.proposedScopePercent = this.currentScopePercent.add(this.variation);
    }
  }


  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
    this.proposedScopePercent = roundOneDecimal(this.proposedScopePercent);
    this.scopeDetails.forEach(ScopeDetailItem::roundData);
  }

}
