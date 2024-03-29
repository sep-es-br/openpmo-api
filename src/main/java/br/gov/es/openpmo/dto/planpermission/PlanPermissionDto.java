package br.gov.es.openpmo.dto.planpermission;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

import java.util.List;

public class PlanPermissionDto {

  private Long idPlan;
  private PersonDto person;
  private List<PermissionDto> permissions;

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public PersonDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonDto person) {
    this.person = person;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

}
