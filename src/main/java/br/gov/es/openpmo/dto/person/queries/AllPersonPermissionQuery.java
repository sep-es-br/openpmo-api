package br.gov.es.openpmo.dto.person.queries;


import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import org.springframework.data.neo4j.repository.query.QueryResult;

import java.util.Collections;
import java.util.Set;

@QueryResult
public class AllPersonPermissionQuery {

  private final Set<CanAccessOffice> canAccessOffice;
  private final Set<CanAccessPlan> canAccessPlan;
  private final Set<CanAccessWorkpack> canAccessWorkpack;

  public AllPersonPermissionQuery(
    final Set<CanAccessOffice> canAccessOffice,
    final Set<CanAccessPlan> canAccessPlan,
    final Set<CanAccessWorkpack> canAccessWorkpack
  ) {
    this.canAccessOffice = Collections.unmodifiableSet(canAccessOffice);
    this.canAccessPlan = Collections.unmodifiableSet(canAccessPlan);
    this.canAccessWorkpack = Collections.unmodifiableSet(canAccessWorkpack);
  }

  public Set<CanAccessOffice> getCanAccessOffice() {
    return this.canAccessOffice;
  }

  public Set<CanAccessPlan> getCanAccessPlan() {
    return this.canAccessPlan;
  }

  public Set<CanAccessWorkpack> getCanAccessWorkpack() {
    return this.canAccessWorkpack;
  }

}
