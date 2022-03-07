package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.ScopeData;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeDataRepository extends Neo4jRepository<ScopeData, Long> {

}
