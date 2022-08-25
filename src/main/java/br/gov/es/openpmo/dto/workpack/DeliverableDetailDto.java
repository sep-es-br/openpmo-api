package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class DeliverableDetailDto extends WorkpackDetailDto {

  public static DeliverableDetailDto of(final Workpack workpack) {
    return (DeliverableDetailDto) WorkpackDetailDto.of(workpack, DeliverableDetailDto::new);
  }

}
