package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class MilestoneModelDetailDto extends WorkpackModelDetailDto {


  public static MilestoneModelDetailDto of(final WorkpackModel workpackModel) {
    return (MilestoneModelDetailDto) WorkpackModelDetailDto.of(workpackModel, MilestoneModelDetailDto::new);
  }


  @Override
  public String getType() {
    return "MilestoneModel";
  }

}
