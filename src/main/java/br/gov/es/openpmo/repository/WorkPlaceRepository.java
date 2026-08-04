package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.WorkPlace;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface WorkPlaceRepository extends Neo4jRepository<WorkPlace, Long> {

  @Query("MATCH (:Person)-[:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)-[:`FOR`]->(:Office) " +
         "WHERE id(workPlace)=$workPlaceId " +
         "OPTIONAL MATCH (workPlace)-[:`IS`]->(organization:Organization) " +
         "RETURN organization")
  Optional<Organization> findOrganizationByWorkPlaceId(@Param("workPlaceId") Long workPlaceId);

  @Query("MATCH (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)-[:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "OPTIONAL MATCH (workPlace)-[:`IS`]->(organization:Organization) " +
         "RETURN organization")
  Optional<Organization> findOrganizationByPersonAndOffice(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId
  );

  @Query("MATCH (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)-[:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "MATCH (organization:Organization) WHERE id(organization)=$organizationId " +
         "OPTIONAL MATCH (workPlace)-[current:`IS`]->(:Organization) " +
         "WITH workPlace, organization, collect(current) AS currentRelationships " +
         "FOREACH (relationship IN currentRelationships | DELETE relationship) " +
         "CREATE (workPlace)-[:`IS`]->(organization) " +
         "RETURN organization")
  Organization replaceOrganization(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId,
    @Param("organizationId") Long organizationId
  );

  @Query("MATCH (person:Person)-[legacy:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "CREATE (person)-[backup:LEGACY_IS_IN_CONTACT_BOOK_OF]->(office) " +
         "SET backup = properties(legacy) " +
         "CREATE (workPlace:WorkPlace) " +
         "CREATE (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(workPlace) " +
         "SET contact = properties(legacy) " +
         "CREATE (workPlace)-[:`FOR`]->(office) " +
         "DELETE legacy " +
         "RETURN count(workPlace)")
  Long migrateLegacyContacts();

  @Query("MATCH (person:Person)-[legacy:WORKS_IN]->(organization:Organization) " +
         "MATCH (person)-[:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace) " +
         "WITH person, organization, legacy, collect(workPlace) AS workPlaces " +
         "CREATE (person)-[backup:LEGACY_WORKS_IN]->(organization) " +
         "SET backup = properties(legacy) " +
         "FOREACH (workPlace IN workPlaces | MERGE (workPlace)-[:`IS`]->(organization)) " +
         "DELETE legacy " +
         "RETURN count(DISTINCT person)")
  Long migrateLegacyOrganizations();
}
