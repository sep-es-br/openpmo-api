package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class DeliverableDetailParentDto extends WorkpackDetailParentDto {

  public static DeliverableDetailParentDto of(final Workpack workpack) {
    return (DeliverableDetailParentDto) WorkpackDetailParentDto.of(workpack, DeliverableDetailParentDto::new);
  }

}
