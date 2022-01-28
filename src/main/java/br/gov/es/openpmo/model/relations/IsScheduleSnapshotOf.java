package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.schedule.Schedule;
import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type = "IS_SNAPSHOT_OF")
public class IsScheduleSnapshotOf extends IsSnapshotOf<Schedule> {

  public IsScheduleSnapshotOf(
      final Schedule master,
      final Schedule snapshot
  ) {
    super(master, snapshot);
  }

}
