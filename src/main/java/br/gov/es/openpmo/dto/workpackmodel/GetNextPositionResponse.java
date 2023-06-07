package br.gov.es.openpmo.dto.workpackmodel;

public class GetNextPositionResponse {

  private final Long nextPosition;

  public GetNextPositionResponse(final Long nextPosition) {
    this.nextPosition = nextPosition;
  }

  public static GetNextPositionResponse of(final Long position) {
    return new GetNextPositionResponse(position);
  }

  public Long getNextPosition() {
    return this.nextPosition;
  }

}
