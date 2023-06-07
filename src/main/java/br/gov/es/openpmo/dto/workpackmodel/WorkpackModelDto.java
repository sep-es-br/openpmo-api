package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;

import java.util.Optional;
import java.util.function.Supplier;

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

  @JsonIgnore
  @JsonProperty("type")
  private String type;

  private String modelName;

  private String modelNameInPlural;

  private String fontIcon;

  private PropertyModelDto sortBy;

  private Long position;


  public static <TYPE extends WorkpackModelDto> WorkpackModelDto of(
    final WorkpackModel workpackModel,
    final Supplier<TYPE> instanceSupplier
  ) {
    final WorkpackModelDto instance = instanceSupplier.get();
    instance.setId(workpackModel.getId());
    instance.setModelNameInPlural(workpackModel.getModelNameInPlural());
    instance.setModelName(workpackModel.getModelName());
    instance.setFontIcon(workpackModel.getFontIcon());
    instance.setPosition(workpackModel.getPosition());
    return instance;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @JsonIgnore
  @JsonProperty("type")
  public abstract String getType();

  @JsonProperty("type")
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

  public Long getPosition() {
    return this.position;
  }

  public void setPosition(final Long position) {
    this.position = position;
  }

  @JsonIgnore
  public Long getPositionOrElseZero() {
    return Optional.ofNullable(this.position)
      .orElse(0L);
  }

}
