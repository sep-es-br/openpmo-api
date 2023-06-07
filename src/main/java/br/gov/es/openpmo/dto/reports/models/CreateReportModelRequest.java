package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.reports.ReportFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateReportModelRequest {

  @NotNull
  private Long idPlanModel;

  @NotNull
  @NotBlank
  private String name;

  private String fullName;

  private Boolean active;

  private ReportFormat preferredOutputFormat;

  private List<PropertyModelDto> paramModels;

  private List<CreateReportModelFileItem> files;

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public void setIdPlanModel(final Long idPlanModel) {
    this.idPlanModel = idPlanModel;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public ReportFormat getPreferredOutputFormat() {
    return this.preferredOutputFormat;
  }

  public void setPreferredOutputFormat(final ReportFormat preferredOutputFormat) {
    this.preferredOutputFormat = preferredOutputFormat;
  }

  public List<PropertyModelDto> getParamModels() {
    return this.paramModels;
  }

  public void setParamModels(final List<PropertyModelDto> paramModels) {
    this.paramModels = paramModels;
  }

  public List<CreateReportModelFileItem> getFiles() {
    return this.files;
  }

  public void setFiles(final List<CreateReportModelFileItem> files) {
    this.files = files;
  }

}
