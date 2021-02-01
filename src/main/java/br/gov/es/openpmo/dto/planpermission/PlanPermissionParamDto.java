package br.gov.es.openpmo.dto.planpermission;

import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;

public class PlanPermissionParamDto {

    private Long idPlan;
    private String email;
    private List<PermissionDto> permissions;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdPlan() {
        return this.idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public List<PermissionDto> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

}
