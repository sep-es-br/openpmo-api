package br.gov.es.openpmo.dto.office;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class OfficeUpdateDto {

	@NotNull(message = ApplicationMessage.ID_NOT_NULL)
	private Long id;
	@NotBlank(message = "name.not.blank")
	private String name;
	@NotBlank(message = "name.not.blank")
	private String fullName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
