package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDate;

@NodeEntity
public class Deliverable extends Workpack {

  private Boolean completed;
  private LocalDate endManagementDate;

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

  public Boolean getCompleted() {
    return this.completed;
  }

  public void setCompleted(final Boolean completed) {
    this.completed = completed;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }

  public void setEndManagementDate(final LocalDate endManagementDate) {
    this.endManagementDate = endManagementDate;
  }
}
