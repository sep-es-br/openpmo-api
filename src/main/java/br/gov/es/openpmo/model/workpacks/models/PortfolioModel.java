package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.utils.WorkpackModelInstanceType;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class PortfolioModel extends WorkpackModel {

  @Relationship(value = "IS_INSTANCE_BY", direction = Relationship.INCOMING)
  private Set<Portfolio> instances;

  public Set<Portfolio> getInstances() {
    return this.instances;
  }

  public void setInstances(final Set<Portfolio> instances) {
    this.instances = instances;
  }

  public String getType() {
    return WorkpackModelInstanceType.TYPE_NAME_MODEL_PORTFOLIO.getShortName();
  }

}
