package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

import java.util.Set;

@Node
public class MilestoneModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = INCOMING)
  private Set<Milestone> instances;

  public Set<Milestone> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Milestone> instances) {
    this.instances = instances;
  }

}
