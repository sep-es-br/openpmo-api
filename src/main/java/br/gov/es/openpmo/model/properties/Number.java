package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.NumberModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Number extends Property<Number, Double> {

  private Double value;

  private Long precision;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private NumberModel driver;

  public Number() {
  }

  @Override
  public Number snapshot() {
    final Number number = new Number();
    number.setValue(this.value);
    return number;
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
  public boolean hasChanges(final Number other) {
    return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
  }

  @Override
  public Double getValue() {
    return this.value;
  }

  @Override
  public void setValue(final Double value) {
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
    final Number number = (Number) o;
    return Objects.equals(this.driver, number.driver);
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public NumberModel getDriver() {
    return this.driver;
  }

  public void setDriver(final NumberModel driver) {
    this.driver = driver;
  }

  public Long getPrecision() {
    return precision;
  }

  public void setPrecision(Long precision) {
    this.precision = precision;
  }

}
