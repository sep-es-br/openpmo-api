package br.gov.es.openpmo.dto.dashboards.datasheet;

public class WorkpacksByModelResponse {

  private Long quantity;

  private String modelName;

  private String icon;

  public static WorkpacksByModelResponse from(final WorkpackByModelQueryResult from) {
    final WorkpacksByModelResponse to = new WorkpacksByModelResponse();
    to.setQuantity(from.getQuantity());
    to.setModelName(from.getWorkpackName());
    to.setIcon(from.getIcon());
    return to;
  }

  public Long getQuantity() {
    return this.quantity;
  }

  public void setQuantity(final Long quantity) {
    this.quantity = quantity;
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(final String modelName) {
    this.modelName = modelName;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

}
