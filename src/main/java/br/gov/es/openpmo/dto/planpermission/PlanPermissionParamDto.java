package br.gov.es.openpmo.dto.planpermission;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlanPermissionParamDto {

  @NotNull
  private Long idPlan;
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

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public List<PermissionDto> getPermissions() {
    return Optional.ofNullable(this.permissions)
      .map(Collections::unmodifiableList)
      .orElse(null);
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

}
