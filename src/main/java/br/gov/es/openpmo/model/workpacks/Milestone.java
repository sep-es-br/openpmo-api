package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
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

  @Override
  public String getType() {
    return "Milestone";
  }
}
