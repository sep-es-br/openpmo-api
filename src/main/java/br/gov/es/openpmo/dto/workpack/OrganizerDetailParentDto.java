package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class OrganizerDetailParentDto extends WorkpackDetailParentDto {

  public static OrganizerDetailParentDto of(final Workpack workpack) {
    return (OrganizerDetailParentDto) WorkpackDetailParentDto.of(workpack, OrganizerDetailParentDto::new);
  }

}
