package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.ScheduleData;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleDataRepository extends Neo4jRepository<ScheduleData, Long> {

}
