package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Text;

public class TextDto extends PropertyDto {

    private String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public static PropertyDto of(final Property property) {
        final TextDto textDto = new TextDto();
        textDto.setId(property.getId());
        textDto.setIdPropertyModel(property.getPropertyModelId());
        textDto.setValue(((Text) property).getValue());
        return textDto;
    }
}
