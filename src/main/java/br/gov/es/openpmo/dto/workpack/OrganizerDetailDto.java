package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class OrganizerDetailDto extends WorkpackDetailDto {

  public static OrganizerDetailDto of(final Workpack workpack) {
    return (OrganizerDetailDto) WorkpackDetailDto.of(workpack, OrganizerDetailDto::new);
  }

}
