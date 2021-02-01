package br.gov.es.openpmo.dto.unitmeasure;

import br.gov.es.openpmo.model.UnitMeasure;

public class UnitMeasureDto {
	private Long id;
	private String name;
	private String fullName;
	private Long idOffice;

	public UnitMeasureDto() {

	}

	public UnitMeasureDto(UnitMeasure unitMeasure) {
		this.id = unitMeasure.getId();
		this.name = unitMeasure.getName();
		this.fullName = unitMeasure.getFullName();
		this.idOffice = unitMeasure.getOffice().getId();
	}

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
