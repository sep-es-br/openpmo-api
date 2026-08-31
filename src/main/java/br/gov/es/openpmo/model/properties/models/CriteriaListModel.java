package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class CriteriaListModel extends ListModel {

  private Double weight;

  private Double itemValue;

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public Double getItemValue() {
    return this.itemValue;
  }

  public void setItemValue(final Double itemValue) {
    this.itemValue = itemValue;
  }

}
