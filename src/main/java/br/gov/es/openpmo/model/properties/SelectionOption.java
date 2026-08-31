package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.model.Entity;
import java.util.Objects;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class SelectionOption extends Entity {

  private Double value;

  private String label;

  private Long position;

  public Double getValue() {
    return this.value;
  }

  public void setValue(final Double value) {
    this.value = value;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public Long getPosition() {
    return this.position;
  }

  public void setPosition(final Long position) {
    this.position = position;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || this.getClass() != object.getClass() || !super.equals(object)) {
      return false;
    }
    final SelectionOption that = (SelectionOption) object;
    return Objects.equals(this.value, that.value) &&
      Objects.equals(this.label, that.label) &&
      Objects.equals(this.position, that.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.value, this.label, this.position);
  }

}
