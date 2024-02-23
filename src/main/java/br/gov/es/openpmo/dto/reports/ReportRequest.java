package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.reports.ReportFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ReportRequest {

  @NotNull
  private final Long idReportModel;

  @NotNull
  private final Long idPlan;

  private final List<ReportParamsRequest> params;

  @NotEmpty
  private final List<@NotNull Long> scope;

  @NotNull
  private final ReportFormat format;

  public ReportRequest(
    final Long idReportModel,
    final Long idPlan,
    final List<ReportParamsRequest> params,
    final List<Long> scope,
    final ReportFormat format
  ) {
    this.idReportModel = idReportModel;
    this.idPlan = idPlan;
    this.params = params;
    this.scope = scope;
    this.format = format;
  }

  public Long getIdReportModel() {
    return this.idReportModel;
  }

  public List<ReportParamsRequest> getParams() {
    return this.params;
  }

  public List<Long> getScope() {
    return this.scope;
  }

  public ReportFormat getFormat() {
    return this.format;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }



}
