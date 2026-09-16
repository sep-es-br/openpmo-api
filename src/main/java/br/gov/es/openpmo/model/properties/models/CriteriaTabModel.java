package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CriteriaTabModel extends TabModel {

  private CriteriaOperation operation;

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

}
