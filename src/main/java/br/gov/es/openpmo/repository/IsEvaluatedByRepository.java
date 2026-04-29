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

  @Query(
       "MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline) " +
       "WHERE id(baseline)=$idBaseline " +

       "MATCH (workpack)-[:BELONGS_TO {linked:false}]->(plan:Plan) " +

       "OPTIONAL MATCH (workpack)-[:IS_IN*0..]->(parent:Workpack)-[:BELONGS_TO {linked:false}]->(plan) " +
       "WITH baseline, plan, collect(DISTINCT parent) + workpack AS workpacks " +

       "OPTIONAL MATCH (member:Person)-[:IS_CCB_MEMBER_FOR{active:true}]->(wp) " +
       "WHERE wp IN workpacks " +

       "OPTIONAL MATCH (member)-[:IS_CCB_MEMBER_FOR{active:true}]->(plan) " +

       "OPTIONAL MATCH (member)-[:IS_CCB_MEMBER_FOR{active:true}]->(office:Office)<-[:IS_ADOPTED_BY]-(plan) " +

       "OPTIONAL MATCH (evaluator:Person)<-[:IS_EVALUATED_BY]-(baseline) " +
     
       "WITH collect(DISTINCT member) AS members, collect(DISTINCT evaluator) AS evaluators " +
     
       "RETURN size(members) > 0 AND all(m IN members WHERE m IN evaluators) AS allCCBMembersEvaluated"
     )
     boolean wasEvaluatedByAllMembers(Long idBaseline);

  @Query("MATCH (p:Person), (b:Baseline) " +
          "WHERE id(p) = $personId AND id(b) = $baselineId " +
          "CREATE (b)-[r:IS_EVALUATED_BY {decision: $decision, inRoleWorkLocation: $inRoleWorkLocation, when: $when, comment: $comment}]->(p) " +
          "RETURN r")
  IsEvaluatedBy createIsEvaluatedBy(Long personId, Long baselineId, String decision, String inRoleWorkLocation, LocalDateTime when, String comment);

}
