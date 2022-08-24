package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class DeliverableModelDetailDto extends WorkpackModelDetailDto {


  public static DeliverableModelDetailDto of(final WorkpackModel workpackModel) {
    return (DeliverableModelDetailDto) WorkpackModelDetailDto.of(workpackModel, DeliverableModelDetailDto::new);
  }

  @Override
  public String getType() {
    return "DeliverableModel";
  }

}
