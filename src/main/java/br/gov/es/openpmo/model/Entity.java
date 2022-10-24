package br.gov.es.openpmo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.Objects;

public abstract class Entity {

  @Id
  @GeneratedValue
  private Long id;

  protected Entity() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final Entity entity = (Entity) o;
    return Objects.equals(this.id, entity.id);
  }

}
