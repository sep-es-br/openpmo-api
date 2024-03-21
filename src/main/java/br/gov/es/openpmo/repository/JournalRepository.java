package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.workpack.breakdown.structure.JournalInformationDto;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends Neo4jRepository<JournalEntry, Long> {

  @Query (
      "MATCH (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
      "WHERE j.type = 'INFORMATION' AND ID(w) IN $idsWorkpack " +
      "WITH ID(w) AS wId, max(j.date) AS maxDate " +
      "MATCH (journal:JournalEntry)-[:SCOPE_TO]->(workpack:Workpack) " +
      "WHERE ID(workpack) = wId AND journal.date = maxDate " +
      "RETURN ID(journal) AS id, ID(workpack) AS idWorkapck, journal.date AS date "
  )
  List<JournalInformationDto> findAllJournalInformationDto(List<Long> idsWorkpack);

  @Query("MATCH (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
         "WHERE id(w)=$workpackId " +
         "RETURN id(j)")
  List<Long> findAllJournalIdsByWorkpackId(Long workpackId);

  @Query("MATCH (w:Workpack), (j:JournalEntry), (p:Person) " +
          "WHERE id(w) IN $scope " +
          "AND (w)<-[:SCOPE_TO]-(j) " +
          "AND (p)<-[:IS_RECORDED_BY]-(j) " +
          "AND ($dateFrom IS NULL OR date(datetime($dateFrom)) <= date(datetime(j.date))) " +
          "AND ($dateTo IS NULL OR date(datetime(j.date)) <= date(datetime($dateTo))) " +
          "AND (j.type IN $journalType OR 'ALL' IN $journalType) " +
          "RETURN j " +
          "ORDER BY j.date DESC")
  List<JournalEntry> findAll(
    LocalDate dateFrom,
    LocalDate dateTo,
    List<JournalType> journalType,
    List<Integer> scope
  );

  @Query("MATCH (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
         "WHERE id(j)=$journalId " +
         "RETURN id(w)")
  Optional<Long> findWorkpackIdByJournalId(Long journalId);

  @Query("MATCH (j:JournalEntry)-[:IS_RECORDED_BY]->(p:Person) " +
         "WHERE id(j)=$journalId " +
         "RETURN p")
  Optional<Person> findAuthorByJournalId(Long journalId);

  @Query("MATCH (j:JournalEntry)-[:IS_RECORDED_FOR]->(p:Person) " +
         "WHERE id(j)=$journalId " +
         "RETURN p")
  Optional<Person> findTargetByJournalId(Long journalId);

  @Query("MATCH (j:JournalEntry)<-[:IS_EVIDENCE_OF]-(f:File) " +
         "WHERE id(j)=$journalId " +
         "RETURN f")
  List<File> findEvidencesByJournalId(Long journalId);

  @Query("MATCH (:JournalEntry)<-[:IS_EVIDENCE_OF]-(f:File) " +
         "WHERE id(f)=$evidenceId " +
         "DETACH DELETE f")
  void deleteEvidenceById(Long evidenceId);

  @Query("MATCH (j:JournalEntry)-[:SCOPE_TO]->(o:Office) " +
         "WHERE id(j)=$journalId " +
         "RETURN o")
  Optional<Office> findOfficeByJournalId(Long journalId);

  @Query("MATCH (j:JournalEntry)-[:SCOPE_TO]->(p:Plan) " +
         "WHERE id(j)=$journalId " +
         "RETURN p")
  Optional<Plan> findPlanByJournalId(Long journalId);

}
