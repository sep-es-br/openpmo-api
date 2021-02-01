package br.gov.es.openpmo.dto.unitmeasure;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class UnitMeasureUpdateDto {
	@NotNull(message = ApplicationMessage.ID_NOT_NULL)
	private Long id;
	@NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
	private String name;
	@NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
	private String fullName;
	@NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
	private Long idOffice;

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

	public Long getIdOffice() {
		return this.idOffice;
	}

	public void setIdOffice(Long idOffice) {
		this.idOffice = idOffice;
	}

}
