package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRepository extends CrudRepository<Organization, Long>, CustomRepository {

  @Query("MATCH (or:Organization)-[is:IS_REGISTERED_IN]->(o:Office) WHERE ID(o) = $idOffice RETURN o,or")
  List<Organization> findByIdOffice(@Param("idOffice") Long idOffice);

  @Query("MATCH (p:Organization)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack RETURN p")
  List<Organization> findByIdWorkpackReturnDistinctOrganization(@Param("idWorkpack") Long idWorkpack);

}
