package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Organizer extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private OrganizerModel instance;

  public OrganizerModel getInstance() {
    return this.instance;
  }

  public void setInstance(final OrganizerModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Organizer();
  }

}
