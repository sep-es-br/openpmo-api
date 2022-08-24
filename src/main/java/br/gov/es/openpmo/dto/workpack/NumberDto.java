package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.Property;

public class NumberDto extends PropertyDto {

    private Double value;

    public Double getValue() {
        return this.value;
    }

    public void setValue(final Double value) {
        this.value = value;
    }

    public static PropertyDto of(final Property property) {
        final NumberDto numberDto = new NumberDto();
        numberDto.setId(property.getId());
        numberDto.setIdPropertyModel(property.getPropertyModelId());
        numberDto.setValue(((Number) property).getValue());
        return numberDto;
    }
}
