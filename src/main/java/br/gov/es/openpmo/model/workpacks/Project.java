package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;

@NodeEntity
public class Project extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private ProjectModel instance;
  private Boolean completed;
  private LocalDate endManagementDate;

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

  public Boolean getCompleted() {
    return this.completed;
  }

  public void setCompleted(final boolean completed) {
    this.completed = completed;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }

  public void setEndManagementDate(final LocalDate endManagementDate) {
    this.endManagementDate = endManagementDate;
  }
}
