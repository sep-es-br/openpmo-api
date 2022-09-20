package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Organizer;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class OrganizerModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Organizer> instances;

  public Set<Organizer> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Organizer> instances) {
    this.instances = instances;
  }

}
