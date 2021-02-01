package br.gov.es.openpmo.dto.stakeholder;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.openpmo.dto.organization.OrganizationDto;

public class StakeholderOrganizationDto {

    private Long idWorkpack;
    private OrganizationDto organization;
    private List<RoleDto> roles = new ArrayList<>(0);

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}
