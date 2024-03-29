package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.UnitSelectionModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class UnitSelection extends Property<UnitSelection, UnitMeasure> {

  @Relationship("VALUES")
  private UnitMeasure value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private UnitSelectionModel driver;

  public UnitSelection() {
  }

  @Override
  public UnitSelection snapshot() {
    final UnitSelection unitSelection = new UnitSelection();
    unitSelection.setValue(this.value);
    return unitSelection;
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
  public UnitMeasure getValue() {
    return this.value;
  }

  @Override
  public void setValue(final UnitMeasure value) {
    this.value = value;
  }

  public UnitSelectionModel getDriver() {
    return this.driver;
  }

  public void setDriver(final UnitSelectionModel driver) {
    this.driver = driver;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.driver);
  }

  @Override
  public boolean hasChanges(final UnitSelection other) {
    return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || this.hasChangesSafe(this.value, other.value));
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
    final UnitSelection that = (UnitSelection) o;
    return Objects.equals(this.driver, that.driver);
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  private boolean hasChangesSafe(
    final UnitMeasure value,
    final UnitMeasure otherValue
  ) {
    final boolean hasNameChanges = (value.getName() != null || otherValue.getName() != null)
                                   && (value.getName() != null && otherValue.getName() == null || value.getName() == null || !value.getName().equals(
      otherValue.getName()));

    final boolean hasFullNameChanges = (value.getFullName() != null || otherValue.getFullName() != null)
                                       && (value.getFullName() != null && otherValue.getFullName() == null || value.getFullName() == null || !value.getFullName().equals(otherValue.getFullName()));

    return hasNameChanges || hasFullNameChanges;
  }

}
