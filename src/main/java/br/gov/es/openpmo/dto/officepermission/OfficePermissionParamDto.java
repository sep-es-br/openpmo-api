package br.gov.es.openpmo.dto.officepermission;

import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;

public class OfficePermissionParamDto {

    private Long idOffice;
    private String email;
    private List<PermissionDto> permissions;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdOffice() {
        return this.idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }

    public List<PermissionDto> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

}
