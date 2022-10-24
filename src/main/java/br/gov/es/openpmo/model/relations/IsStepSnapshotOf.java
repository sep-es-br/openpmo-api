package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.schedule.Step;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@RelationshipProperties
public class IsStepSnapshotOf extends IsSnapshotOf<Step> {

  public IsStepSnapshotOf(
    final Step master,
    final Step snapshot
  ) {
    super(master, snapshot);
  }

}
