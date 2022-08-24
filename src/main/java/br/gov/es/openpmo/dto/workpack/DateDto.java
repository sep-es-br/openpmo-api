package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;

import java.time.LocalDateTime;

public class DateDto extends PropertyDto {

    private LocalDateTime value;

    public LocalDateTime getValue() {
        return this.value;
    }

    public void setValue(final LocalDateTime value) {
        this.value = value;
    }

    public static PropertyDto of(final Property property) {
        final DateDto dateDto = new DateDto();
        dateDto.setId(property.getId());
        dateDto.setIdPropertyModel(property.getPropertyModelId());
        dateDto.setValue(((Date) property).getValue());
        return dateDto;
    }
}
