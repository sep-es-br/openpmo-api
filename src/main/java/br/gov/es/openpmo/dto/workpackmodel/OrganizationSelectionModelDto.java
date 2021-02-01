package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;

public class OrganizationSelectionModelDto extends PropertyModelDto {
	
	private boolean multipleSelection;

	private List<Long> defaults;

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public List<Long> getDefaults() {
		return defaults;
	}

	public void setDefaults(List<Long> defaults) {
		this.defaults = defaults;
	}
}
