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

  @Query("MATCH (w:Workpack), (j:JournalEntry), (p:Person) " +
         "WHERE ($scope IS NULL OR $scope=[] OR id(w) IN $scope) " +
         "  AND (  " +
         "    (w)<-[:SCOPE_TO]-(j) OR (w)<-[:IS_IN*]-(:Workpack)<-[:SCOPE_TO]-(j)  " +
         ") AND (   " +
         "    (($from IS NULL OR (datetime($from) < datetime(j.date))) AND ($to IS NULL OR datetime(j.date) < datetime($to))) " +
         "    AND  " +
         "    (j.type=$journalType OR $journalType='ALL')  " +
         ") AND ( " +
         "    (p)<-[:IS_RECORDED_BY]-(j) " +
         ")" +
         "RETURN j")
  List<JournalEntry> findAll(LocalDate from, LocalDate to, JournalType journalType, List<Integer> scope);

  @Query("match (j:JournalEntry)-[:SCOPE_TO]->(w:Workpack) " +
         "where id(j)=$journalId " +
         "return id(w)")
  Optional<Long> findWorkpackIdByJournalId(Long journalId);

  @Query("match (j:JournalEntry)-[:IS_RECORDED_BY]->(p:Person) " +
         "where id(j)=$journalId " +
         "return p")
  Optional<Person> findPersonByJournalId(Long journalId);

  @Query("match (j:JournalEntry)<-[:IS_EVIDENCE_OF]->(f:File) " +
         "where id(j)=$journalId " +
         "return f")
  List<File> findFileByJournalId(Long journalId);

}
