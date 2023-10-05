package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Selection extends Property<Selection, String> {

  private String value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private SelectionModel driver;

  public Selection() {
  }

  @Override
  public Selection snapshot() {
    final Selection selection = new Selection();
    selection.setValue(this.value);
    return selection;
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
  public boolean hasChanges(final Selection other) {
    return (this.value != null || other.value != null)
           && (this.value == null || !this.value.equals(other.value));
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public void setValue(final String value) {
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
    final Selection selection = (Selection) o;
    return Objects.equals(this.driver, selection.driver);
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public SelectionModel getDriver() {
    return this.driver;
  }

  public void setDriver(final SelectionModel driver) {
    this.driver = driver;
  }

}
