package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
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
