package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

import java.util.List;

public class WorkpackPermissionDetailDto {

    private Long id;
    private String name;
    private List<String> roles;
    private String icon;
    private PermissionLevelEnum accessLevel;
    private Boolean isCcbMember;

    public WorkpackPermissionDetailDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public PermissionLevelEnum getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(PermissionLevelEnum accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Boolean getCcbMember() {
        return isCcbMember;
    }

    public void setCcbMember(Boolean ccbMember) {
        isCcbMember = ccbMember;
    }
}
