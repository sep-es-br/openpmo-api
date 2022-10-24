package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
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
    return new Deliverable();
  }

}
