package br.gov.es.openpmo.model.preprojects.models;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class PreProjectModel extends Entity {

  private boolean active;

  private CriteriaOperation operation;

  @Relationship("IS_ADOPTED_BY")
  private Office office;

  @Relationship(value = "INSTANTIATES", direction = Relationship.INCOMING)
  private Set<PreProject> instances;

  @Relationship(value = "FEATURES", direction = Relationship.INCOMING)
  private Set<PropertyModel> properties;

  public PreProjectModel() {
    this.active = false;
    this.operation = CriteriaOperation.AVERAGE;
    this.properties = new HashSet<>();
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Set<PreProject> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<PreProject> instances) {
    this.instances = instances;
  }

  public Set<PropertyModel> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<PropertyModel> properties) {
    this.properties = properties;
  }

}
