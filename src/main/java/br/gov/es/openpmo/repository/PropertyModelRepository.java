package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.PropertyModel;

public interface PropertyModelRepository extends Neo4jRepository<PropertyModel, Long> {

    @Query("MATCH (p:PropertyModel)<-[:FEATURES]-(w:WorkpackModel) WHERE ID(w) = $idWorkpackModel RETURN p ")
    List<PropertyModel> findAllByIdWorkpackModel(@Param("idWorkpackModel") Long idWorkpackModel);

}
