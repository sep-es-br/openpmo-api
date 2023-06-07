package br.gov.es.openpmo.repository;

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

  @Query("MATCH (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
         "WHERE id(w)=$workpackId " +
         "RETURN id(j)")
  List<Long> findAllJournalIdsByWorkpackId(Long workpackId);

  @Query("MATCH (w), (j:JournalEntry), (p:Person) " +
         "WHERE ($scope IS NULL OR $scope=[] OR id(w) IN $scope) " +
         " AND ( " +
         " (w)<-[:SCOPE_TO]-(j) OR (w)<-[:IS_IN*]-(:Workpack)<-[:SCOPE_TO]-(j) OR j.type='FAIL' " +
         ") AND ( " +
         " (($from IS NULL OR (date(datetime($from)) <= date(datetime(j.date)))) " +
         " AND " +
         " ($to IS NULL OR date(datetime(j.date)) <= date(datetime($to)))) " +
         " AND " +
         " (j.type IN $journalType OR 'ALL' IN $journalType) " +
         ") AND ( " +
         " (p)<-[:IS_RECORDED_BY]-(j) " +
         ")" +
         "RETURN j " +
         "ORDER BY j.date DESC")
  List<JournalEntry> findAll(
    LocalDate from,
    LocalDate to,
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
