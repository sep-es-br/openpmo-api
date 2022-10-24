package br.gov.es.openpmo.model.properties.models;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Node
public class GroupModel extends PropertyModel {

  @Relationship(type = "GROUPS")
  private Set<PropertyModel> groupedProperties;

  public Set<PropertyModel> getGroupedProperties() {
    return this.groupedProperties;
  }

  public void setGroupedProperties(final Set<PropertyModel> groupedProperties) {
    this.groupedProperties = groupedProperties;
  }

}
