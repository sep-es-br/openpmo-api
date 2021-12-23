package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@QueryResult
public class StakeholderAndPermissionQuery {

  private final Set<IsStakeholderIn> stakeholderIn;
  private final Set<CanAccessWorkpack> workpackPermissions;

  public StakeholderAndPermissionQuery(
    final ArrayList<IsStakeholderIn> stakeholderIn,
    final ArrayList<CanAccessWorkpack> workpackPermissions
  ) {
    this.stakeholderIn = new HashSet<>(stakeholderIn);
    this.workpackPermissions = new HashSet<>(workpackPermissions);
  }

  public Set<IsStakeholderIn> getStakeholderIn() {
    return this.stakeholderIn;
  }

  public Set<CanAccessWorkpack> getWorkpackPermissions() {
    return this.workpackPermissions;
  }
}
