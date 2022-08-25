package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class MilestoneModelDto extends WorkpackModelDto {

  private static final String MILESTONE_MODEL = "MilestoneModel";


  public static MilestoneModelDto of(final WorkpackModel workpackModel) {
    return (MilestoneModelDto) WorkpackModelDto.of(
      workpackModel,
      MilestoneModelDto::new
    );
  }

  @Override
  public String getType() {
    return MILESTONE_MODEL;
  }

}
