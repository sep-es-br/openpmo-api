package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.DateModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@NodeEntity
public class Date extends Property<Date, LocalDateTime> {

  private LocalDateTime value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private DateModel driver;

  public Date() {
  }

  @Override
  public Date snapshot() {
    final Date date = new Date();
    date.setValue(this.value);
    return date;
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
  public boolean hasChanges(final Date other) {
    return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
  }

  @Override
  public LocalDateTime getValue() {
    return this.value;
  }

  @Override
  public void setValue(final LocalDateTime value) {
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
    final Date date = (Date) o;
    return Objects.equals(this.driver, date.driver);
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public DateModel getDriver() {
    return this.driver;
  }

  public void setDriver(final DateModel driver) {
    this.driver = driver;
  }

  @Transient
  public LocalDate toLocalDate() {
    if (this.value == null) {
      return null;
    }
    return this.value.toLocalDate();
  }

}
