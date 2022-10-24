package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Project extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private ProjectModel instance;

  public ProjectModel getInstance() {
    return this.instance;
  }

  public void setInstance(final ProjectModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Project();
  }

}
