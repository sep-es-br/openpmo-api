package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.CostData;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostDataRepository extends Neo4jRepository<CostData, Long> {

}
