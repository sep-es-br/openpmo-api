package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.UnitMeasure;

public interface UnitMeasureRepository extends Neo4jRepository<UnitMeasure, Long> {

    @Query("MATCH (u:UnitMeasure)-[a:AVAILABLE_IN]->(o:Office) "
               + "WHERE ID(o) = $idOffice "
               + "RETURN u,a,o")
    List<UnitMeasure> findByOffice(@Param("idOffice") Long idOffice);

}
