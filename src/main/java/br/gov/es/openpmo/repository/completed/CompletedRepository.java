package br.gov.es.openpmo.repository.completed;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Set;

@Repository
public interface CompletedRepository extends Neo4jRepository<Workpack, Long> {

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId SET w.completed=$completed")
  void setCompleted(
    Long workpackId,
    Boolean completed
  );

  @Query("MATCH (w:Workpack)-[:IS_IN]->(p:Workpack) WHERE id(w)=$workpackId RETURN id(p)")
  Long getParentId(Long workpackId);

  @Query("MATCH (parent:Workpack)<-[:IS_IN]-(sons:Workpack{deleted:false}) " +
         "WHERE id(parent)=$parentId " +
         "WITH collect(sons) AS allSons " +
         "RETURN ALL(son IN allSons WHERE son.completed IS NOT NULL AND son.completed=true)")
  boolean allSonsAreCompleted(Long parentId);

  @Query("MATCH (w:Workpack)-[:IS_INSTANCE_BY]->(m:WorkpackModel) " +
         "WHERE id(w)=$workpackId " +
         "RETURN m.scheduleSessionActive=true")
  boolean isScheduleSessionActive(Long workpackId);

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId SET w.endManagementDate=$endManagementDate")
  void setEndManagementDate(
    Long workpackId,
    LocalDate endManagementDate
  );

  @Query("MATCH (parent:Workpack)<-[:IS_IN]-(sons:Workpack{deleted:false}) " +
         "WHERE id(parent)=$parentId " +
         "RETURN sons")
  Set<Workpack> allSonsHaveEndDate(Long parentId);

  @Query("MATCH (parent:Workpack)<-[:IS_IN]-(sons:Workpack{deleted:false}) " +
         "WHERE id(parent)=$parentId " +
         "WITH collect(DISTINCT sons.endManagementDate) AS allEndDatesSet " +
         "UNWIND allEndDatesSet AS allEndDates " +
         "RETURN max(allEndDates)")
  LocalDate getLatestDateFromSons(Long parentId);

  @Query("MATCH (w:Workpack) WHERE id(w)=$workpackId SET w.reason=$reason")
  void setReason(
    Long workpackId,
    String reason
  );

}
