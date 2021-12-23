package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Project;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class ProjectModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Project> instances;

  public Set<Project> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Project> instances) {
    this.instances = instances;
  }
}
