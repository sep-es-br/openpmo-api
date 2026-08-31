package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CriteriaGroupModel extends GroupModel {

  private Double weight;

  private CriteriaOperation operation;

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

}
