package br.gov.es.openpmo.dto.menu;

import java.util.Set;

public class WorkpackModelMenuDto {
    private Long id;
    private String modelName;
    private String fontIcon;
    private Set<WorkpackModelMenuDto> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public Set<WorkpackModelMenuDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackModelMenuDto> children) {
        this.children = children;
    }
}
