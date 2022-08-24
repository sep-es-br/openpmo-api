package br.gov.es.openpmo.dto.office;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.office.Office;

import java.util.ArrayList;
import java.util.List;

public class OfficeDto {
  private Long id;
  private String name;
  private String fullName;
  private List<PermissionDto> permissions;

  public OfficeDto() {

  }

  public OfficeDto(final Office office) {
    this.id = office.getId();
    this.name = office.getName();
    this.fullName = office.getFullName();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public static OfficeDto of(final Office office) {
    final OfficeDto officeDto = new OfficeDto();
    officeDto.setId(office.getId());
    officeDto.setName(office.getName());
    officeDto.setPermissions(new ArrayList<>());
    officeDto.setFullName(office.getFullName());
    return officeDto;
  }
}
