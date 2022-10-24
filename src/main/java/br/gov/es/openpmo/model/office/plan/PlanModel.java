package br.gov.es.openpmo.model.office.plan;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.Office;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.annotation.Transient;

import java.util.Optional;
import java.util.Set;

@Node
public class PlanModel extends Entity {

  @Relationship(type = "IS_ADOPTED_BY")
  private Office office;

  @Relationship(type = "IS_SHARED_WITH")
  private Set<Office> sharedWith;

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

}
