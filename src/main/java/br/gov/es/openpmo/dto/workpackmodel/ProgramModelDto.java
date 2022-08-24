package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class ProgramModelDto extends WorkpackModelDto {

  public static final String PROGRAM_MODEL = "ProgramModel";

  public static ProgramModelDto of(final WorkpackModel workpackModel) {
    final ProgramModelDto programModelDto = new ProgramModelDto();
    programModelDto.setId(workpackModel.getId());
    programModelDto.setModelNameInPlural(workpackModel.getModelNameInPlural());
    programModelDto.setModelName(workpackModel.getModelName());
    programModelDto.setType(PROGRAM_MODEL);
    programModelDto.setFontIcon(workpackModel.getFontIcon());
    programModelDto.setSortBy(PropertyModelDto.of(workpackModel.getSortBy()));
    return programModelDto;
  }
}
