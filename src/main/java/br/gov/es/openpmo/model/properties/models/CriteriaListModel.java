package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CriteriaListModel extends ListModel {

  private Double itemValue;
  public Double getItemValue() {
    return this.itemValue;
  }

  public void setItemValue(final Double itemValue) {
    this.itemValue = itemValue;
  }

}
