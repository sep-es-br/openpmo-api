package br.gov.es.openpmo.model.office.plan;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;
import java.util.Set;

@NodeEntity
public class PlanModel extends Entity {

  @Relationship(type = "IS_ADOPTED_BY")
  private Office office;

  @Relationship(type = "IS_SHARED_WITH")
  private Set<Office> sharedWith;

  @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
  private CostAccountModel costAccountModel;

  private String name;
  private String fullName;

  @Property("public")
  private boolean publicShared;

  public PlanModel() {
  }

  public PlanModel(final Long id) {
    this.setId(id);
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Set<Office> getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Set<Office> sharedWith) {
    this.sharedWith = sharedWith;
  }

  public CostAccountModel getCostAccountModel() {
    return costAccountModel;
  }

  public void setCostAccountModel(CostAccountModel costAccountModel) {
    this.costAccountModel = costAccountModel;
  }

  public boolean isPublicShared() {
    return this.publicShared;
  }

  public void setPublicShared(final boolean publicShared) {
    this.publicShared = publicShared;
  }

  @Transient
  public Long getIdOffice() {
    return Optional.ofNullable(this.office).map(Entity::getId).orElse(null);
  }

  @Transient
  public Long getIdCostAccountModel() {
    return Optional.ofNullable(this.costAccountModel).map(Entity::getId).orElse(null);
  }

}
