package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;
import javax.validation.constraints.NotNull;

public class LocalitySelectionModelDto extends PropertyModelDto {

	private boolean multipleSelection;
	@NotNull
	private Long idDomain;
	private List<Long> defaults;

	public boolean isMultipleSelection() {
		return multipleSelection;
	}

	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	public Long getIdDomain() {
		return this.idDomain;
	}

	public void setIdDomain(Long idDomain) {
		this.idDomain = idDomain;
	}

	public List<Long> getDefaults() {
		return defaults;
	}

	public void setDefaults(List<Long> defaults) {
		this.defaults = defaults;
	}
}
