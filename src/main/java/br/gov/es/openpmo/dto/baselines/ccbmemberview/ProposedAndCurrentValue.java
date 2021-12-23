package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;

public class ProposedAndCurrentValue {

  private BigDecimal proposedValue;
  private BigDecimal currentValue;

  public void addProposedValue(final BigDecimal proposedValue) {
    if(proposedValue == null) return;

    if(this.proposedValue == null) {
      this.proposedValue = BigDecimal.ZERO;
    }

    this.proposedValue = this.proposedValue.add(proposedValue);
  }

  public void addCurrentValue(final BigDecimal currentValue) {
    if(currentValue == null) return;

    if(this.currentValue == null) {
      this.currentValue = BigDecimal.ZERO;
    }

    this.currentValue = this.currentValue.add(currentValue);
  }

  public BigDecimal getProposedValue() {
    return this.proposedValue;
  }

  public BigDecimal getCurrentValue() {
    return this.currentValue;
  }
}
