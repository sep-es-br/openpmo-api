package br.gov.es.openpmo.dto.workpack.breakdown.structure;

public class WorkpackModelRepresentation {

  private Long idWorkpackModel;

  private String workpackModelName;

  private String workpackModelType;

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }

  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public String getWorkpackModelName() {
    return workpackModelName;
  }

  public void setWorkpackModelName(String workpackModelName) {
    this.workpackModelName = workpackModelName;
  }

  public String getWorkpackModelType() {
    return workpackModelType;
  }

  public void setWorkpackModelType(String workpackModelType) {
    this.workpackModelType = workpackModelType;
  }

}
