package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.dto.organization.OrganizationDto;

import java.util.ArrayList;
import java.util.List;

public class StakeholderOrganizationDto {

  private Long idWorkpack;
  private OrganizationDto organization;
  private List<RoleDto> roles = new ArrayList<>(0);

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public OrganizationDto getOrganization() {
    return this.organization;
  }

  public void setOrganization(final OrganizationDto organization) {
    this.organization = organization;
  }

  public List<RoleDto> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<RoleDto> roles) {
    this.roles = roles;
  }

}
