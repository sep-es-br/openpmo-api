package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Toggle;

public class ToggleDto extends PropertyDto {

    private boolean value;

    public boolean isValue() {
        return this.value;
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    public static ToggleDto of(final Property property) {
        final ToggleDto toggleDto = new ToggleDto();
        toggleDto.setId(property.getId());
        toggleDto.setIdPropertyModel(property.getPropertyModelId());
        toggleDto.setValue(((Toggle) property).getValue());
        return toggleDto;
    }
}
