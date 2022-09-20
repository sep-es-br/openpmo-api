package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Milestone;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class MilestoneModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Milestone> instances;

  public Set<Milestone> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Milestone> instances) {
    this.instances = instances;
  }

}
