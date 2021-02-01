package br.gov.es.openpmo.dto.office;

import javax.validation.constraints.NotBlank;

public class OfficeStoreDto {

	@NotBlank(message = "name.not.blank")
	private String name;
	@NotBlank(message = "name.not.blank")
	private String fullName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
