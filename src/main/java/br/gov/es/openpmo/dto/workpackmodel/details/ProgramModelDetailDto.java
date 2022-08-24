package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class ProgramModelDetailDto extends WorkpackModelDetailDto {

  public static ProgramModelDetailDto of(final WorkpackModel workpackModel) {
    return (ProgramModelDetailDto) WorkpackModelDetailDto.of(workpackModel, ProgramModelDetailDto::new);
  }

  @Override
  public String getType() {
    return "ProgramModel";
  }

}
