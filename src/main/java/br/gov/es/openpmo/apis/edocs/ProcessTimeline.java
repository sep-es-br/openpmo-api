package br.gov.es.openpmo.apis.edocs;

import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;

public class ProcessTimeline {

  private final long daysDuration;
  private final ProcessHistoryResponse detail;

  public ProcessTimeline(
    final long daysDuration,
    final ProcessHistoryResponse detail
  ) {

    this.daysDuration = daysDuration;
    this.detail = detail;
  }

  public long daysDuration() {
    return this.daysDuration;
  }

  public ProcessHistoryResponse detail() {
    return this.detail;
  }

}
