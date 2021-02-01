package br.gov.es.openpmo.dto.workpack;

import java.time.LocalDateTime;

public class DateDto extends PropertyDto {

	private LocalDateTime value;

	public LocalDateTime getValue() {
		return value;
	}

	public void setValue(LocalDateTime value) {
		this.value = value;
	}
}
