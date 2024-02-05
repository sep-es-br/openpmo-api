package br.gov.es.openpmo.dto.menu;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackMenuResultDto {
    private Long id;
    private Long idWorkpackModel;
    private Long idPlan;
    private Long idParent;
    private String name;
    private String fullName;
    private String fontIcon;
    private Set<WorkpackMenuResultDto> children = new LinkedHashSet<>(0);

    public WorkpackMenuResultDto() {
    }

    public WorkpackMenuResultDto(WorkpackResultDto w) {
        this.id = w.getId();
        this.idWorkpackModel = w.getIdWorkpackModel();
        this.idPlan = w.getIdPlan();
        this.idParent = w.getIdParent();
        this.name = w.getName();
        this.fullName = w.getFullName();
        this.fontIcon = w.getFontIcon();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdWorkpackModel() {
        return idWorkpackModel;
    }

    public void setIdWorkpackModel(Long idWorkpackModel) {
        this.idWorkpackModel = idWorkpackModel;
    }

    public Long getIdParent() {
        return idParent;
    }

    public void setIdParent(Long idParent) {
        this.idParent = idParent;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public Set<WorkpackMenuResultDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackMenuResultDto> children) {
        this.children = children;
    }

}
