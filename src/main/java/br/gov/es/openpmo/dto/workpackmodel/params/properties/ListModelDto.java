package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;

public class ListModelDto extends PropertyModelDto {

  public static ListModelDto of(final PropertyModel propertyModel) {
    return (ListModelDto) PropertyModelDto.of(propertyModel, ListModelDto::new);
  }

}
