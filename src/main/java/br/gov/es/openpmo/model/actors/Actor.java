package br.gov.es.openpmo.model.actors;

import br.gov.es.openpmo.model.Entity;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public abstract class Actor extends Entity {

  private String name;

  private String fullName;

  protected Actor() {
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

}
