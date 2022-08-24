package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.properties.OrganizationSelection;
import br.gov.es.openpmo.model.properties.Property;

import java.util.Set;
import java.util.stream.Collectors;

public class OrganizationSelectionDto extends PropertyDto {

    private Set<Long> selectedValues;

    public Set<Long> getSelectedValues() {
        return this.selectedValues;
    }

    public void setSelectedValues(final Set<Long> selectedValues) {
        this.selectedValues = selectedValues;
    }

    public static PropertyDto of(final Property property) {
        final OrganizationSelectionDto organizationSelectionDto = new OrganizationSelectionDto();
        organizationSelectionDto.setId(property.getId());
        organizationSelectionDto.setIdPropertyModel(property.getPropertyModelId());
        organizationSelectionDto.setSelectedValues(((OrganizationSelection) property)
                .getValue()
                .stream()
                .map(Organization::getId)
                .collect(Collectors.toSet()));
        return organizationSelectionDto;
    }
}
