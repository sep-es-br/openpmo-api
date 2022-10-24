package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Node;
import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;
import java.util.Set;

@Node
public class DeliverableModel extends WorkpackModel {

  private Boolean showCompletedManagement;
  @Relationship(value = "IS_INSTANCE_BY", direction = INCOMING)
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
