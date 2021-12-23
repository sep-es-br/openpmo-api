package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.model.Entity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import org.neo4j.ogm.annotation.NodeEntity;

import java.beans.Transient;
import java.util.Objects;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = IntegerModel.class, name = "IntegerModel"),
  @Type(value = TextModel.class, name = "TextModel"),
  @Type(value = DateModel.class, name = "DateModel"),
  @Type(value = ToggleModel.class, name = "ToggleModel"),
  @Type(value = UnitSelectionModel.class, name = "UnitSelectionModel"),
  @Type(value = SelectionModel.class, name = "SelectionModel"),
  @Type(value = TextAreaModel.class, name = "TextAreaModel"),
  @Type(value = NumberModel.class, name = "NumberModel"),
  @Type(value = CurrencyModel.class, name = "CurrencyModel"),
  @Type(value = LocalitySelectionModel.class, name = "LocalitySelectionModel"),
  @Type(value = GroupModel.class, name = "GroupModel"),
  @Type(value = OrganizationSelectionModel.class, name = "OrganizationSelectionModel")})
@ApiModel(subTypes = {IntegerModel.class, TextModel.class, DateModel.class, ToggleModel.class,
  UnitSelectionModel.class, SelectionModel.class, TextAreaModel.class, NumberModel.class, CurrencyModel.class,
  LocalitySelectionModel.class,
  OrganizationSelectionModel.class, GroupModel.class}, discriminator = "type", description = "Supertype of all PropertyModel.")
@NodeEntity
public class PropertyModel extends Entity {

  private Long sortIndex;

  private String name;

  private String label;

  private Session session;

  private boolean active;

  private boolean fullLine;

  private boolean required;

  public Long getSortIndex() {
    return this.sortIndex;
  }

  public void setSortIndex(final Long sortIndex) {
    this.sortIndex = sortIndex;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public Session getSession() {
    return this.session;
  }

  public void setSession(final Session session) {
    this.session = session;
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

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.sortIndex, this.name, this.label,
                        this.session, this.active, this.fullLine, this.required
    );
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || this.getClass() != o.getClass()) {
      return false;
    }
    if(!super.equals(o)) {
      return false;
    }
    final PropertyModel that = (PropertyModel) o;
    return this.active == that.active && this.fullLine == that.fullLine && this.required == that.required && Objects.equals(
      this.sortIndex, that.sortIndex) && Objects.equals(this.name, that.name) && Objects.equals(this.label, that.label)
           && Objects.equals(this.session, that.session);
  }

  @Transient
  public boolean hasSameType(final PropertyModel property) {
    if(property == null) return false;
    return this.getTypeName().equals(property.getTypeName());
  }

  @Transient
  public String getTypeName() {
    return this.getClass().getTypeName();
  }

  @Transient
  public boolean hasSameName(final PropertyModel property) {
    if(property == null) return false;
    return this.name.equals(property.name);
  }

}
