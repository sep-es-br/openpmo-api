package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationRepository extends CrudRepository<Organization, Long>, CustomRepository {

  @Query("MATCH (or:Organization)-[is:IS_REGISTERED_IN]->(o:Office) WHERE ID(o) = $idOffice RETURN o,or")
  List<Organization> findByIdOffice(@Param("idOffice") Long idOffice);

  @Query("MATCH (p:Organization)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack RETURN p")
  List<Organization> findByIdWorkpackReturnDistinctOrganization(@Param("idWorkpack") Long idWorkpack);
  
  @Query("MATCH (or:Organization)-[is:IS_REGISTERED_IN]->(o:Office) WHERE ID(o) = $idOffice " +
		 "WITH o, or, apoc.text.levenshteinSimilarity(apoc.text.clean(or.name + or.fullName), apoc.text.clean($term)) AS score " +
		 "WHERE score > $searchCutOffScore " +
  		 "RETURN o,or " +
  		 "ORDER BY score DESC")
  List<Organization> findByIdOfficeAndByTerm(@Param("idOffice") Long idOffice,
								 		   	 @Param("term") String term,
								 		   	 @Param("searchCutOffScore") double searchCutOffScore);

}
