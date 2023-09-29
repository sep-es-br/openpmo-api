package br.gov.es.openpmo.dto.dashboards.datasheet;

public class WorkpacksByModelResponse {

  private Long idWorkpackModel;

  private Long quantity;

  private String modelName;

  private String icon;

  private Long level;

  public static WorkpacksByModelResponse from(final WorkpackByModelQueryResult from) {
    final WorkpacksByModelResponse to = new WorkpacksByModelResponse();
    to.setIdWorkpackModel(from.getIdWorkpackModel());
    to.setQuantity(from.getQuantity());
    to.setModelName(from.getWorkpackName());
    to.setIcon(from.getIcon());
    to.setLevel(from.getLevel());
    return to;
  }

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }

  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
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

  public Long getLevel() {
    return level;
  }

  public void setLevel(Long level) {
    this.level = level;
  }

}
