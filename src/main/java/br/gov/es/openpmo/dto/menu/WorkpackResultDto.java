package br.gov.es.openpmo.dto.menu;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackResultDto {
    private Long id;
    private Long idWorkpackModel;
    private Long idPlan;
    private Long idParent;
    private String name;
    private String fullName;
    private String fontIcon;
    private Set<WorkpackResultDto> children = new LinkedHashSet<>(0);
    private String modelName;
    private List<String> labels;
    private String type;
    private String modelNameInPlural;
    private Long position;

    public WorkpackResultDto() {
    }

    public WorkpackResultDto(Long idWorkpack, Long idWorkpackModel, Long idPlan, Long idParent, String name, String fullName,
                             String fontIcon, String modelName, String modelNameInPlural, List<String> labels, Long position) {
        this.id = idWorkpack;
        this.idWorkpackModel = idWorkpackModel;
        this.idPlan = idPlan;
        this.idParent = idParent;
        this.name = name;
        this.fullName = fullName;
        this.fontIcon = fontIcon;
        this.modelName = modelName;
        this.modelNameInPlural = modelNameInPlural;
        this.labels = labels;
        this.position = position;
    }

    public WorkpackResultDto(WorkpackResultDto w) {
        this.id = w.getId();
        this.idWorkpackModel = w.getIdWorkpackModel();
        this.idPlan = w.getIdPlan();
        this.idParent = w.getIdParent();
        this.name = w.getName();
        this.fullName = w.getFullName();
        this.fontIcon = w.getFontIcon();
        this.modelName = w.getModelName();
        this.modelNameInPlural = w.getModelNameInPlural();
        this.position = w.getPosition();
        if (w.getLabels() != null && !w.getLabels().isEmpty()) {
            w.getLabels().forEach(this::setType);
        }
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

    public Set<WorkpackResultDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackResultDto> children) {
        this.children = children;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelNameInPlural() {
        return modelNameInPlural;
    }

    public void setModelNameInPlural(String modelNameInPlural) {
        this.modelNameInPlural = modelNameInPlural;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Map<Long, Set<WorkpackResultDto>> getChildrenGroupedByModel() {
        Map<Long, Set<WorkpackResultDto>> mapModel = new HashMap<>(0);
        if (CollectionUtils.isNotEmpty(children)) {
            for (WorkpackResultDto child : children) {
                mapModel.computeIfAbsent(child.getIdWorkpackModel(), k -> new LinkedHashSet<>());
                mapModel.get(child.getIdWorkpackModel()).add(child);
            }
        }
        return mapModel;
    }
}
