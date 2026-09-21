package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import br.gov.es.openpmo.dto.preprojects.PreProjectListDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationRow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface PreProjectRepository
  extends Neo4jRepository<PreProject, Long>, CustomRepository {

  @Query(
    "MATCH (preProject:PreProject)-[:INSTANTIATES]->(:PreProjectModel)-[:IS_ADOPTED_BY]->(office:Office) " +
    "OPTIONAL MATCH (preProject)<-[:IS]-(organization:Organization) " +
    "OPTIONAL MATCH (preProject)-[:ORIGINATED]->(project:Project)-[:BELONGS_TO]->(plan:Plan) " +
    "WHERE id(office) = $idOffice " +
    "  AND (project IS NULL OR (coalesce(project.deleted, false) = false AND coalesce(project.canceled, false) = false)) " +
    "OPTIONAL MATCH (project)<-[:FEATURES]-(status:Property)-[:IS_DRIVEN_BY]->(:PropertyModel {name: 'Status'}) " +
    "RETURN id(preProject) AS id, id(preProject) AS idPreProject, id(project) AS idProject, " +
    "       preProject.name AS name, preProject.fullName AS fullName, status.value AS status, " +
    "       id(plan) AS idPlan, id(project) AS idWorkpack, id(organization) AS idOrganization " +
    "ORDER BY toLower(preProject.name), id(preProject)"
  )
  List<PreProjectListDto> findAllByOfficeId(@Param("idOffice") Long idOffice);

  @Query(
    "MATCH (preProject:PreProject)-[:INSTANTIATES]->(:PreProjectModel)<-[:FEATURES]-(criteriaTabModel:CriteriaTabModel) " +
    "MATCH (preProject)<-[:FEATURES]-(criteriaTab:CriteriaTab)-[:IS_DRIVEN_BY]->(criteriaTabModel) " +
    "MATCH propertyPath=(criteriaTab)-[:ORGANIZES|GROUPS*0..]->(property:CriteriaSelection) " +
    "-[:IS_DRIVEN_BY]->(propertyModel:CriteriaSelectionModel) " +
    "WHERE id(preProject) = $idPreProject " +
    "WITH preProject, criteriaTabModel, property, propertyModel, " +
    "     [node IN nodes(propertyPath) WHERE node:CriteriaGroup][0] AS group " +
    "OPTIONAL MATCH (group)-[:IS_DRIVEN_BY]->(groupModel:CriteriaGroupModel) " +
    "OPTIONAL MATCH (property)-[:VALUES]->(option:SelectionOption) " +
    "OPTIONAL MATCH (propertyModel)-[:ACCEPTS]->(allowedOption:SelectionOption) " +
    "WITH criteriaTabModel, property, propertyModel, group, groupModel, " +
    "     sum(DISTINCT toFloat(coalesce(option.value, 0.0))) AS note, " +
    "     max(toFloat(coalesce(allowedOption.value, 0.0))) AS maximumNote " +
    "RETURN id(criteriaTabModel) AS idCriteriaTabModel, " +
    "       criteriaTabModel.name AS criteriaTabName, " +
    "       criteriaTabModel.label AS criteriaTabLabel, " +
    "       criteriaTabModel.sortIndex AS criteriaTabSortIndex, " +
    "       criteriaTabModel.weight AS criteriaTabWeight, " +
    "       toString(criteriaTabModel.operation) AS criteriaTabOperation, " +
    "       id(group) AS idGroup, " +
    "       groupModel.name AS groupName, " +
    "       groupModel.label AS groupLabel, " +
    "       coalesce(groupModel.weight, 1.0) AS groupWeight, " +
    "       toString(groupModel.operation) AS groupOperation, " +
    "       coalesce(group.active, true) AS groupActive, " +
    "       coalesce(groupModel.disabledValue, 0.0) AS groupDisabledValue, " +
    "       id(propertyModel) AS idPropertyModel, " +
    "       propertyModel.name AS name, " +
    "       propertyModel.label AS label, " +
    "       coalesce(propertyModel.weight, 1.0) AS weight, " +
    "       note AS note, " +
    "       maximumNote AS maximumNote " +
    "ORDER BY criteriaTabSortIndex, idCriteriaTabModel, idGroup, propertyModel.sortIndex, idPropertyModel"
  )
  List<PreProjectEvaluationRow> findEvaluationRows(@Param("idPreProject") Long idPreProject);

  @Query(
    "MATCH (preProject:PreProject) " +
    "WHERE id(preProject) = $id " +
    "RETURN preProject, " +
    "[(preProject)-[instantiates:INSTANTIATES]->(preProjectModel:PreProjectModel) | " +
    "  [instantiates, preProjectModel]], " +
    "[(preProject)<-[isOrganization:IS]-(organization:Organization) | " +
    "  [isOrganization, organization]]"
  )
  Optional<PreProject> findByIdThin(@Param("id") Long id);

  @Query(
    "MATCH (preProject:PreProject), (project:Project) " +
    "WHERE id(preProject) = $idPreProject AND id(project) = $idProject " +
    "MERGE (preProject)-[:ORIGINATED]->(project)"
  )
  void createOriginatedRelationship(
    @Param("idPreProject") Long idPreProject,
    @Param("idProject") Long idProject
  );

  @Query(
    "MATCH (preProject:PreProject)<-[features:FEATURES]-(criteriaTab:CriteriaTab)" +
    "-[tabDrivenBy:IS_DRIVEN_BY]->(criteriaTabModel:CriteriaTabModel) " +
    "WHERE id(preProject) = $idPreProject " +
    "  AND id(criteriaTabModel) = $idCriteriaTabModel " +
    "RETURN criteriaTab, features, preProject, tabDrivenBy, criteriaTabModel, " +
    "[(criteriaTab)-[children:ORGANIZES|GROUPS*1..]->(child:Property) | " +
    "  [children, child]], " +
    "[(criteriaTab)-[:ORGANIZES|GROUPS*0..]->(property:Property)" +
    "  -[isDrivenBy:IS_DRIVEN_BY]->(propertyModel:PropertyModel) | " +
    "  [property, isDrivenBy, propertyModel]], " +
    "[(criteriaTab)-[:ORGANIZES|GROUPS*0..]->(property:Property)" +
    "  -[valueRelationship:CONTAINS|VALUES]->(value) | " +
    "  [property, valueRelationship, value]], " +
    "[(criteriaTab)-[:ORGANIZES|GROUPS*0..]->(:Property)" +
    "  -[:IS_DRIVEN_BY]->(propertyModel:PropertyModel)" +
    "  -[configurationRelationship:ACCEPTS]->(configuration) | " +
    "  [propertyModel, configurationRelationship, configuration]]"
  )
  Optional<CriteriaTab> findCriteriaTabByModelId(
    @Param("idPreProject") Long idPreProject,
    @Param("idCriteriaTabModel") Long idCriteriaTabModel
  );

}
