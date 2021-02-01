package br.gov.es.openpmo.dto.permission;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

public class PermissionDto {
    private Long id;
    private String role;
    private PermissionLevelEnum level;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public PermissionLevelEnum getLevel() {
        return this.level;
    }

    public void setLevel(PermissionLevelEnum level) {
        this.level = level;
    }
}
