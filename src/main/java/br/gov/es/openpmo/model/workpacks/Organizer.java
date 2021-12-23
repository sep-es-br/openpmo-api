package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
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
    final Organizer organizer = new Organizer();
    //organizer.setInstance(this.instance);
    return organizer;
  }

}
