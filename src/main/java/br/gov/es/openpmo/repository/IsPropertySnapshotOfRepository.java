package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsPropertySnapshotOfRepository extends Neo4jRepository<IsPropertySnapshotOf, Long> {

}
