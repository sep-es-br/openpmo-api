package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.beans.Transient;

@RelationshipEntity(type = "IS_LINKED_TO")
public class IsLinkedTo {

  @Id
  @GeneratedValue
  private Long id;

  @StartNode
  private Workpack workpack;

  @EndNode
  private WorkpackModel workpackModel;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public WorkpackModel getWorkpackModel() {
    return this.workpackModel;
  }

  public void setWorkpackModel(final WorkpackModel workpackModel) {
    this.workpackModel = workpackModel;
  }

  @Transient
  public Long getWorkpackModelId() {
    return this.workpackModel.getId();
  }
}
