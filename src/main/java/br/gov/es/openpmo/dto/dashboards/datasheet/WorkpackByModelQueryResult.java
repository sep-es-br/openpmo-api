package br.gov.es.openpmo.dto.dashboards.datasheet;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackByModelQueryResult {

  private Long idWorkpackModel;

  private Long idPlan;

  private Long quantity;

  private String singularName;

  private String pluralName;

  private String icon;

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public void setIdWorkpackModel(final Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public Long getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final Long quantity) {
    this.quantity = quantity;
  }

  public String getSingularName() {
    return this.singularName;
  }

  public void setSingularName(final String singularName) {
    this.singularName = singularName;
  }

  public String getPluralName() {
    return this.pluralName;
  }

  public void setPluralName(final String pluralName) {
    this.pluralName = pluralName;
  }

  public String getWorkpackName() {
    return this.quantity == 1 ? this.singularName : this.pluralName;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

}
