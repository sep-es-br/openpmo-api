package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.UnitSelectionModel;

import java.util.Optional;

public class UnitSelectionModelDto extends PropertyModelDto {

  private Long defaults;


  public static UnitSelectionModelDto of(final PropertyModel propertyModel) {
    final UnitSelectionModelDto instance = (UnitSelectionModelDto) PropertyModelDto.of(propertyModel, UnitSelectionModelDto::new);
    Optional.of(propertyModel)
      .map(UnitSelectionModel.class::cast)
      .map(UnitSelectionModel::getDefaultValue)
      .map(UnitMeasure::getId)
      .ifPresent(instance::setDefaults);
    return instance;
  }

  public Long getDefaults() {
    return this.defaults;
  }

  public void setDefaults(final Long defaults) {
    this.defaults = defaults;
  }

}
