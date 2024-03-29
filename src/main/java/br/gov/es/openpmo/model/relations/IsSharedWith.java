package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Transient;

@RelationshipEntity(type = "IS_SHARED_WITH")
public class IsSharedWith {

  @Id
  @GeneratedValue
  private Long id;
  private PermissionLevelEnum permissionLevel;

  @StartNode
  private Workpack workpack;

  @EndNode
  private Office office;

  public IsSharedWith() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public PermissionLevelEnum getPermissionLevel() {
    return this.permissionLevel;
  }

  public void setPermissionLevel(final PermissionLevelEnum permissionLevel) {
    this.permissionLevel = permissionLevel;
  }

  public String comboName() {
    return this.workpack.getName() + this.workpack.getOriginalOffice().map(a -> " (" + a.getName() + ") ").orElse("");
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  @Transient
  public Long workpackId() {
    return this.workpack.getId();
  }

  @Transient
  public WorkpackModel workpackInstance() {
    return this.workpack.getWorkpackModelInstance();
  }

  @Transient
  public Long getOfficeId() {
    return this.office.getId();
  }

  public boolean containsPlan(final Long idPlan) {
    return this.office.getPlans().stream().anyMatch(plan -> plan.getId().equals(idPlan));
  }

}
