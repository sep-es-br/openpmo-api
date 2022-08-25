package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.properties.LocalitySelection;
import br.gov.es.openpmo.model.properties.Property;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalitySelectionDto extends PropertyDto {

  private Set<Long> selectedValues;

  public static PropertyDto of(final Property property) {
    final LocalitySelectionDto localitySelectionDto = new LocalitySelectionDto();
    localitySelectionDto.setId(property.getId());
    localitySelectionDto.setIdPropertyModel(property.getPropertyModelId());
    localitySelectionDto.setSelectedValues(getValue((LocalitySelection) property));
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

  public Set<Long> getSelectedValues() {
    return this.selectedValues;
  }

  public void setSelectedValues(final Set<Long> selectedValues) {
    this.selectedValues = selectedValues;
  }

}
