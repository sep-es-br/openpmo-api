package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.reports.ReportFormat;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ReportRequest {

  @NotNull
  private final Long idReportModel;

  private final List<ReportParamsRequest> params;

  private final List<Long> scope;

  @NotNull
  private final ReportFormat format;

  public ReportRequest(Long idReportModel, List<ReportParamsRequest> params, List<Long> scope, ReportFormat format) {
    this.idReportModel = idReportModel;
    this.params = params;
    this.scope = scope;
    this.format = format;
  }

  public Long getIdReportModel() {
    return idReportModel;
  }

  public List<ReportParamsRequest> getParams() {
    return params;
  }

  public List<Long> getScope() {
    return scope;
  }

  public ReportFormat getFormat() {
    return format;
  }

}
