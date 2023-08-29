package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ProgramDetailDto extends WorkpackDetailDto {

  public static ProgramDetailDto of(final Workpack workpack) {
    return (ProgramDetailDto) WorkpackDetailDto.of(workpack, ProgramDetailDto::new);
  }

}
