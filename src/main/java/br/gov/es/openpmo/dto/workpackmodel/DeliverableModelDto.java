package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class DeliverableModelDto extends WorkpackModelDto {

  private static final String DELIVERABLE_MODEL = "DeliverableModel";

  public static DeliverableModelDto of(final WorkpackModel workpackModel) {
    return (DeliverableModelDto) WorkpackModelDto.of(
      workpackModel,
      DeliverableModelDto::new
    );
  }


  @Override
  public String getType() {
    return DELIVERABLE_MODEL;
  }

}
