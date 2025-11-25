package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.Milestone;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("MATCH (m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->(b:Baseline{active: true}) " +
         "WHERE id(m) = $milestoneId " +
         "RETURN s.date")
  Optional<LocalDateTime> fetchMilestoneBaselineDate(Long milestoneId);

  @Query(
    "MATCH (m:Milestone)<-[:IS_SNAPSHOT_OF]-(s:Milestone)-[:COMPOSES]->(b:Baseline{active: true}) " +
    "WHERE id(m) = $idMilestone " +
    "RETURN s.date is not null and s.date <> $date"
  )
  boolean hasBaselineDateChanged(
    Long idMilestone,
    LocalDateTime date
  );
  
  @Query(
        "MATCH p=(w:Workpack)<-[:IS_IN*]-(m:Milestone)\n" +
        "where id(w) = $workpackId \n" +
        "return count(m)"
  )
  Long countMilestonesByWorkpack(Long workpackId);

}
