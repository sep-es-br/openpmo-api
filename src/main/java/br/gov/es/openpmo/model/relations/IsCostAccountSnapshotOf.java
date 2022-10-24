package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@RelationshipProperties
public class IsCostAccountSnapshotOf extends IsSnapshotOf<CostAccount> {

  public IsCostAccountSnapshotOf(
    final CostAccount master,
    final CostAccount snapshot
  ) {
    super(master, snapshot);
  }

}
