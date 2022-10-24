package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@RelationshipProperties
public class IsWorkpackSnapshotOf extends IsSnapshotOf<Workpack> {

  public IsWorkpackSnapshotOf(
    final Workpack master,
    final Workpack snapshot
  ) {
    super(master, snapshot);
  }

}
