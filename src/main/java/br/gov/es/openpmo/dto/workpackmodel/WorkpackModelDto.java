package br.gov.es.openpmo.dto.workpackmodel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioModelDto.class, name = "PortfolioModel"),
        @JsonSubTypes.Type(value = ProgramModelDto.class, name = "ProgramModel"),
        @JsonSubTypes.Type(value = OrganizerModelDto.class, name = "OrganizerModel"),
        @JsonSubTypes.Type(value = DeliverableModelDto.class, name = "DeliverableModel"),
        @JsonSubTypes.Type(value = ProjectModelDto.class, name = "ProjectModel"),
        @JsonSubTypes.Type(value = MilestoneModelDto.class, name = "MilestoneModel") })
@ApiModel(subTypes = { PortfolioModelDto.class, ProgramModelDto.class, OrganizerModelDto.class,
        DeliverableModelDto.class, ProjectModelDto.class,
        MilestoneModelDto.class }, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelDto {

    private Long id;
    private String type;
    private String modelName;
    private String modelNameInPlural;
    private String fontIcon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelNameInPlural() {
        return this.modelNameInPlural;
    }

    public void setModelNameInPlural(String modelNameInPlural) {
        this.modelNameInPlural = modelNameInPlural;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }
}
