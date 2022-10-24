package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Dashboard extends Entity {

  private String tripleConstraint;

  private String earnedValueAnalysis;

  @Relationship("BELONGS_TO")
  private Workpack workpack;

  public String getTripleConstraint() {
    return this.tripleConstraint;
  }

  public void setTripleConstraint(final String tripleConstraint) {
    this.tripleConstraint = tripleConstraint;
  }

  public String getEarnedValueAnalysis() {
    return this.earnedValueAnalysis;
  }

  public void setEarnedValueAnalysis(final String earnedValueAnalysis) {
    this.earnedValueAnalysis = earnedValueAnalysis;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

}
