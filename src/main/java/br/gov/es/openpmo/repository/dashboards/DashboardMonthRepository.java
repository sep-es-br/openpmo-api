package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardMonthRepository extends Neo4jRepository<DashboardMonth, Long> {

  @Query("match (month:DashboardMonth) " +
    "optional match (month)<-[:IS_AT]-(nodes) " +
    "with month, nodes " +
    "where id(month) in $monthsId " +
    "detach delete month, nodes")
  void deleteWithNodes(List<Long> monthsId);

}
