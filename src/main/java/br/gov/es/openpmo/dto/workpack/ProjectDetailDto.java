package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ProjectDetailDto extends WorkpackDetailDto {

  private boolean fromPreProjectInStructuring;

  public boolean isFromPreProjectInStructuring() {
    return this.fromPreProjectInStructuring;
  }

  public void setFromPreProjectInStructuring(final boolean fromPreProjectInStructuring) {
    this.fromPreProjectInStructuring = fromPreProjectInStructuring;
  }

  public static ProjectDetailDto of(final Workpack workpack) {
    return (ProjectDetailDto) WorkpackDetailDto.of(workpack, ProjectDetailDto::new);
  }

}
