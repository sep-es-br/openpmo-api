package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class OrganizerModelDto extends WorkpackModelDto {

  private static final String ORGANIZER_MODEL = "OrganizerModel";

  public static OrganizerModelDto of(final WorkpackModel workpackModel) {
    return (OrganizerModelDto) WorkpackModelDto.of(
      workpackModel,
      OrganizerModelDto::new
    );
  }

  @Override
  public String getType() {
    return ORGANIZER_MODEL;
  }

}
