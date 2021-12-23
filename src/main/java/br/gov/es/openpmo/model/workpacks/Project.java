package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
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
    final Project project = new Project();
    //project.setInstance(this.instance);
    return project;
  }

}
