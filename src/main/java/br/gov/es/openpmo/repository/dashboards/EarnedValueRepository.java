package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.EarnedValue;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarnedValueRepository extends Neo4jRepository<EarnedValue, Long> {

}
