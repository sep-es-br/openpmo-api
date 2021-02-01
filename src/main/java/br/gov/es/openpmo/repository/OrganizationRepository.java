package br.gov.es.openpmo.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Organization;

public interface OrganizationRepository extends CrudRepository<Organization, Long> {

    @Query("MATCH (or:Organization)-[is:IS_REGISTERED_IN]->(o:Office) WHERE ID(o) = $idOffice RETURN o,or")
    List<Organization> findByIdOffice(@Param("idOffice") Long idOffice);

    @Query("MATCH (p:Organization)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack RETURN p")
	List<Organization> findByIdWorkpackReturnDistinctOrganization(@Param("idWorkpack") Long idWorkpack);
}
