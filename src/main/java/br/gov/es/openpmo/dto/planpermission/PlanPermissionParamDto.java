package br.gov.es.openpmo.dto.planpermission;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class PlanPermissionParamDto {

  @NotNull
  private Long idPlan;
  @NotNull
  private String email;
  private PersonDto person;
  private List<PermissionDto> permissions;

  public PersonDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonDto person) {
    this.person = person;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public List<PermissionDto> getPermissions() {
    return Collections.unmodifiableList(this.permissions);
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

}
