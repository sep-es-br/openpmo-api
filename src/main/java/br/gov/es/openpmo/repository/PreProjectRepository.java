package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface PreProjectRepository
  extends Neo4jRepository<PreProject, Long>, CustomRepository {

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
