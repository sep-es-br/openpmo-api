package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Transient;

@RelationshipEntity(type = "BELONGS_TO")
public class BelongsTo {

  @Id
  @GeneratedValue
  private Long id;

  private Boolean linked;

  @StartNode
  private Workpack workpack;

  @EndNode
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

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

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
