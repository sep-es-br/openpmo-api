package br.gov.es.openpmo.dto.menu;

import java.util.HashSet;
import java.util.Set;

public class WorkpackMenuDto {
    private Long id;
    private String name;
    private String fontIcon;
    private Set<WorkpackMenuDto> children = new HashSet<>(0);

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
}
