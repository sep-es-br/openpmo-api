package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.TextArea;

public class TextAreaDto extends PropertyDto {

    private String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public static TextAreaDto of(final Property property) {
        final TextAreaDto textAreaDto = new TextAreaDto();
        textAreaDto.setId(property.getId());
        textAreaDto.setIdPropertyModel(property.getPropertyModelId());
        textAreaDto.setValue(((TextArea) property).getValue());
        return textAreaDto;
    }
}
