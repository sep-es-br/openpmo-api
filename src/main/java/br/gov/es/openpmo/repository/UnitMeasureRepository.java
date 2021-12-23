package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitMeasureRepository extends Neo4jRepository<UnitMeasure, Long>, CustomRepository {

  @Query("MATCH (u:UnitMeasure)-[a:AVAILABLE_IN]->(o:Office) "
         + "WHERE id(o) = $idOffice "
         + "RETURN u,a,o")
  List<UnitMeasure> findByOffice(@Param("idOffice") Long idOffice);

}
