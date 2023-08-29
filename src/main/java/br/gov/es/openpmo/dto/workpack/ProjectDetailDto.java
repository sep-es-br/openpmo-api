package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ProjectDetailDto extends WorkpackDetailDto {

  public static ProjectDetailDto of(final Workpack workpack) {
    return (ProjectDetailDto) WorkpackDetailDto.of(workpack, ProjectDetailDto::new);
  }

}
