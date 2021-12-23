package br.gov.es.openpmo.dto.workpack;

import java.util.Set;

public class OrganizationSelectionDto extends PropertyDto {

  private Set<Long> selectedValues;

  public Set<Long> getSelectedValues() {
    return this.selectedValues;
  }

  public void setSelectedValues(final Set<Long> selectedValues) {
    this.selectedValues = selectedValues;
  }
}
