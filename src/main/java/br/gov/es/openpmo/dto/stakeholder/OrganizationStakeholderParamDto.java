package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotNull;
import java.util.List;

public class OrganizationStakeholderParamDto {

  private Long id;
  @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
  private Long idWorkpack;
  private Long idOrganization;
  private List<RoleDto> roles;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdOrganization() {
    return this.idOrganization;
  }

  public void setIdOrganization(final Long idOrganization) {
    this.idOrganization = idOrganization;
  }

  public List<RoleDto> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<RoleDto> roles) {
    this.roles = roles;
  }

}
