package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsScheduleSnapshotOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsScheduleSnapshotOfRepository extends Neo4jRepository<IsScheduleSnapshotOf, Long> {

}
