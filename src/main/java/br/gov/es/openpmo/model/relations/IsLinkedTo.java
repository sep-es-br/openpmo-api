package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.beans.Transient;

@RelationshipProperties
public class IsLinkedTo {

  @RelationshipId
  private Long id;

  private Workpack workpack;

  @TargetNode
  private WorkpackModel workpackModel;

  public IsLinkedTo() {
  }

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
