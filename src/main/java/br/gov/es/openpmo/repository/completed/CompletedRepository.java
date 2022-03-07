package br.gov.es.openpmo.repository.completed;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CompletedRepository extends Neo4jRepository<Workpack, Long> {

    @Query("match (w:Workpack) where id(w)=$workpackId set w.completed=$completed")
    void setCompleted(Long workpackId, Boolean completed);

    @Query("match (w:Workpack)-[:IS_IN]->(p:Workpack) where id(w)=$workpackId return id(p)")
    Long getParentId(Long workpackId);

    @Query("match (parent:Workpack)<-[:IS_IN]-(sons:Workpack) " +
            "where id(parent)=$parentId " +
            "with collect(sons) as allSons " +
            "return all(son in allSons where son.completed is not null and son.completed=true)")
    boolean allSonsAreCompleted(Long parentId);

    @Query("match (w:Workpack)-[:IS_INSTANCE_BY]->(m:WorkpackModel) " +
            "where id(w)=$workpackId " +
            "return m.scheduleSessionActive=true")
    boolean isScheduleSessionActive(Long workpackId);

    @Query("match (w:Workpack) where id(w)=$workpackId set w.endManagementDate=$endManagementDate")
    void setEndManagementDate(Long workpackId, LocalDate endManagementDate);

    @Query("match (parent:Workpack)<-[:IS_IN]-(sons:Workpack) " +
            "where id(parent)=$parentId " +
            "with collect(sons) as allSons " +
            "return all(son in allSons where son.endManagementDate is not null)")
    boolean allSonsHaveEndDate(Long parentId);

    @Query("match (parent:Workpack)<-[:IS_IN]-(sons:Workpack) " +
            "where id(parent)=$parentId " +
            "with collect(distinct sons.endManagementDate) as allEndDatesSet " +
            "unwind allEndDatesSet as allEndDates " +
            "return max(allEndDates)")
    LocalDate getLatestDateFromSons(Long parentId);

    @Query("match (w:Workpack) where id(w)=$workpackId set w.reason=$reason")
    void setReason(Long workpackId, String reason);

}
