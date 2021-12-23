package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsStepSnapshotOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsStepSnapshotOfRepository extends Neo4jRepository<IsStepSnapshotOf, Long> {

}
