package br.gov.es.openpmo.dto.office;

import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.Office;

public class OfficeDto {
	private Long id;
	private String name;
	private String fullName;
	private List<PermissionDto> permissions;

	public OfficeDto() {

	}

	public OfficeDto(Office office) {
		this.id = office.getId();
		this.name = office.getName();
		this.fullName = office.getFullName();
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

	public List<PermissionDto> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionDto> permissions) {
		this.permissions = permissions;
	}
}
