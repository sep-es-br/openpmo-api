package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TransversalViewSelectionModel;

public class TransversalViewSelectionModelDto extends PropertyModelDto {

  private String defaultValue;

  private String possibleValues;

  private boolean multipleSelection;

  public static TransversalViewSelectionModelDto of(final PropertyModel propertyModel) {
    final TransversalViewSelectionModelDto instance = (TransversalViewSelectionModelDto) PropertyModelDto.of(
      propertyModel,
      TransversalViewSelectionModelDto::new
    );
    final TransversalViewSelectionModel transversalProperty = (TransversalViewSelectionModel) propertyModel;
    instance.setDefaultValue(transversalProperty.getDefaultValue());
    instance.setPossibleValues(transversalProperty.getPossibleValues());
    instance.setMultipleSelection(transversalProperty.isMultipleSelection());
    return instance;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getPossibleValues() {
    return this.possibleValues;
  }

  public void setPossibleValues(final String possibleValues) {
    this.possibleValues = possibleValues;
  }

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }
}
