package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.PerformanceIndexes;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceIndexesRepository extends Neo4jRepository<PerformanceIndexes, Long> {

}
