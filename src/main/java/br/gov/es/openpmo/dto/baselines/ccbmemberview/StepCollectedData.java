package br.gov.es.openpmo.dto.baselines.ccbmemberview;

public class StepCollectedData {

  public ProposedAndCurrentValue cost = new ProposedAndCurrentValue();
  public ProposedAndCurrentValue step = new ProposedAndCurrentValue();

  public boolean isNull() {
    return this.cost.isNull() && this.step.isNull();
  }

}
