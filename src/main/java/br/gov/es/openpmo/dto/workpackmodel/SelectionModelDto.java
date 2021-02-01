package br.gov.es.openpmo.dto.workpackmodel;

import javax.validation.constraints.NotBlank;

public class SelectionModelDto extends PropertyModelDto {

	@NotBlank
	private String defaultValue;
	@NotBlank
	private String possibleValues;
	private boolean multipleSelection;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(String possibleValues) {
		this.possibleValues = possibleValues;
	}

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

}
