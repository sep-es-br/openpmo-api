package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.budget.BudgetPlan;
import br.gov.es.openpmo.model.properties.BudgetPlanSelection;
import br.gov.es.openpmo.model.properties.Property;
import java.util.Set;

public class BudgetPlanSelectionDto extends PropertyDto {

  private Set<BudgetPlan> value;

  public static BudgetPlanSelectionDto of(Property property) {
    BudgetPlanSelectionDto instance = new BudgetPlanSelectionDto();
    instance.setId(property.getId());
    instance.setIdPropertyModel(property.getPropertyModelId());
    instance.setValue(((BudgetPlanSelection) property).getValue());
    return instance;
  }

  public Set<BudgetPlan> getValue() {
    return value;
  }

  public void setValue(Set<BudgetPlan> value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "BudgetPlanSelection";
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }
}
