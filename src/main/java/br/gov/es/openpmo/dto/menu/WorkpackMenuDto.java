package br.gov.es.openpmo.dto.menu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.es.openpmo.dto.permission.PermissionDto;

public class WorkpackMenuDto {
    private Long id;
    private Long idPlan;
    private String name;
    private String fontIcon;
    private List<PermissionDto> permissions;
    private Set<WorkpackMenuDto> children = new HashSet<>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public Set<WorkpackMenuDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackMenuDto> children) {
        this.children = children;
    }

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}
