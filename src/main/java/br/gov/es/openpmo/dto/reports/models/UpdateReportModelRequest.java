package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.reports.ReportFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UpdateReportModelRequest {

  @NotNull
  private final Long id;

  @NotNull
  @NotBlank
  private final String name;

  private final String fullName;

  private final ReportFormat preferredOutputFormat;

  private final List<PropertyModelDto> paramModels;

  private final List<UpdateReportModelFileItem> files;

  public UpdateReportModelRequest(
    final Long id,
    final String name,
    final String fullName,
    final ReportFormat preferredOutputFormat,
    final List<PropertyModelDto> paramModels,
    final List<UpdateReportModelFileItem> files
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.preferredOutputFormat = preferredOutputFormat;
    this.paramModels = paramModels;
    this.files = files;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public ReportFormat getPreferredOutputFormat() {
    return this.preferredOutputFormat;
  }

  public List<PropertyModelDto> getParamModels() {
    return this.paramModels;
  }

  public List<UpdateReportModelFileItem> getFiles() {
    return this.files;
  }

}
