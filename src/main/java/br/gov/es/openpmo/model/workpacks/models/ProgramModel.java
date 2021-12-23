package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Program;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class ProgramModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Program> instances;

  public Set<Program> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Program> instances) {
    this.instances = instances;
  }
}
