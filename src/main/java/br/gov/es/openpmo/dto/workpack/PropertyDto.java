package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = IntegerDto.class, name = "Integer"),
  @Type(value = TextDto.class, name = "Text"),
  @Type(value = DateDto.class, name = "Date"),
  @Type(value = ToggleDto.class, name = "Toggle"),
  @Type(value = UnitSelectionDto.class, name = "UnitSelection"),
  @Type(value = SelectionDto.class, name = "Selection"),
  @Type(value = TextAreaDto.class, name = "TextArea"),
  @Type(value = NumberDto.class, name = "Num"),
  @Type(value = CurrencyDto.class, name = "Currency"),
  @Type(value = LocalitySelectionDto.class, name = "LocalitySelection"),
  @Type(value = GroupDto.class, name = "Group"),
  @Type(value = OrganizationSelectionDto.class, name = "OrganizationSelection")
})
@ApiModel(subTypes = {
  IntegerDto.class,
  TextDto.class,
  DateDto.class,
  ToggleDto.class,
  UnitSelectionDto.class,
  SelectionDto.class,
  TextAreaDto.class,
  NumberDto.class,
  CurrencyDto.class,
  LocalitySelectionDto.class,
  GroupDto.class,
  OrganizationSelectionDto.class
}, discriminator = "type", description = "Supertype of all Property.")
public abstract class PropertyDto {

  private Long id;

  private Long idPropertyModel;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPropertyModel() {
    return this.idPropertyModel;
  }

  public void setIdPropertyModel(final Long idPropertyModel) {
    this.idPropertyModel = idPropertyModel;
  }
}
