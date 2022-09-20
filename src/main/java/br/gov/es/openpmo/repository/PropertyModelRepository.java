package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyModelRepository extends Neo4jRepository<PropertyModel, Long> {

  @Query("MATCH (p:PropertyModel)<-[:FEATURES]-(w:WorkpackModel) WHERE ID(w) = $idWorkpackModel RETURN p ")
  List<PropertyModel> findAllByIdWorkpackModel(@Param("idWorkpackModel") Long idWorkpackModel);

  @Query("MATCH(p:Property)-[d:IS_DRIVEN_BY]->(pm:PropertyModel) WHERE ID(pm)= $id return COUNT(p)")
  Long countPropertyByIdPropertyModel(@Param("id") Long id);

  @Query("match (p:Property)-[i:IS_DRIVEN_BY]->(:PropertyModel) " +
         "where id(p)=$propertyId " +
         "detach delete i")
  void deleteRelationshipByPropertyId(Long propertyId);

  @Query("match (p:Property), (m:PropertyModel) " +
         "where id(p)=$propertyId and id(m)=$propertyModelId " +
         "create (p)-[:IS_DRIVEN_BY]->(m)")
  void createRelationshipByPropertyIdAndModelId(
    Long propertyId,
    Long propertyModelId
  );

  @Query("match (p:Property)-[:IS_DRIVEN_BY]->(m:PropertyModel) " +
         "where id(p)=$propertyId " +
         "return m")
  Optional<PropertyModel> findByIdProperty(Long propertyId);

}
