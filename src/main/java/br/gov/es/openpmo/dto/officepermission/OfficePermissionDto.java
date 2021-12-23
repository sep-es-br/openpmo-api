package br.gov.es.openpmo.dto.officepermission;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.RoleResource;

import java.util.Collections;
import java.util.List;

public class OfficePermissionDto {

  private Long idOffice;
  private PersonDto person;
  private List<? extends PermissionDto> permissions;

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public PersonDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonDto person) {
    this.person = person;
  }

  public List<PermissionDto> getPermissions() {
    return Collections.unmodifiableList(this.permissions);
  }

  public void setPermissions(final List<? extends PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public void addAllRoles(final List<? extends RoleResource> roles) {
    this.person.addAllRoles(roles);
  }
}
