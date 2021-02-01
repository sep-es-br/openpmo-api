package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioModelDetailDto.class, name = "PortfolioModel"),
        @JsonSubTypes.Type(value = ProgramModelDetailDto.class, name = "ProgramModel"),
        @JsonSubTypes.Type(value = OrganizerModelDetailDto.class, name = "OrganizerModel"),
        @JsonSubTypes.Type(value = DeliverableModelDetailDto.class, name = "DeliverableModel"),
        @JsonSubTypes.Type(value = ProjectModelDetailDto.class, name = "ProjectModel"),
        @JsonSubTypes.Type(value = MilestoneModelDetailDto.class, name = "MilestoneModel") })
@ApiModel(subTypes = { PortfolioModelDetailDto.class, ProgramModelDetailDto.class, OrganizerModelDetailDto.class,
        DeliverableModelDetailDto.class, ProjectModelDetailDto.class,
        MilestoneModelDto.class }, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelDetailDto {

    private Long id;
    private String fontIcon;
    private String modelName;
    private String modelNameInPlural;
    private boolean costSessionActive;
    private boolean scheduleSessionActive;
    private boolean stakeholderSessionActive;
    private boolean childWorkpackModelSessionActive;
    private List<String> personRoles;
    private List<String> organizationRoles;
    private List<? extends PropertyModelDto> properties;
    private PlanModelDto planModel;
    private Set<WorkpackModelDetailDto> children;
    private WorkpackModelDto parent;
    private PropertyModelDto sortBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelNameInPlural() {
        return modelNameInPlural;
    }

    public void setModelNameInPlural(String modelNameInPlural) {
        this.modelNameInPlural = modelNameInPlural;
    }

    public boolean isCostSessionActive() {
        return costSessionActive;
    }

    public void setCostSessionActive(boolean costSessionActive) {
        this.costSessionActive = costSessionActive;
    }

    public boolean isScheduleSessionActive() {
        return this.scheduleSessionActive;
    }

    public void setScheduleSessionActive(boolean scheduleSessionActive) {
        this.scheduleSessionActive = scheduleSessionActive;
    }

    public boolean isStakeholderSessionActive() {
        return stakeholderSessionActive;
    }

    public void setStakeholderSessionActive(boolean stakeholderSessionActive) {
        this.stakeholderSessionActive = stakeholderSessionActive;
    }

    public boolean isChildWorkpackModelSessionActive() {
        return childWorkpackModelSessionActive;
    }

    public void setChildWorkpackModelSessionActive(boolean childWorkpackModelSessionActive) {
        this.childWorkpackModelSessionActive = childWorkpackModelSessionActive;
    }

    public List<String> getPersonRoles() {
        return personRoles;
    }

    public void setPersonRoles(List<String> personRoles) {
        this.personRoles = personRoles;
    }

    public List<String> getOrganizationRoles() {
        return organizationRoles;
    }

    public void setOrganizationRoles(List<String> organizationRoles) {
        this.organizationRoles = organizationRoles;
    }

    public List<? extends PropertyModelDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyModelDto> properties) {
        this.properties = properties;
    }

    public PlanModelDto getPlanModel() {
        return planModel;
    }

    public void setPlanModel(PlanModelDto planModel) {
        this.planModel = planModel;
    }

    public Set<WorkpackModelDetailDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackModelDetailDto> children) {
        this.children = children;
    }

    public WorkpackModelDto getParent() {
        return parent;
    }

    public void setParent(WorkpackModelDto parent) {
        this.parent = parent;
    }

    public PropertyModelDto getSortBy() {
        return sortBy;
    }

    public void setSortBy(PropertyModelDto sortBy) {
        this.sortBy = sortBy;
    }
}
