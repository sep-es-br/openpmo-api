package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class CostAccountModel extends Entity {

  @Relationship(type = "BELONGS_TO")
  private PlanModel planModel;

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<CostAccount> instances;

  @Relationship(type = "FEATURES", direction = Relationship.INCOMING)
  private Set<PropertyModel> properties;

  public PlanModel getPlanModel() {
    return planModel;
  }

  public void setPlanModel(PlanModel planModel) {
    this.planModel = planModel;
  }

  public Set<CostAccount> getInstances() {
    return instances;
  }

  public void setInstances(Set<CostAccount> instances) {
    this.instances = instances;
  }

  public Set<PropertyModel> getProperties() {
    if (properties == null) {
      properties = new HashSet<>();
    }
    return properties;
  }

  public void setProperties(Set<PropertyModel> properties) {
    this.properties = properties;
  }
}
