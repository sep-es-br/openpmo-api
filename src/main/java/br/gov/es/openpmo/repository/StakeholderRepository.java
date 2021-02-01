package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.relations.IsStakeholderIn;

public interface StakeholderRepository extends Neo4jRepository<IsStakeholderIn, Long> {

    @Query("MATCH (a:Actor)-[is:IS_STAKEHOLDER_IN]->(w:Workpack) WHERE ID(w) = $idWorkpack RETURN a,is,w")
    List<IsStakeholderIn> findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (p:Actor)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack AND (ID(p) = $idActor OR $idActor is null) RETURN p,o,is")
    List<IsStakeholderIn> findByIdWorkpackAndIdActor(@Param("idWorkpack") Long idWorkpack,
            @Param("idActor") Long idActor);


    @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack AND (ID(p) = $idPerson) RETURN p,o,is")
    List<IsStakeholderIn> findByIdWorkpackAndIdPerson(@Param("idWorkpack") Long idWorkpack,
            @Param("idPerson") Long idPerson);
}
