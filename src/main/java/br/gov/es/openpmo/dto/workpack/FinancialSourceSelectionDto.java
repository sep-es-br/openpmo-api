package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.budget.FinancialSource;
import br.gov.es.openpmo.model.properties.FinancialSourceSelection;
import br.gov.es.openpmo.model.properties.Property;
import java.util.Set;

public class FinancialSourceSelectionDto extends PropertyDto {

  private Set<FinancialSource> value;

  public static FinancialSourceSelectionDto of(Property property) {
    FinancialSourceSelectionDto instance = new FinancialSourceSelectionDto();
    instance.setId(property.getId());
    instance.setIdPropertyModel(property.getPropertyModelId());
    instance.setValue(((FinancialSourceSelection) property).getValue());
    return instance;
  }

  public Set<FinancialSource> getValue() {
    return value;
  }

  public void setValue(Set<FinancialSource> value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "FinancialSourceSelection";
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }
}
