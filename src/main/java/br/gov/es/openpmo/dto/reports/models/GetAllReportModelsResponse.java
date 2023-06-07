package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.model.reports.ReportFormat;

public class GetAllReportModelsResponse {

  private Long id;

  private Long idPlanModel;

  private String name;

  private String fullName;

  private Boolean active;

  private ReportFormat preferredOutputFormat;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIdPlanModel() {
    return idPlanModel;
  }

  public void setIdPlanModel(Long idPlanModel) {
    this.idPlanModel = idPlanModel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public ReportFormat getPreferredOutputFormat() {
    return preferredOutputFormat;
  }

  public void setPreferredOutputFormat(ReportFormat preferredOutputFormat) {
    this.preferredOutputFormat = preferredOutputFormat;
  }

}
