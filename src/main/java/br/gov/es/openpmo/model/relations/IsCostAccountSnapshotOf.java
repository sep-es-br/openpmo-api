package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type = "IS_SNAPSHOT_OF")
public class IsCostAccountSnapshotOf extends IsSnapshotOf<CostAccount> {

  public IsCostAccountSnapshotOf(
    final CostAccount master,
    final CostAccount snapshot
  ) {
    super(master, snapshot);
  }

}
