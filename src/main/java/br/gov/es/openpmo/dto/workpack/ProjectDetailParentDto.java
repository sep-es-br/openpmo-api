package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ProjectDetailParentDto extends WorkpackDetailParentDto {

  public static ProjectDetailParentDto of(final Workpack workpack) {
    return (ProjectDetailParentDto) WorkpackDetailParentDto.of(workpack, ProjectDetailParentDto::new);
  }

}
