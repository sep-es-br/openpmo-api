package br.gov.es.openpmo.dto.workpack.breakdown.structure;

public class WorkpackModelRepresentation {

  private Long idWorkpackModel;

  private String workpackModelName;

  private String workpackModelType;

  private Long workpackModelPosition;

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public void setIdWorkpackModel(final Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public String getWorkpackModelName() {
    return this.workpackModelName;
  }

  public void setWorkpackModelName(final String workpackModelName) {
    this.workpackModelName = workpackModelName;
  }

  public String getWorkpackModelType() {
    return this.workpackModelType;
  }

  public void setWorkpackModelType(final String workpackModelType) {
    this.workpackModelType = workpackModelType;
  }

  public Long getWorkpackModelPosition() {
    return this.workpackModelPosition;
  }

  public void setWorkpackModelPosition(final Long workpackModelPosition) {
    this.workpackModelPosition = workpackModelPosition;
  }

}
