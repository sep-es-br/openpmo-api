package br.gov.es.openpmo.dto.workpack;

import java.time.LocalDateTime;

public class DateDto extends PropertyDto {

  private LocalDateTime value;

  public LocalDateTime getValue() {
    return this.value;
  }

  public void setValue(final LocalDateTime value) {
    this.value = value;
  }
}
