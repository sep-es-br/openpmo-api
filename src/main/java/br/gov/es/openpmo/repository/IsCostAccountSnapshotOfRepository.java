package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsCostAccountSnapshotOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsCostAccountSnapshotOfRepository extends Neo4jRepository<IsCostAccountSnapshotOf, Long> {

}
