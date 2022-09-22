package br.gov.es.openpmo.dto.baselines.ccbmemberview;

public class StepCollectedData {

  public ProposedAndCurrentValue cost = new ProposedAndCurrentValue();
  public ProposedAndCurrentValue work = new ProposedAndCurrentValue();

  public boolean isNull() {
    return this.cost.isNull() && this.work.isNull();
  }

}
