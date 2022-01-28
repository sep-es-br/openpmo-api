package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = PortfolioModelDto.class, name = "PortfolioModel"),
    @JsonSubTypes.Type(value = ProgramModelDto.class, name = "ProgramModel"),
    @JsonSubTypes.Type(value = OrganizerModelDto.class, name = "OrganizerModel"),
    @JsonSubTypes.Type(value = DeliverableModelDto.class, name = "DeliverableModel"),
    @JsonSubTypes.Type(value = ProjectModelDto.class, name = "ProjectModel"),
    @JsonSubTypes.Type(value = MilestoneModelDto.class, name = "MilestoneModel")})
@ApiModel(subTypes = {PortfolioModelDto.class, ProgramModelDto.class, OrganizerModelDto.class,
    DeliverableModelDto.class, ProjectModelDto.class,
    MilestoneModelDto.class}, discriminator = "type", description = "Supertype of all WorkpackModel.")
public abstract class WorkpackModelDto {

  private Long id;

  private String type;

  private String modelName;

  private String modelNameInPlural;

  private String fontIcon;

  private PropertyModelDto sortBy;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(final String modelName) {
    this.modelName = modelName;
  }

  public String getModelNameInPlural() {
    return this.modelNameInPlural;
  }

  public void setModelNameInPlural(final String modelNameInPlural) {
    this.modelNameInPlural = modelNameInPlural;
  }

  public String getFontIcon() {
    return this.fontIcon;
  }

  public void setFontIcon(final String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public PropertyModelDto getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final PropertyModelDto sortBy) {
    this.sortBy = sortBy;
  }

}
