package br.gov.es.openpmo.dto.workpackmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GetNextPositionRequest {

  private final Long idPlanModel;

  private final Long idWorkpackModel;

  private GetNextPositionRequest(
    final Long idPlanModel,
    final Long idWorkpackModel
  ) {
    this.idPlanModel = idPlanModel;
    this.idWorkpackModel = idWorkpackModel;
  }

  public static GetNextPositionRequest of(
    final Long idPlanModel,
    final Long idWorkpackModel
  ) {
    return new GetNextPositionRequest(
      idPlanModel,
      idWorkpackModel
    );
  }

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  @JsonIgnore
  public boolean hasIdWorkpackModel() {
    return this.idWorkpackModel != null;
  }

  @JsonIgnore
  public boolean hasIdPlanModel() {
    return this.idPlanModel != null;
  }

}
