package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalSelectionOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalLinkedModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalWorkpackDto;
import br.gov.es.openpmo.model.workpacks.Program;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface TransversalRepository extends Neo4jRepository<Program, Long> {

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "WHERE id(program) = $idTransversalProgram AND view.classification = 'TRANSVERSAL' "
    + "RETURN count(program) > 0")
  boolean isTransversalProgram(@Param("idTransversalProgram") Long idTransversalProgram);

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel)-[:USES]->(model:WorkpackModel) "
    + "WHERE id(program) = $idTransversalProgram "
    + "AND coalesce(model.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "RETURN DISTINCT id(model) AS idWorkpackModel, "
    + "head([label IN labels(model) WHERE label ENDS WITH 'Model' AND label <> 'WorkpackModel']) AS type, "
    + "model.modelName AS modelName, model.modelNameInPlural AS modelNameInPlural, "
    + "model.fontIcon AS fontIcon, true AS currentlyConfigured ORDER BY modelNameInPlural")
  List<TransversalLinkedModelDto> findConfiguredModels(
    @Param("idTransversalProgram") Long idTransversalProgram
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:INCLUDES]->(target:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) "
    + "WHERE id(program) = $idTransversalProgram AND NOT EXISTS((view)-[:USES]->(model)) "
    + "RETURN DISTINCT id(model) AS idWorkpackModel, "
    + "head([label IN labels(model) WHERE label ENDS WITH 'Model' AND label <> 'WorkpackModel']) AS type, "
    + "model.modelName AS modelName, model.modelNameInPlural AS modelNameInPlural, "
    + "model.fontIcon AS fontIcon, false AS currentlyConfigured ORDER BY modelNameInPlural")
  List<TransversalLinkedModelDto> findHistoricalModels(
    @Param("idTransversalProgram") Long idTransversalProgram
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan) "
    + "MATCH (view)-[:USES]->(model:WorkpackModel) "
    + "MATCH (target:Workpack)-[:IS_INSTANCE_BY]->(model) "
    + "MATCH (target)-[:BELONGS_TO]->(plan) "
    + "WHERE id(program) = $idTransversalProgram AND id(model) = $idWorkpackModel "
    + "AND coalesce(model.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND coalesce(target.deleted, false) = false "
    + "OPTIONAL MATCH (target)-[:IS_IN]->(parent:Workpack) "
    + "OPTIONAL MATCH (program)-[direct:INCLUDES]->(target) "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(ancestor:Workpack)<-[:IS_IN*]-(target) "
    + "WITH target, model, plan, parent, count(DISTINCT direct) AS directCount, "
    + "count(DISTINCT ancestor) AS ancestorCount "
    + "WHERE directCount = 0 AND ancestorCount = 0 "
    + "RETURN DISTINCT id(target) AS idWorkpack, id(plan) AS idPlan, "
    + "id(model) AS idWorkpackModel, id(parent) AS originalParentId, "
    + "target.name AS name, target.fullName AS fullName, "
    + "coalesce(target.canceled, false) AS canceled, false AS alreadyIncluded, "
    + "true AS currentlyEligible, false AS coveredByIncludedAncestor ORDER BY name SKIP $skip LIMIT $limit")
  List<TransversalWorkpackDto> findEligibleWorkpacks(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpackModel") Long idWorkpackModel,
    @Param("skip") int skip,
    @Param("limit") int limit
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan) "
    + "MATCH (view)-[:USES]->(model:WorkpackModel) "
    + "MATCH (target:Workpack)-[:IS_INSTANCE_BY]->(model) "
    + "MATCH (target)-[:BELONGS_TO]->(plan) "
    + "WHERE id(program) = $idTransversalProgram AND id(model) = $idWorkpackModel "
    + "AND coalesce(model.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND coalesce(target.deleted, false) = false "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(direct:Workpack) WHERE direct = target "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(ancestor:Workpack)<-[:IS_IN*]-(target) "
    + "WITH target, count(DISTINCT direct) AS directCount, count(DISTINCT ancestor) AS ancestorCount "
    + "WHERE directCount = 0 AND ancestorCount = 0 "
    + "RETURN count(DISTINCT target)")
  long countEligibleWorkpacks(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpackModel") Long idWorkpackModel
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:INCLUDES]->(target:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) "
    + "WHERE id(program) = $idTransversalProgram AND id(model) = $idWorkpackModel "
    + "AND ($showCanceled OR coalesce(target.canceled, false) = false) "
    + "OPTIONAL MATCH (program)-[:BELONGS_TO]->(plan:Plan) "
    + "OPTIONAL MATCH (target)-[:BELONGS_TO]->(targetPlan:Plan) "
    + "OPTIONAL MATCH (target)-[:IS_IN]->(parent:Workpack) "
    + "OPTIONAL MATCH (view)-[configured:USES]->(model) "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(ancestor:Workpack)<-[:IS_IN*]-(target) "
    + "WITH target, model, plan, targetPlan, parent, count(DISTINCT configured) AS configuredCount, "
    + "count(DISTINCT ancestor) AS ancestorCount "
    + "WHERE ancestorCount = 0 "
    + "RETURN DISTINCT id(target) AS idWorkpack, id(targetPlan) AS idPlan, "
    + "id(model) AS idWorkpackModel, id(parent) AS originalParentId, "
    + "target.name AS name, target.fullName AS fullName, "
    + "coalesce(target.canceled, false) AS canceled, true AS alreadyIncluded, "
    + "(configuredCount > 0 AND targetPlan = plan "
    + "AND coalesce(model.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND coalesce(target.deleted, false) = false) AS currentlyEligible, "
    + "false AS coveredByIncludedAncestor ORDER BY name SKIP $skip LIMIT $limit")
  List<TransversalWorkpackDto> findIncludedWorkpacks(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpackModel") Long idWorkpackModel,
    @Param("showCanceled") boolean showCanceled,
    @Param("skip") int skip,
    @Param("limit") int limit
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:INCLUDES]->(target:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) "
    + "WHERE id(program) = $idTransversalProgram AND id(model) = $idWorkpackModel "
    + "AND ($showCanceled OR coalesce(target.canceled, false) = false) "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(ancestor:Workpack)<-[:IS_IN*]-(target) "
    + "WITH target, count(DISTINCT ancestor) AS ancestorCount "
    + "WHERE ancestorCount = 0 RETURN count(DISTINCT target)")
  long countIncludedWorkpacks(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpackModel") Long idWorkpackModel,
    @Param("showCanceled") boolean showCanceled
  );

  @Query("MATCH (target:Workpack) WHERE id(target) = $idWorkpack RETURN count(target) > 0")
  boolean existsWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (program:Program)-[participation:INCLUDES]->(target:Workpack) "
    + "WHERE id(program) = $idTransversalProgram AND id(target) = $idWorkpack "
    + "RETURN count(participation) > 0")
  boolean hasDirectParticipation(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpack") Long idWorkpack
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan) "
    + "MATCH (view)-[:USES]->(model:WorkpackModel) "
    + "MATCH (target:Workpack)-[:IS_INSTANCE_BY]->(model) "
    + "MATCH (target)-[:BELONGS_TO]->(plan) "
    + "WHERE id(program) = $idTransversalProgram AND id(target) = $idWorkpack "
    + "AND coalesce(model.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND coalesce(target.deleted, false) = false "
    + "OPTIONAL MATCH (program)-[:INCLUDES]->(ancestor:Workpack)<-[:IS_IN*]-(target) "
    + "RETURN count(DISTINCT target) > 0 AND count(DISTINCT ancestor) = 0")
  boolean isEligibleWorkpack(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpack") Long idWorkpack
  );

  @Query("MATCH (program:Program), (target:Workpack) "
    + "WHERE id(program) = $idTransversalProgram AND id(target) = $idWorkpack "
    + "MERGE (program)-[:INCLUDES]->(target)")
  void includeWorkpack(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpack") Long idWorkpack
  );

  @Query("MATCH (program:Program)-[participation:INCLUDES]->(target:Workpack) "
    + "WHERE id(program) = $idTransversalProgram AND id(target) = $idWorkpack "
    + "DELETE participation")
  void removeWorkpack(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idWorkpack") Long idWorkpack
  );

  @Query("MATCH (view:WorkpackModel)-[:BELONGS_TO]->(planModel:PlanModel) "
    + "MATCH (programModel:ProgramModel)-[:IS_IN*0..]->(view) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "WHERE id(view) = $idTransversalView AND view.classification = 'TRANSVERSAL' "
    + "AND programModel.classification = 'TRANSVERSAL' "
    + "OPTIONAL MATCH (programModel)-[:IS_IN]->(parentModel:WorkpackModel) "
    + "RETURN DISTINCT id(programModel) AS id, id(parentModel) AS idParentModel, programModel.modelName AS name, "
    + "programModel.modelNameInPlural AS nameInPlural ORDER BY name")
  List<TransversalProgramModelDto> findProgramModelsForView(
    @Param("idTransversalView") Long idTransversalView
  );

  @Query("MATCH (program:Program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "WHERE id(view) = $idTransversalView AND id(plan) = $idPlan "
    + "AND view.classification = 'TRANSVERSAL' AND programModel.classification = 'TRANSVERSAL' "
    + "WITH program, programModel, plan, planModel, collect(DISTINCT view) AS views "
    + "WHERE size(views) = 1 "
    + "OPTIONAL MATCH (program)-[:IS_IN]->(parent:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "RETURN id(program) AS idTransversalProgram, id(program) AS idWorkpack, "
    + "id(programModel) AS idWorkpackModel, id(parent) AS idParent, "
    + "id(head(views)) AS idTransversalView, "
    + "id(plan) AS idPlan, id(planModel) AS idPlanModel, programModel.fontIcon AS fontIcon, "
    + "program.name AS name, program.fullName AS fullName, 'Program' AS type, "
    + "'TRANSVERSAL' AS classification "
    + "ORDER BY fullName, name")
  List<TransversalProgramOptionDto> findProgramsForView(
    @Param("idTransversalView") Long idTransversalView,
    @Param("idPlan") Long idPlan
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel:PlanModel) "
    + "MATCH (plan)-[:IS_STRUCTURED_BY]->(planModel) "
    + "WHERE id(view) = $idRootTransversalViewModel "
    + "AND view.classification = 'TRANSVERSAL' AND programModel.classification = 'TRANSVERSAL' "
    + "AND ($idPlan IS NULL OR id(plan) = $idPlan) "
    + "AND ($idPlanModel IS NULL OR id(planModel) = $idPlanModel) "
    + "AND coalesce(program.deleted, false) = false "
    + "OPTIONAL MATCH (program)-[:IS_IN]->(parent:Workpack) "
    + "RETURN DISTINCT id(program) AS id, id(parent) AS parentId, program.name AS name, "
    + "program.fullName AS fullName, id(plan) AS idPlan ORDER BY fullName, name")
  List<TransversalSelectionOptionDto> findSelectionOptions(
    @Param("idRootTransversalViewModel") Long idRootTransversalViewModel,
    @Param("idPlan") Long idPlan,
    @Param("idPlanModel") Long idPlanModel
  );

  @Query("MATCH (program:Program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "WHERE id(view) = $idRootTransversalViewModel "
    + "AND view.classification = 'TRANSVERSAL' AND programModel.classification = 'TRANSVERSAL' "
    + "AND coalesce(program.deleted, false) = false "
    + "OPTIONAL MATCH (program)-[:IS_IN]->(parent:Workpack) "
    + "RETURN DISTINCT id(program) AS id, id(parent) AS parentId, program.name AS name, "
    + "program.fullName AS fullName, id(plan) AS idPlan ORDER BY fullName, name")
  List<TransversalSelectionOptionDto> findSelectionOptionsByPlanModel(
    @Param("idRootTransversalViewModel") Long idRootTransversalViewModel
  );

  @Query("MATCH (program:Program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "WHERE id(program) = $idTransversalProgram "
    + "AND programModel.classification = 'TRANSVERSAL' AND view.classification = 'TRANSVERSAL' "
    + "WITH program, programModel, plan, planModel, collect(DISTINCT view) AS views "
    + "WHERE size(views) = 1 "
    + "WITH program, programModel, plan, planModel, head(views) AS view "
    + "RETURN id(plan) AS idPlan, id(planModel) AS idPlanModel, id(view) AS idTransversalView, "
    + "id(program) AS idTransversalProgram, id(programModel) AS idWorkpackModel, id(program) AS idWorkpack")
  Optional<TransversalContextDto> findTransversalProgramContext(
    @Param("idTransversalProgram") Long idTransversalProgram
  );

  @Query("MATCH (program:Program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "WHERE id(program) = $idTransversalProgram "
    + "AND programModel.classification = 'TRANSVERSAL' AND view.classification = 'TRANSVERSAL' "
    + "WITH program, programModel, plan, planModel, collect(DISTINCT view) AS views "
    + "WHERE size(views) = 1 "
    + "WITH program, programModel, plan, planModel, head(views) AS view "
    + "MATCH (view)-[:USES]->(structuralModel:ProjectModel) "
    + "MATCH (project:Project)-[:IS_INSTANCE_BY]->(structuralModel) "
    + "MATCH (project)-[:BELONGS_TO]->(plan) "
    + "WHERE coalesce(structuralModel.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND project.deleted = false "
    + "OPTIONAL MATCH (project)-[:IS_IN]->(parent:Workpack) "
    + "OPTIONAL MATCH (program)-[participation:INCLUDES]->(project) "
    + "WITH DISTINCT program, plan, view, project, structuralModel, parent, participation "
    + "RETURN id(project) AS idProject, id(project) AS idWorkpack, id(plan) AS idPlan, "
    + "id(structuralModel) AS idWorkpackModel, id(structuralModel) AS idStructuralModel, "
    + "project.name AS name, project.fullName AS fullName, id(parent) AS originalParentId, "
    + "CASE WHEN participation IS NULL THEN false ELSE true END AS alreadyIncluded, "
    + "true AS currentlyEligible "
    + "ORDER BY name")
  List<EligibleProjectDto> findEligibleProjects(
    @Param("idTransversalProgram") Long idTransversalProgram
  );

  @Query("MATCH (program:Program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "MATCH (program)-[:INCLUDES]->(project:Project) "
    + "WHERE id(program) = $idTransversalProgram "
    + "AND programModel.classification = 'TRANSVERSAL' AND view.classification = 'TRANSVERSAL' "
    + "OPTIONAL MATCH (project)-[:IS_INSTANCE_BY]->(structuralModel:ProjectModel) "
    + "OPTIONAL MATCH (project)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]->(plan) "
    + "WITH program, programModel, plan, view, project, structuralModel, parent, "
    + "CASE WHEN project.deleted = false AND EXISTS((project)-[:BELONGS_TO]->(plan)) "
    + "AND coalesce(structuralModel.classification, 'STRUCTURAL') = 'STRUCTURAL' "
    + "AND EXISTS((view)-[:USES]->(structuralModel)) THEN true ELSE false END AS currentlyEligible "
    + "RETURN id(project) AS idProject, id(project) AS idWorkpack, id(plan) AS idPlan, "
    + "id(structuralModel) AS idWorkpackModel, id(structuralModel) AS idStructuralModel, "
    + "project.name AS name, project.fullName AS fullName, id(parent) AS originalParentId, "
    + "true AS alreadyIncluded, currentlyEligible AS currentlyEligible "
    + "ORDER BY name")
  List<EligibleProjectDto> findIncludedProjects(
    @Param("idTransversalProgram") Long idTransversalProgram
  );

  @Query("MATCH (program:Program)-[:IS_INSTANCE_BY]->(programModel:ProgramModel) "
    + "MATCH (program)-[:MEMBER_OF]->(view:WorkpackModel) "
    + "MATCH (program)-[:BELONGS_TO]->(plan:Plan)-[:IS_STRUCTURED_BY]->(planModel:PlanModel) "
    + "MATCH (programModel)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:BELONGS_TO]->(planModel) "
    + "MATCH (view)-[:USES]->(structuralModel:ProjectModel) "
    + "MATCH (project:Project)-[:IS_INSTANCE_BY]->(structuralModel) "
    + "MATCH (project)-[:BELONGS_TO]->(plan) "
    + "WHERE id(program) = $idTransversalProgram AND id(project) = $idProject "
    + "AND programModel.classification = 'TRANSVERSAL' AND view.classification = 'TRANSVERSAL' "
    + "AND coalesce(structuralModel.classification, 'STRUCTURAL') = 'STRUCTURAL' AND project.deleted = false "
    + "RETURN count(project) > 0")
  boolean isEligibleProject(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idProject") Long idProject
  );

  @Query("MATCH (project:Project) WHERE id(project) = $idProject RETURN count(project) > 0")
  boolean existsProject(@Param("idProject") Long idProject);

  @Query("MATCH (program:Program), (project:Project) "
    + "WHERE id(program) = $idTransversalProgram AND id(project) = $idProject "
    + "MERGE (program)-[:INCLUDES]->(project)")
  void includeProject(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idProject") Long idProject
  );

  @Query("MATCH (program:Program)-[participation:INCLUDES]->(project:Project) "
    + "WHERE id(program) = $idTransversalProgram AND id(project) = $idProject "
    + "DELETE participation")
  void removeProject(
    @Param("idTransversalProgram") Long idTransversalProgram,
    @Param("idProject") Long idProject
  );
}
