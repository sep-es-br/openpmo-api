package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioModelParamDto.class, name = "PortfolioModel"),
                  @JsonSubTypes.Type(value = ProgramModelParamDto.class, name = "ProgramModel"),
                  @JsonSubTypes.Type(value = OrganizerModelParamDto.class, name = "OrganizerModel"),
                  @JsonSubTypes.Type(value = DeliverableModelParamDto.class, name = "DeliverableModel"),
                  @JsonSubTypes.Type(value = ProjectModelParamDto.class, name = "ProjectModel"),
                  @JsonSubTypes.Type(value = MilestoneModelParamDto.class, name = "MilestoneModel") })
@ApiModel(subTypes = { PortfolioModelParamDto.class, ProgramModelParamDto.class, OrganizerModelParamDto.class, DeliverableModelParamDto.class,
    ProjectModelParamDto.class,
    MilestoneModelParamDto.class }, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelParamDto {

    private Long id;

    @NotBlank(message = "fontIcon.not.blank")
    private String fontIcon;

    @NotBlank(message = "modelName.not.blank")
    private String modelName;

    private String modelNameInPlural;

    private boolean costSessionActive;
    private boolean stakeholderSessionActive;
    private boolean childWorkpackModelSessionActive;
    private boolean scheduleSessionActive;
    
    private List<String> personRoles;
    private List<String> organizationRoles;

    @Valid
    private List<? extends PropertyModelDto> properties;

    private Long idParent;

    private String sortBy;

    @NotNull
    private Long idPlanModel;

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

    public boolean isScheduleSessionActive() {
        return this.scheduleSessionActive;
    }

    public void setScheduleSessionActive(boolean scheduleSessionActive) {
        this.scheduleSessionActive = scheduleSessionActive;
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

    public Long getIdParent() {
        return idParent;
    }

    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    public Long getIdPlanModel() {
        return idPlanModel;
    }

    public void setIdPlanModel(Long idPlanModel) {
        this.idPlanModel = idPlanModel;
    }

    public List<? extends PropertyModelDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyModelDto> properties) {
        this.properties = properties;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
