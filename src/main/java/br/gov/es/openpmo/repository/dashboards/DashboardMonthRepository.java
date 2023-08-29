package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardMonthRepository extends Neo4jRepository<DashboardMonth, Long> {

  @Query("match (month:DashboardMonth)<-[:IS_AT]-(nodes) " +
    "where id(month)=$monthId " +
    "detach delete month, nodes")
  void deleteWithNodes(Long monthId);

}
