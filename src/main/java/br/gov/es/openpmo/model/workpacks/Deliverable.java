package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Deliverable extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private DeliverableModel instance;

  public DeliverableModel getInstance() {
    return this.instance;
  }

  public void setInstance(final DeliverableModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    final Deliverable deliverable = new Deliverable();
    //deliverable.setInstance(this.instance);
    return deliverable;
  }

}
