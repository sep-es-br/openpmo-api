package br.gov.es.openpmo.model.properties.models;

import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class TabModel extends PropertyModel {

  private String icon;

  @Relationship("ORGANIZES")
  private Set<PropertyModel> organizedProperties;

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public Set<PropertyModel> getOrganizedProperties() {
    return this.organizedProperties;
  }

  public void setOrganizedProperties(final Set<PropertyModel> organizedProperties) {
    this.organizedProperties = organizedProperties;
  }

}
