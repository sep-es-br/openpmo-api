package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TextModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Text extends Property<Text, String> {

  private String value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private TextModel driver;

  public Text() {
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
    final Text text = (Text) o;
    return Objects.equals(this.driver, text.driver);
  }

  @Override
  public Text snapshot() {
    final Text text = new Text();
    text.setValue(this.value);
    return text;
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
  public boolean hasChanges(final Text other) {
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
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public TextModel getDriver() {
    return this.driver;
  }

  public void setDriver(final TextModel driver) {
    this.driver = driver;
  }

}
