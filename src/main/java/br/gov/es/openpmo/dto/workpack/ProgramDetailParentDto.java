package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ProgramDetailParentDto extends WorkpackDetailParentDto {

  public static ProgramDetailParentDto of(final Workpack workpack) {
    return (ProgramDetailParentDto) WorkpackDetailParentDto.of(workpack, ProgramDetailParentDto::new);
  }

}
