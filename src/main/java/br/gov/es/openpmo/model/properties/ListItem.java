package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class ListItem extends Entity {

  private String foreignKey;

  private String label;

  public String getForeignKey() {
    return this.foreignKey;
  }

  public void setForeignKey(final String foreignKey) {
    this.foreignKey = foreignKey;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

}
