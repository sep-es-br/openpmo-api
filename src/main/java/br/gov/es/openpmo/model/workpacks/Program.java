package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Program extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private ProgramModel instance;

  public ProgramModel getInstance() {
    return this.instance;
  }

  public void setInstance(final ProgramModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Program();
  }

}
