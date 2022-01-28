package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.schedule.Step;
import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type = "IS_SNAPSHOT_OF")
public class IsStepSnapshotOf extends IsSnapshotOf<Step> {

  public IsStepSnapshotOf(
      final Step master,
      final Step snapshot
  ) {
    super(master, snapshot);
  }

}
