package br.gov.es.openpmo.dto.workpackmodel;

public class IntegerModelDto extends PropertyModelDto {

	private Long min;
	private Long max;
	private Long defaultValue;

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

	public Long getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Long defaultValue) {
		this.defaultValue = defaultValue;
	}
}
