package br.gov.es.openpmo.model.office;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.actors.WorkPlace;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class Office extends Entity {

  private String name;
  private String fullName;

  @JsonIgnoreProperties("office")
  @Relationship(type = "IS_ADOPTED_BY", direction = INCOMING)
  private Set<Plan> plans;

  @JsonIgnoreProperties("office")
  @Relationship(type = "IS_ADOPTED_BY", direction = INCOMING)
  private Set<PlanModel> plansModel;

  @JsonIgnoreProperties("office")
  @Relationship(type = "IS_ADOPTED_BY", direction = INCOMING)
  private PreProjectModel preProjectModel;

  @JsonIgnoreProperties("office")
  @Relationship(type = "FOR", direction = INCOMING)
  private Set<WorkPlace> workPlaces;

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

  public Set<Plan> getPlans() {
    return this.plans;
  }

  public void setPlans(final Set<Plan> plans) {
    this.plans = plans;
  }

  public Set<PlanModel> getPlansModel() {
    return this.plansModel;
  }

  public void setPlansModel(final Set<PlanModel> plansModel) {
    this.plansModel = plansModel;
  }

  public PreProjectModel getPreProjectModel() {
    return this.preProjectModel;
  }

  public void setPreProjectModel(final PreProjectModel preProjectModel) {
    this.preProjectModel = preProjectModel;
  }

  public Set<WorkPlace> getWorkPlaces() {
    return this.workPlaces;
  }

  public void setWorkPlaces(final Set<WorkPlace> workPlaces) {
    this.workPlaces = workPlaces;
  }

  @Transient
  public OfficeDto getDto() {
    return new OfficeDto(this);
  }

}
