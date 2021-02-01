package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.gov.es.openpmo.model.relations.CanAccessOffice;

@Repository
public interface OfficePermissionRepository extends Neo4jRepository<CanAccessOffice, Long> {

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) WHERE ID(o) = $idOffice AND ID(p) = $idPerson  RETURN o,p,is")
    List<CanAccessOffice> findByIdOfficeAndIdPerson(@Param("idOffice") Long idOffice, @Param("idPerson") Long idPerson);

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) WHERE ID(o) = $idOffice RETURN o,p,is")
    List<CanAccessOffice> findByIdOffice(@Param("idOffice") Long idOffice);

}