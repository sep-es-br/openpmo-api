package br.gov.es.openpmo.dto.stakeholder;

import java.util.List;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class OrganizationStakeholderParamDto {

    private Long id;
    @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
    private Long idWorkpack;
    private Long idOrganization;
    private List<RoleDto> roles;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdOrganization() {
        return this.idOrganization;
    }

    public void setIdOrganization(Long idOrganization) {
        this.idOrganization = idOrganization;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }
}
