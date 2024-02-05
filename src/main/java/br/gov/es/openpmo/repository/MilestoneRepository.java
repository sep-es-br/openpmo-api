package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("MATCH (m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->(b:Baseline{active: true}) " +
         "WHERE id(m) = $milestoneId " +
         "RETURN s.date")
  Optional<LocalDateTime> fetchMilestoneBaselineDate(Long milestoneId);

  @Query("MATCH (m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->(b:Baseline{active:true}) " +
         "WHERE id(m) = $milestoneId " +
         "RETURN b")
  List<Baseline> fetchBaselineById(Long milestoneId);

  @Query(
    "MATCH (p:Project)<-[:IS_IN*]-(m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->" +
    "(b:Baseline{active:true})<-[:IS_BASELINED_BY]-(p) " +
    "WHERE id(m) = $idMilestone " +
    "RETURN count(m) > 0"
  )
  boolean isOnActualBaseline(Long idMilestone);

  @Query(
    "MATCH (m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->(b:Baseline{active: true}) " +
    "WHERE id(m) = $idMilestone " +
    "RETURN s.date is not null and s.date <> $date"
  )
  boolean hasBaselineDateChanged(
    Long idMilestone,
    LocalDateTime date
  );

}
