package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
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
    final Program program = new Program();
    //program.setInstance(this.instance);
    return program;
  }

}
