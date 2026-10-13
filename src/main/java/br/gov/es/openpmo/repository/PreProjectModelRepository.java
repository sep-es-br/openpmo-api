package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface PreProjectModelRepository
  extends Neo4jRepository<PreProjectModel, Long>, CustomRepository {

  @Query(
    "MATCH (preProjectModel:PreProjectModel)-[:IS_ADOPTED_BY]->(office:Office) " +
    "WHERE id(office) = $idOffice " +
    "RETURN id(preProjectModel) " +
    "LIMIT 1"
  )
  Optional<Long> findIdByOfficeId(@Param("idOffice") Long idOffice);

  @Query(
    "MATCH (preProjectModel:PreProjectModel)-[:IS_ADOPTED_BY]->(office:Office) " +
    "WHERE id(office) = $idOffice " +
    "AND coalesce(preProjectModel.active, false) = true " +
    "RETURN count(preProjectModel) > 0"
  )
  boolean isActiveByOfficeId(@Param("idOffice") Long idOffice);

}
