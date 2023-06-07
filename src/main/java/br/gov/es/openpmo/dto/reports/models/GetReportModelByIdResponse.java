package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.model.reports.ReportFormat;

import java.util.List;

public class GetReportModelByIdResponse {

  private final Long id;

  private final Long idPlanModel;

  private final String name;

  private final String fullName;

  private final Boolean active;

  private final Boolean compiled;

  private final ReportFormat preferredOutputFormat;

  private final List<PropertyModelDto> paramModels;

  private final List<GetReportModelFileItem> files;

  public GetReportModelByIdResponse(
    final Long id,
    final Long idPlanModel,
    final String name,
    final String fullName,
    final Boolean active,
    final Boolean compiled,
    final ReportFormat preferredOutputFormat,
    final List<PropertyModelDto> paramModels,
    final List<GetReportModelFileItem> files
  ) {
    this.id = id;
    this.idPlanModel = idPlanModel;
    this.name = name;
    this.fullName = fullName;
    this.active = active;
    this.compiled = compiled;
    this.preferredOutputFormat = preferredOutputFormat;
    this.paramModels = paramModels;
    this.files = files;
  }

  public static GetReportModelByIdResponse of(
    final ReportDesign entity,
    final List<PropertyModelDto> properties,
    final List<GetReportModelFileItem> files
  ) {
    return new GetReportModelByIdResponse(
      entity.getId(),
      entity.getIdPlanModel(),
      entity.getName(),
      entity.getFullName(),
      entity.getActive(),
      files.stream().allMatch(GetReportModelFileItem::getCompiled),
      entity.getPreferredOutputFormat(),
      properties,
      files
    );
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public Boolean getActive() {
    return this.active;
  }

  public ReportFormat getPreferredOutputFormat() {
    return this.preferredOutputFormat;
  }

  public List<PropertyModelDto> getParamModels() {
    return this.paramModels;
  }

  public List<GetReportModelFileItem> getFiles() {
    return this.files;
  }

  public Boolean getCompiled() {
    return compiled;
  }
}
