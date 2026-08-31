package br.gov.es.openpmo.model.preprojects;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.properties.Property;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PreProject extends Entity {

  private String name;

  private String fullName;

  @Relationship("INSTANTIATES")
  private PreProjectModel instance;

  @Relationship(value = "FEATURES", direction = Relationship.INCOMING)
  private Set<Property> properties;

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

  public PreProjectModel getInstance() {
    return this.instance;
  }

  public void setInstance(final PreProjectModel instance) {
    this.instance = instance;
  }

  public Set<Property> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<Property> properties) {
    this.properties = properties;
  }

}
