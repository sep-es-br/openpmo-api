package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;

public class StakeholderParamDto {

  @NotNull(message = ID_WORKPACK_NOT_NULL)
  private Long idWorkpack;
  private PersonStakeholderParamDto person;
  private List<RoleDto> roles;
  private List<PermissionDto> permissions;
  private Long idPlan;

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public PersonStakeholderParamDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonStakeholderParamDto person) {
    this.person = person;
  }

  public List<RoleDto> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<RoleDto> roles) {
    this.roles = roles;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  @JsonIgnore
  public PermissionLevelEnum getGratherPermissionLevel() {
    if (this.hasEdit()) return PermissionLevelEnum.EDIT;
    return PermissionLevelEnum.READ;
  }

  @JsonIgnore
  private boolean hasEdit() {
    return this.permissions.stream().map(PermissionDto::getLevel).anyMatch(level -> level.equals(PermissionLevelEnum.EDIT));
  }

}
