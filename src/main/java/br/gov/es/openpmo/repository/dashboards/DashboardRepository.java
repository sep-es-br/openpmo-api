package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.Dashboard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

  @Query("match (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
    "where id(workpack)=$workpackId " +
    "optional match (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth)<-[isAt:IS_AT]-(entities) " +
    "with * " +
    "return dashboard, isPartOf, month, isAt, entities")
  Optional<Dashboard> findByWorkpackId(Long workpackId);

  @Query("match (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
    "where id(workpack)=$workpackId " +
    "optional match (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) " +
    "with * " +
    "return dashboard, isPartOf, month")
  Optional<Dashboard> findByWorkpackIdForInterval(Long workpackId);

}
