package br.gov.es.openpmo.dto.officepermission;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class OfficePermissionParamDto {

  @NotNull
  private Long idOffice;
  @NotNull
  private String key;
  private PersonDto person;
  private List<PermissionDto> permissions;

  public PersonDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonDto person) {
    this.person = person;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }
}
