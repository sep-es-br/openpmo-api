package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.schedule.Schedule;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@RelationshipProperties
public class IsScheduleSnapshotOf extends IsSnapshotOf<Schedule> {

  public IsScheduleSnapshotOf(
    final Schedule master,
    final Schedule snapshot
  ) {
    super(master, snapshot);
  }

}
