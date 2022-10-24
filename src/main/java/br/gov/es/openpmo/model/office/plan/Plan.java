package br.gov.es.openpmo.model.office.plan;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.BelongsTo;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.Set;

@Node
public class Plan extends Entity {

  @Relationship(type = "IS_ADOPTED_BY")
  private Office office;

  @Relationship(type = "IS_STRUCTURED_BY")
  private PlanModel planModel;

  @Relationship(type = "BELONGS_TO")
  private Set<BelongsTo> belongsTo;

  private String name;

  private String fullName;

  private LocalDate start;

  private LocalDate finish;

  public Plan() {
  }

  public Set<BelongsTo> getBelongsTo() {
    return this.belongsTo;
  }

  public void setBelongsTo(final Set<BelongsTo> belongsTo) {
    this.belongsTo = belongsTo;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public PlanModel getPlanModel() {
    return this.planModel;
  }

  public void setPlanModel(final PlanModel planModel) {
    this.planModel = planModel;
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

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getFinish() {
    return this.finish;
  }

  public void setFinish(final LocalDate finish) {
    this.finish = finish;
  }

}
