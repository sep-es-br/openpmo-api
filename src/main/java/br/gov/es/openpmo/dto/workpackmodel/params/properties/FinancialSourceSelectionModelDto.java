package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.enumerator.SelectionLevel;
import br.gov.es.openpmo.model.budget.FinancialSource;
import br.gov.es.openpmo.model.properties.models.FinancialSourceSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.Set;
import javax.validation.constraints.NotNull;

public class FinancialSourceSelectionModelDto extends PropertyModelDto {

  private Set<FinancialSource> defaultValue;
  @NotNull
  private SelectionLevel selectionLevel;
  private boolean multipleSelection;

  public static FinancialSourceSelectionModelDto of(PropertyModel propertyModel) {
    FinancialSourceSelectionModelDto instance = (FinancialSourceSelectionModelDto) PropertyModelDto.of(
      propertyModel,
      FinancialSourceSelectionModelDto::new
    );
    FinancialSourceSelectionModel model = (FinancialSourceSelectionModel) propertyModel;
    instance.setDefaultValue(model.getDefaultValue());
    instance.setSelectionLevel(model.getSelectionLevel());
    instance.setMultipleSelection(model.isMultipleSelection());
    return instance;
  }

  public Set<FinancialSource> getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Set<FinancialSource> defaultValue) {
    this.defaultValue = defaultValue;
  }

  public SelectionLevel getSelectionLevel() {
    return selectionLevel;
  }

  public void setSelectionLevel(SelectionLevel selectionLevel) {
    this.selectionLevel = selectionLevel;
  }

  public boolean isMultipleSelection() {
    return multipleSelection;
  }

  public void setMultipleSelection(boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }
}
