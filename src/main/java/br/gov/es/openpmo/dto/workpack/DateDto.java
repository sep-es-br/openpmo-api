package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;

import java.time.LocalDateTime;

public class DateDto extends PropertyDto {

  private LocalDateTime value;

  private String reason;

  public static DateDto of(final Property property) {
    final DateDto dateDto = new DateDto();
    dateDto.setId(property.getId());
    dateDto.setIdPropertyModel(property.getPropertyModelId());
    dateDto.setValue(((Date) property).getValue());
    return dateDto;
  }

  public LocalDateTime getValue() {
    return this.value;
  }

  public void setValue(final LocalDateTime value) {
    this.value = value;
  }

  @Override
  public String getType() {
    return "Date";
  }

  @Override
  public void setType(final String type) {
    this.type = type;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

}
