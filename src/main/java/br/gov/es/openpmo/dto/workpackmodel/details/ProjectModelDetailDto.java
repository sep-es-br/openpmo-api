package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class ProjectModelDetailDto extends WorkpackModelDetailDto {


  public static ProjectModelDetailDto of(final WorkpackModel workpackModel) {
    return (ProjectModelDetailDto) WorkpackModelDetailDto.of(workpackModel, ProjectModelDetailDto::new);
  }

  @Override
  public String getType() {
    return "ProjectModel";
  }

}
