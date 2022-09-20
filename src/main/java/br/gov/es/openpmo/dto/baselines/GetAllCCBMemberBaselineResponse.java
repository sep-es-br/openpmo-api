package br.gov.es.openpmo.dto.baselines;

import java.util.List;

public class GetAllCCBMemberBaselineResponse {

  private final Long idWorkpack;

  private final String nameWorkpack;

  private final List<GetAllBaselinesResponse> baselines;

  public GetAllCCBMemberBaselineResponse(
    final Long idWorkpack,
    final String nameWorkpack,
    final List<GetAllBaselinesResponse> baselines
  ) {
    this.idWorkpack = idWorkpack;
    this.nameWorkpack = nameWorkpack;
    this.baselines = baselines;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getNameWorkpack() {
    return this.nameWorkpack;
  }

  public List<GetAllBaselinesResponse> getBaselines() {
    return this.baselines;
  }

}
