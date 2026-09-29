package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.model.workpacks.Program;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface TransversalRepository extends Neo4jRepository<Program, Long> {

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
    + "OPTIONAL MATCH (project)-[:IS_IN]->(parent:Workpack) "
    + "OPTIONAL MATCH (program)-[participation:INCLUDES]->(project) "
    + "WHERE structuralModel.classification = 'STRUCTURAL' "
    + "AND project.deleted = false "
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
    + "OPTIONAL MATCH (project)-[:IS_INSTANCE_BY]->(structuralModel:ProjectModel) "
    + "OPTIONAL MATCH (project)-[:IS_IN]->(parent:Workpack)-[:BELONGS_TO]->(plan) "
    + "WHERE id(program) = $idTransversalProgram "
    + "AND programModel.classification = 'TRANSVERSAL' AND view.classification = 'TRANSVERSAL' "
    + "WITH program, programModel, plan, view, project, structuralModel, parent, "
    + "CASE WHEN project.deleted = false AND EXISTS((project)-[:BELONGS_TO]->(plan)) "
    + "AND structuralModel.classification = 'STRUCTURAL' "
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
    + "AND structuralModel.classification = 'STRUCTURAL' AND project.deleted = false "
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
