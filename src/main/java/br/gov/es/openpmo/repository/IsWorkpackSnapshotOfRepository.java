package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsWorkpackSnapshotOfRepository extends Neo4jRepository<IsWorkpackSnapshotOf, Long> {

}
