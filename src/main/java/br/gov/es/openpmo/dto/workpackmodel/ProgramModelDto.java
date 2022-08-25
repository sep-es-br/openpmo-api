package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class ProgramModelDto extends WorkpackModelDto {

  private static final String PROGRAM_MODEL = "ProgramModel";

  public static ProgramModelDto of(final WorkpackModel workpackModel) {
    return (ProgramModelDto) WorkpackModelDto.of(
      workpackModel,
      ProgramModelDto::new
    );
  }

  @Override
  public String getType() {
    return PROGRAM_MODEL;
  }

}
