package br.gov.es.openpmo.dto.workpack;


import br.gov.es.openpmo.model.properties.Group;
import br.gov.es.openpmo.model.properties.Property;

import java.util.List;
import java.util.stream.Collectors;

public class GroupDto extends PropertyDto {

    private List<? extends PropertyDto> groupedProperties;

    public List<? extends PropertyDto> getGroupedProperties() {
        return this.groupedProperties;
    }

    public void setGroupedProperties(final List<? extends PropertyDto> groupedProperties) {
        this.groupedProperties = groupedProperties;
    }

    public static PropertyDto of(final Property property) {
        final GroupDto groupDto = new GroupDto();
        groupDto.setId(property.getId());
        groupDto.setIdPropertyModel(property.getPropertyModelId());
        groupDto.setGroupedProperties(((Group) property)
                .getGroupedProperties()
                .stream()
                .map(PropertyDto::of)
                .collect(Collectors.toList()));
        return groupDto;
    }
}
