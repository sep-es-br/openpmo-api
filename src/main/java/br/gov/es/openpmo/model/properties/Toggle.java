package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.ToggleModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Toggle extends Property<Toggle, Boolean> {

  private boolean value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private ToggleModel driver;

  public Toggle() {
  }

  @Override
  public Toggle snapshot() {
    final Toggle toggle = new Toggle();
    toggle.setValue(this.value);
    return toggle;
  }

  @Override
  public CategoryEnum getCategory() {
    return this.category;
  }

  @Override
  public void setCategory(final CategoryEnum category) {
    this.category = category;
  }

  @Override
  public boolean hasChanges(final Toggle other) {
    return this.value != other.value;
  }

  @Override
  public Boolean getValue() {
    return this.value;
  }

  @Override
  public void setValue(final Boolean value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.driver);
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
    final Toggle toggle = (Toggle) o;
    return Objects.equals(this.driver, toggle.driver);
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public ToggleModel getDriver() {
    return this.driver;
  }

  public void setDriver(final ToggleModel driver) {
    this.driver = driver;
  }

}
