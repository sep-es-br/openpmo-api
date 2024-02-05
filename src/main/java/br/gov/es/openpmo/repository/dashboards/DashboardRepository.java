package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.Dashboard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

  @Query("MATCH (dashboard:Dashboard)-[blt:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
      ",(dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) " +
      "WHERE id(workpack) IN $ids AND month.date <  $date " +
      "RETURN dashboard, blt , workpack, isPartOf, month, [" +
      "  [ (dashboard)<-[:IS_PART_OF]-(:DashboardMonth)<-[isAt:IS_AT]-(entities) | [ isAt, entities] ] " +
      "] "
  )
  List<Dashboard> findAllByWorkpackId(List<Long> ids, LocalDate date);

  @Query("MATCH (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
         "WHERE id(workpack)=$workpackId " +
         "RETURN dashboard, [" +
         "  [ (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) | [isPartOf, month] ], " +
         "  [ (dashboard)<-[:IS_PART_OF]-(:DashboardMonth)<-[isAt:IS_AT]-(entities) | [ isAt, entities] ] " +
         "] "
  )
  Optional<Dashboard> findByWorkpackId(Long workpackId);

  @Query("MATCH (dashboard:Dashboard)-[:BELONGS_TO]->(workpack:Workpack{deleted:false,canceled:false}) " +
         "WHERE id(workpack)=$workpackId " +
         "RETURN dashboard, [" +
         "  [ (dashboard)<-[isPartOf:IS_PART_OF]-(month:DashboardMonth) | [isPartOf, month] ] " +
         "]")
  Optional<Dashboard> findByWorkpackIdForInterval(Long workpackId);

  @Query("MATCH (d:Dashboard)<-[:IS_PART_OF]-(dm:DashboardMonth)<-[:IS_AT]-(n) " +
         "DETACH DELETE d, dm, n")
  void purgeAllDashboards();

}
