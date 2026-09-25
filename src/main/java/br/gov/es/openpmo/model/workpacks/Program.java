package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Program extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private ProgramModel instance;

  @Relationship("MEMBER_OF")
  private Set<WorkpackModel> transversalViews;

  @Relationship("INCLUDES")
  private Set<Project> projects;

  public ProgramModel getInstance() {
    return this.instance;
  }

  public void setInstance(final ProgramModel instance) {
    this.instance = instance;
  }

  public Set<WorkpackModel> getTransversalViews() {
    if (this.transversalViews == null) {
      this.transversalViews = new HashSet<>();
    }
    return this.transversalViews;
  }

  public void setTransversalViews(final Set<WorkpackModel> transversalViews) {
    this.transversalViews = transversalViews;
  }

  public Set<Project> getProjects() {
    if (this.projects == null) {
      this.projects = new HashSet<>();
    }
    return this.projects;
  }

  public void setProjects(final Set<Project> projects) {
    this.projects = projects;
  }

  @Override
  public Workpack snapshot() {
    return new Program();
  }

  @Override
  public String getType() {
    return "Program";
  }
}
