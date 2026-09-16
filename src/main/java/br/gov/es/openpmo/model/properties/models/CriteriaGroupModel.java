package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CriteriaGroupModel extends GroupModel {

  private CriteriaOperation operation;

  private boolean enablementKey;

  private Double disabledValue;

  private String legend;

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

  public boolean isEnablementKey() {
    return this.enablementKey;
  }

  public void setEnablementKey(final boolean enablementKey) {
    this.enablementKey = enablementKey;
  }

  public Double getDisabledValue() {
    return this.disabledValue;
  }

  public void setDisabledValue(final Double disabledValue) {
    this.disabledValue = disabledValue;
  }

  public String getLegend() {
    return this.legend;
  }

  public void setLegend(final String legend) {
    this.legend = legend;
  }

}
