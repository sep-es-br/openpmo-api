package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type = "IS_SNAPSHOT_OF")
public class IsWorkpackSnapshotOf extends IsSnapshotOf<Workpack> {

  public IsWorkpackSnapshotOf(final Workpack master, final Workpack snapshot) {
    super(master, snapshot);
  }

}
