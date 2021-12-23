package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.properties.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type = "IS_SNAPSHOT_OF")
public class IsPropertySnapshotOf extends IsSnapshotOf<Property> {

  public IsPropertySnapshotOf(final Property master, final Property snapshot) {
    super(master, snapshot);
  }

}
