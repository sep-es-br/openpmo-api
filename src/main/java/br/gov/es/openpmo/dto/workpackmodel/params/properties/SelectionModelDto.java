package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;

import javax.validation.constraints.NotBlank;

public class SelectionModelDto extends PropertyModelDto {

  private String defaultValue;
  @NotBlank
  private String possibleValues;
  private boolean multipleSelection;

  public static SelectionModelDto of(final PropertyModel propertyModel) {
    final SelectionModelDto instance = (SelectionModelDto) PropertyModelDto.of(propertyModel, SelectionModelDto::new);
    instance.setDefaultValue(((SelectionModel) propertyModel).getDefaultValue());
    instance.setPossibleValues(((SelectionModel) propertyModel).getPossibleValues());
    instance.setMultipleSelection(((SelectionModel) propertyModel).isMultipleSelection());
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
