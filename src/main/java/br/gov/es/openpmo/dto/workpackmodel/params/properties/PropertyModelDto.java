package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.function.Supplier;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = IntegerModelDto.class, name = "IntegerModel"),
  @JsonSubTypes.Type(value = TextModelDto.class, name = "TextModel"),
  @JsonSubTypes.Type(value = DateModelDto.class, name = "DateModel"),
  @JsonSubTypes.Type(value = ToggleModelDto.class, name = "ToggleModel"),
  @JsonSubTypes.Type(value = UnitSelectionModelDto.class, name = "UnitSelectionModel"),
  @JsonSubTypes.Type(value = SelectionModelDto.class, name = "SelectionModel"),
  @JsonSubTypes.Type(value = TextAreaModelDto.class, name = "TextAreaModel"),
  @JsonSubTypes.Type(value = NumberModelDto.class, name = "NumberModel"),
  @JsonSubTypes.Type(value = CurrencyModelDto.class, name = "CurrencyModel"),
  @JsonSubTypes.Type(value = LocalitySelectionModelDto.class, name = "LocalitySelectionModel"),
  @JsonSubTypes.Type(value = GroupModelDto.class, name = "GroupModel"),
  @JsonSubTypes.Type(value = OrganizationSelectionModelDto.class, name = "OrganizationSelectionModel")})
@ApiModel(subTypes = {IntegerModelDto.class, TextModelDto.class, DateModelDto.class, ToggleModelDto.class,
  UnitSelectionModelDto.class, SelectionModelDto.class, TextAreaModelDto.class, NumberModelDto.class, CurrencyModelDto.class,
  LocalitySelectionModelDto.class, GroupModelDto.class,
  OrganizationSelectionModelDto.class}, discriminator = "type", description = "Supertype of all PropertyModel.")
public class PropertyModelDto {


  private Long id;

  @NotNull
  private Long sortIndex;
  @NotBlank
  private String name;
  @NotBlank
  private String label;
  private String helpText;

  private boolean active;
  private boolean fullLine;
  private boolean required;

  protected static <TYPE extends PropertyModelDto> PropertyModelDto of(
    final PropertyModel propertyModel,
    final Supplier<TYPE> instanceSupplier
  ) {
    final PropertyModelDto instance = instanceSupplier.get();
    instance.setId(propertyModel.getId());
    instance.setActive(propertyModel.isActive());
    instance.setName(propertyModel.getName());
    instance.setLabel(propertyModel.getLabel());
    instance.setSortIndex(propertyModel.getSortIndex());
    instance.setFullLine(propertyModel.isFullLine());
    instance.setRequired(propertyModel.isRequired());
    instance.setHelpText(propertyModel.getHelpText());
    return instance;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public Long getSortIndex() {
    return this.sortIndex;
  }

  public void setSortIndex(final Long sortIndex) {
    this.sortIndex = sortIndex;
  }

  public boolean isFullLine() {
    return this.fullLine;
  }

  public void setFullLine(final boolean fullLine) {
    this.fullLine = fullLine;
  }

  public boolean isRequired() {
    return this.required;
  }

  public void setRequired(final boolean required) {
    this.required = required;
  }

  public String getHelpText() {
    return helpText;
  }

  public void setHelpText(String helpText) {
    this.helpText = helpText;
  }
}
