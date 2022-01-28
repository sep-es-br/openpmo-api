package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends Neo4jRepository<JournalEntry, Long> {

  @Query("match (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
      "where id(w)=$workpackId " +
      "return id(j)")
  List<Long> findAllJournalIdsByWorkpackId(Long workpackId);

  @Query("match (w:Workpack), (j:JournalEntry), (p:Person) " +
      "where ($scope is null or $scope=[] or id(w) in $scope) " +
      " and ( " +
      " (w)<-[:SCOPE_TO]-(j) or (w)<-[:IS_IN*]-(:Workpack)<-[:SCOPE_TO]-(j) or j.type='FAIL' " +
      ") and ( " +
      " (($from is null or (datetime($from) < datetime(j.date))) and ($to is null or datetime(j.date) < datetime($to))) " +
      " and " +
      " (j.type=$journalType or $journalType='ALL') " +
      ") and ( " +
      " (p)<-[:IS_RECORDED_BY]-(j) " +
      ")" +
      "return j " +
      "order by j.date desc")
  List<JournalEntry> findAll(LocalDate from, LocalDate to, JournalType journalType, List<Integer> scope);

  @Query("match (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
      "where id(j)=$journalId " +
      "return id(w)")
  Optional<Long> findWorkpackIdByJournalId(Long journalId);

  @Query("match (j:JournalEntry)-[:IS_RECORDED_BY]->(p:Person) " +
      "where id(j)=$journalId " +
      "return p")
  Optional<Person> findPersonByJournalId(Long journalId);

  @Query("match (j:JournalEntry)<-[:IS_EVIDENCE_OF]-(f:File) " +
      "where id(j)=$journalId " +
      "return f")
  List<File> findEvidencesByJournalId(Long journalId);

  @Query("match (:JournalEntry)<-[:IS_EVIDENCE_OF]-(f:File) " +
      "where id(f)=$evidenceId " +
      "detach delete f")
  void deleteEvidenceById(Long evidenceId);

}
