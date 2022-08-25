package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class ProjectModelDto extends WorkpackModelDto {

  private static final String PROJECT_MODEL = "ProjectModel";


  public static ProjectModelDto of(final WorkpackModel workpackModel) {
    return (ProjectModelDto) WorkpackModelDto.of(
      workpackModel,
      ProjectModelDto::new
    );
  }

  @Override
  public String getType() {
    return PROJECT_MODEL;
  }

}
