package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.RiskData;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskDataRepository extends Neo4jRepository<RiskData, Long> {

}
