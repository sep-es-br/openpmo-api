package br.gov.es.openpmo.dto.workpackmodel;

public class TextModelDto extends PropertyModelDto {

	private Long min;
	private Long max;
	private String defaultValue;

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
		this.min = min;
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
