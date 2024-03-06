package br.gov.es.openpmo.dto.menu;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackMenuResultDto {
    private Long id;
    private Long idWorkpackModel;
    private Long idWorkpackModelLinked;
    private Long idPlan;
    private Long idParent;
    private String name;
    private String fullName;
    private String fontIcon;
    private Comparable sort;
    private Long position;
    private Set<WorkpackMenuResultDto> children = new LinkedHashSet<>(0);

    public WorkpackMenuResultDto() {
    }

    public WorkpackMenuResultDto(WorkpackResultDto w) {
        this.id = w.getId();
        this.idWorkpackModel = w.getIdWorkpackModel();
        this.idWorkpackModelLinked = w.getLinked().equals(true) ? w.getIdWorkpackModel() : null;
        this.idPlan = w.getIdPlan();
        this.idParent = w.getIdParent();
        this.name = w.getName();
        this.fullName = w.getFullName();
        this.fontIcon = w.getFontIcon();
        this.sort = w.getSort();
        this.position = w.getPosition();
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

    public Long getIdWorkpackModelLinked() {
        return idWorkpackModelLinked;
    }

    public void setIdWorkpackModelLinked(Long idWorkpackModelLinked) {
        this.idWorkpackModelLinked = idWorkpackModelLinked;
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

    public Comparable getSort() {
        return sort;
    }

    public void setSort(Comparable sort) {
        this.sort = sort;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }
}
