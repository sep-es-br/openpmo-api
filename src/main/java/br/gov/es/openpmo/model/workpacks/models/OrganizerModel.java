package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Organizer;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

import java.util.Set;

@Node
public class OrganizerModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = INCOMING)
  private Set<Organizer> instances;

  public Set<Organizer> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Organizer> instances) {
    this.instances = instances;
  }

}
