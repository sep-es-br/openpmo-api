package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends Neo4jRepository<Plan, Long>, CustomRepository {

  @Query("MATCH (p: Plan)-[r:IS_ADOPTED_BY]->(o:Office) "
         + ", (p)-[sb:IS_STRUCTURED_BY]->(pm:PlanModel) "
         + "WHERE id(o)= $id RETURN p,r,o,sb,pm")
  List<Plan> findAllInOffice(Long id);

  @Query("MATCH (workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
         "MATCH (plan)-[adoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "WHERE id(workpack)=$id AND (belongsTo.linked is null or belongsTo.linked=false) " +
         "RETURN plan, workpack, belongsTo, adoptedBy, office"
  )
  Optional<Plan> findPlanWithNotLinkedBelongsToRelationship(Long id);

  @Query("MATCH (plan:Plan)  " +
         "WHERE id(plan)=$idPlan " +
         "MATCH (workpack:Workpack)-[isIn:IS_IN]->(parent:Workpack) " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan)  " +
         "OPTIONAL MATCH (parent)-[parentBelongsTo:BELONGS_TO]->(plan)  " +
         "WITH plan, workpack, isIn, parent, belongsTo, parentBelongsTo " +
         "WHERE id(workpack)=$idWorkpack  " +
         "RETURN workpack, parent, belongsTo, parentBelongsTo, plan "
  )
  List<BelongsTo> hasLinkWithWorkpack(
    Long idWorkpack,
    Long idPlan
  );

}
