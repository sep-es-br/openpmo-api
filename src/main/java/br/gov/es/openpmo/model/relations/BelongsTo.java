package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Workpack;

import org.springframework.data.annotation.Transient;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;


@RelationshipProperties() //(type = "BELONGS_TO")
public class BelongsTo {

  @RelationshipId
  private Long id;

  private Boolean linked;

  //@Node
  //private Workpack workpack;

  @TargetNode
  private Plan plan;

  public BelongsTo() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Boolean getLinked() {
    return this.linked;
  }

  public void setLinked(final Boolean linked) {
    this.linked = linked;
  }

  /*
  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }
*/
  public Plan getPlan() {
    return this.plan;
  }

  public void setPlan(final Plan plan) {
    this.plan = plan;
  }

  @Transient
  public Long getIdPlan() {
    return this.plan.getId();
  }

}
