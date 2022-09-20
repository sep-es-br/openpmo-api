package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class DeliverableModel extends WorkpackModel {

  private Boolean showCompletedManagement;
  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Deliverable> instances;

  public Set<Deliverable> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Deliverable> instances) {
    this.instances = instances;
  }

  public Boolean getShowCompletedManagement() {
    return this.showCompletedManagement;
  }

  public void setShowCompletedManagement(final Boolean showCompletedManagement) {
    this.showCompletedManagement = showCompletedManagement;
  }

}
