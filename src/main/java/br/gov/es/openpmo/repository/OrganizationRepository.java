package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends CrudRepository<Organization, Long>, CustomRepository {

    @Query("MATCH (org:Organization) " +
        "OPTIONAL MATCH (org)-[r:IS_REGISTERED_IN]->(o:Office) " +
        "WHERE id(o) = $idOffice " +
        "WITH org, r, o " +
        "WHERE org.integration IS NOT NULL OR r IS NOT NULL " +
        "RETURN DISTINCT org, r, o")
    List<Organization> findByIdOffice(@Param("idOffice") Long idOffice);

    @Query("MATCH (org:Organization) " +
       "WHERE id(org) = $id " +
       "OPTIONAL MATCH (org)-[r:IS_REGISTERED_IN]->(o:Office) " +
       "WHERE id(o) = $idOffice " +
       "RETURN org, r, o")
    Optional<Organization> findByIdAndIdOffice(
        @Param("id") Long id,
        @Param("idOffice") Long idOffice);


    @Query("MATCH (p:Organization)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE id(o) = $idWorkpack RETURN p")
    List<Organization> findByIdWorkpackReturnDistinctOrganization(@Param("idWorkpack") Long idWorkpack);

    @Query(
        "MATCH (or:Organization) " +
        "OPTIONAL MATCH (or)-[r:IS_REGISTERED_IN]->(o:Office) " +
        "WHERE id(o) = $idOffice " +
        "WITH or, r, o, " +
        "apoc.text.levenshteinSimilarity(apoc.text.clean(or.name), apoc.text.clean($term)) AS nameScore, " +
        "apoc.text.levenshteinSimilarity(apoc.text.clean(or.fullName), apoc.text.clean($term)) AS fullNameScore " +
        "WITH or, r, o, " +
        "CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
        "WHERE score > $searchCutOffScore " +
        "AND (r IS NOT NULL OR or.integration IS NOT NULL) " +
        "RETURN DISTINCT or, r, o, score " +
        "ORDER BY score DESC"
        )
    List<Organization> findByIdOfficeAndByTerm(@Param("idOffice") Long idOffice,
                                               @Param("term") String term,
                                               @Param("searchCutOffScore") double searchCutOffScore);

}
