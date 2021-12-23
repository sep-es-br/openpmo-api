package br.gov.es.openpmo.dto.treeview.query;

import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OfficeTreeViewQuery {
  private Office office;
  private List<Plan> plans;

  public OfficeTreeViewQuery() {
  }

  public OfficeTreeViewQuery(final Office office, final List<Plan> plans) {
    this.office = office;
    this.plans = plans;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public List<Plan> getPlans() {
    return this.plans;
  }

  public void setPlans(final List<Plan> plans) {
    this.plans = plans;
  }

}
