package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class OrganizerModelDto extends WorkpackModelDto {

  public static final String ORGANIZER_MODEL = "OrganizerModel";

  public static OrganizerModelDto of(final WorkpackModel workpackModel) {
    final OrganizerModelDto organizerModelDto = new OrganizerModelDto();
    organizerModelDto.setId(workpackModel.getId());
    organizerModelDto.setModelNameInPlural(workpackModel.getModelNameInPlural());
    organizerModelDto.setModelName(workpackModel.getModelName());
    organizerModelDto.setType(ORGANIZER_MODEL);
    organizerModelDto.setFontIcon(workpackModel.getFontIcon());
    organizerModelDto.setSortBy(PropertyModelDto.of(workpackModel.getSortBy()));
    return organizerModelDto;
  }
}
