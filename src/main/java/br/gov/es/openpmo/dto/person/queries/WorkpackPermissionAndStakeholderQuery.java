package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Set;

@QueryResult
public class WorkpackPermissionAndStakeholderQuery {

  private final Set<Workpack> workpacks;
  private final Set<CanAccessWorkpack> canAccess;
  private final Set<IsStakeholderIn> isStakeholderIn;


  public WorkpackPermissionAndStakeholderQuery(
    final Set<Workpack> workpacks,
    final Set<CanAccessWorkpack> canAccess,
    final Set<IsStakeholderIn> isStakeholderIn
  ) {
    this.workpacks = workpacks;
    this.canAccess = canAccess;
    this.isStakeholderIn = isStakeholderIn;
  }

  public Set<Workpack> getWorkpacks() {
    return this.workpacks;
  }

  public Set<CanAccessWorkpack> getCanAccess() {
    return this.canAccess;
  }

  public Set<IsStakeholderIn> getIsStakeholderIn() {
    return this.isStakeholderIn;
  }
}
