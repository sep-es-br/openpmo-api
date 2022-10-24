package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Milestone extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private MilestoneModel instance;

  public MilestoneModel getInstance() {
    return this.instance;
  }

  public void setInstance(final MilestoneModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Milestone();
  }

}
