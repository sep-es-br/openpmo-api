package br.gov.es.openpmo.model.properties.models;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class TabModel extends PropertyModel {

  @Relationship("ORGANIZES")
  private Set<PropertyModel> organizedProperties;

  public Set<PropertyModel> getOrganizedProperties() {
    return this.organizedProperties;
  }

  public void setOrganizedProperties(final Set<PropertyModel> organizedProperties) {
    this.organizedProperties = organizedProperties;
  }

}
