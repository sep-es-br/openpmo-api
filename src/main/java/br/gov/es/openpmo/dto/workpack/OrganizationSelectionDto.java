package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.properties.OrganizationSelection;
import br.gov.es.openpmo.model.properties.Property;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrganizationSelectionDto extends PropertyDto {

  private Set<Long> selectedValues;

  public static OrganizationSelectionDto of(final Property property) {
    final OrganizationSelectionDto organizationSelectionDto = new OrganizationSelectionDto();
    organizationSelectionDto.setId(property.getId());
    organizationSelectionDto.setIdPropertyModel(property.getPropertyModelId());
    organizationSelectionDto.setSelectedValues(getValue((OrganizationSelection) property));
    return organizationSelectionDto;
  }

  private static Set<Long> getValue(final OrganizationSelection property) {
    return Optional.ofNullable(property)
      .map(OrganizationSelection::getValue)
      .map(values -> values.stream()
        .map(Organization::getId)
        .collect(Collectors.toSet())
      ).orElse(null);
  }

  @Override
  public String getType() {
    return "OrganizationSelection";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  public Set<Long> getSelectedValues() {
    return this.selectedValues;
  }

  public void setSelectedValues(final Set<Long> selectedValues) {
    this.selectedValues = selectedValues;
  }

}
