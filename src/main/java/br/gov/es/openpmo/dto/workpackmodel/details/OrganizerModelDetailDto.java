package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class OrganizerModelDetailDto extends WorkpackModelDetailDto {

  public static OrganizerModelDetailDto of(final WorkpackModel workpackModel) {
    return (OrganizerModelDetailDto) WorkpackModelDetailDto.of(workpackModel, OrganizerModelDetailDto::new);
  }

  @Override
  public String getType() {
    return "OrganizerModel";
  }

}
