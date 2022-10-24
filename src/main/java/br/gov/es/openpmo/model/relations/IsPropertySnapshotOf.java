package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.properties.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

@RelationshipProperties
public class IsPropertySnapshotOf extends IsSnapshotOf<Property> {

  public IsPropertySnapshotOf(
    final Property master,
    final Property snapshot
  ) {
    super(master, snapshot);
  }

}
