package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.budget.BudgetPlan;
import br.gov.es.openpmo.model.properties.models.BudgetPlanSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.Set;

public class BudgetPlanSelectionModelDto extends PropertyModelDto {

  private Set<BudgetPlan> defaultValue;
  private boolean multipleSelection;

  public static BudgetPlanSelectionModelDto of(PropertyModel propertyModel) {
    BudgetPlanSelectionModelDto instance = (BudgetPlanSelectionModelDto) PropertyModelDto.of(
      propertyModel,
      BudgetPlanSelectionModelDto::new
    );
    BudgetPlanSelectionModel model = (BudgetPlanSelectionModel) propertyModel;
    instance.setDefaultValue(model.getDefaultValue());
    instance.setMultipleSelection(model.isMultipleSelection());
    return instance;
  }

  public Set<BudgetPlan> getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Set<BudgetPlan> defaultValue) {
    this.defaultValue = defaultValue;
  }

  public boolean isMultipleSelection() {
    return multipleSelection;
  }

  public void setMultipleSelection(boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }
}
