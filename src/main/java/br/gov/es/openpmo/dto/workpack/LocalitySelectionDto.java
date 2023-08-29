package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.properties.LocalitySelection;
import br.gov.es.openpmo.model.properties.Property;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalitySelectionDto extends PropertyDto {

  private Set<Long> selectedValues;
  private Set<SimpleResource> selectedValuesDetails;

  public static LocalitySelectionDto of(final Property property) {
    final LocalitySelectionDto localitySelectionDto = new LocalitySelectionDto();
    localitySelectionDto.setId(property.getId());
    localitySelectionDto.setIdPropertyModel(property.getPropertyModelId());
    localitySelectionDto.selectedValues = getValue((LocalitySelection) property);
    localitySelectionDto.selectedValuesDetails = getValueDetails((LocalitySelection) property);
    return localitySelectionDto;
  }

  private static Set<Long> getValue(final LocalitySelection property) {
    return Optional.ofNullable(property)
      .map(LocalitySelection::getValue)
      .map(values -> values.stream()
        .map(Locality::getId)
        .collect(Collectors.toSet())
      ).orElse(null);
  }

  private static Set<SimpleResource> getValueDetails(final LocalitySelection property) {
    return Optional.ofNullable(property)
      .map(LocalitySelection::getValue)
      .map(values -> values.stream()
        .map(locality -> SimpleResource.of(locality.getId(), locality.getName(), locality.getFullName()))
        .collect(Collectors.toSet())
      ).orElse(null);
  }


  @Override
  public String getType() {
    return "LocalitySelection";
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

  public Set<SimpleResource> getSelectedValuesDetails() {
    return this.selectedValuesDetails;
  }

  public void setSelectedValuesDetails(final Set<SimpleResource> selectedValuesDetails) {
    this.selectedValuesDetails = selectedValuesDetails;
  }
}
