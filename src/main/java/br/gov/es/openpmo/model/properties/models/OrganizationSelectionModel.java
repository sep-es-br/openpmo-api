package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.model.actors.Organization;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class OrganizationSelectionModel extends PropertyModel {

  private boolean multipleSelection;

  @Relationship("DEFAULTS_TO")
  private Set<Organization> defaultValue;

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public Set<Organization> getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final Set<Organization> defaultValue) {
    this.defaultValue = defaultValue;
  }
}
