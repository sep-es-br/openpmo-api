package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IsEvaluatedByRepository extends Neo4jRepository<IsEvaluatedBy, Long> {

  @Query("MATCH (person:Person)<-[isEvaluatedBy:IS_EVALUATED_BY]-(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline AND id(person)=$idPerson " +
         "RETURN person, isEvaluatedBy, baseline")
  Optional<IsEvaluatedBy> findEvaluation(Long idBaseline, Long idPerson);

  @Query("MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline " +
         "OPTIONAL MATCH (member:Person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR{active:true}]->(workpack) " +
         "OPTIONAL MATCH (evaluator:Person)<-[:IS_EVALUATED_BY]-(baseline) " +
         "WITH count(DISTINCT evaluator) AS evaluators, count(DISTINCT member) AS members " +
         "RETURN evaluators = members")
  boolean wasEvaluatedByAllMembers(Long idBaseline);
}