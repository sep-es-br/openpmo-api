package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface IsEvaluatedByRepository extends Neo4jRepository<IsEvaluatedBy, Long> {

  @Query("MATCH (person:Person)<-[isEvaluatedBy:IS_EVALUATED_BY]-(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline AND id(person)=$idPerson " +
         "RETURN person, isEvaluatedBy, baseline")
  Optional<IsEvaluatedBy> findEvaluation(
    Long idBaseline,
    Long idPerson
  );


  @Query("MATCH (person:Person)<-[isEvaluatedBy:IS_EVALUATED_BY]-(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN person, isEvaluatedBy, baseline")
  Set<IsEvaluatedBy> findAllEvaluations(Long idBaseline);


  @Query("MATCH (person:Person)<-[isEvaluatedBy:IS_EVALUATED_BY]-(baseline:Baseline)" +
         "<-[isBaselinedBy:IS_BASELINED_BY]-(workpack:Workpack) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN person, isEvaluatedBy, baseline, isBaselinedBy, workpack")
  Set<Person> findEvaluators(Long idBaseline);

  @Query("MATCH (p:Person), (b:Baseline) " +
          "WHERE id(p) = $personId AND id(b) = $baselineId " +
          "CREATE (b)-[r:IS_EVALUATED_BY {decision: $decision, inRoleWorkLocation: $inRoleWorkLocation, when: $when, comment: $comment}]->(p) " +
          "RETURN r")
  IsEvaluatedBy createIsEvaluatedBy(Long personId, Long baselineId, String decision, String inRoleWorkLocation, LocalDateTime when, String comment);

}
