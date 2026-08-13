package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.WorkPlace;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface WorkPlaceRepository extends Neo4jRepository<WorkPlace, Long> {

  @Query("MATCH (workPlace:WorkPlace)-[:OF]->(:Person) " +
         "MATCH (workPlace)-[:`FOR`]->(:Office) " +
         "WHERE id(workPlace)=$workPlaceId " +
         "OPTIONAL MATCH (organization:Organization)-[:`IS`]->(workPlace) " +
         "RETURN organization")
  Optional<Organization> findOrganizationByWorkPlaceId(@Param("workPlaceId") Long workPlaceId);

  @Query("MATCH (workPlace:WorkPlace)-[:OF]->(person:Person) " +
         "MATCH (workPlace)-[:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "OPTIONAL MATCH (organization:Organization)-[:`IS`]->(workPlace) " +
         "RETURN organization")
  Optional<Organization> findOrganizationByPersonAndOffice(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId
  );

  @Query("MATCH (workPlace:WorkPlace)-[:OF]->(person:Person) " +
         "MATCH (workPlace)-[:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "MATCH (organization:Organization) WHERE id(organization)=$organizationId " +
         "OPTIONAL MATCH (:Organization)-[current:`IS`]->(workPlace) " +
         "WITH workPlace, organization, collect(current) AS currentRelationships " +
         "FOREACH (relationship IN currentRelationships | DELETE relationship) " +
         "MERGE (organization)-[:`IS`]->(workPlace) " +
         "RETURN count(workPlace)")
  Long replaceOrganization(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId,
    @Param("organizationId") Long organizationId
  );

  @Query("MATCH (workPlace:WorkPlace)-[:OF]->(person:Person) " +
         "MATCH (workPlace)-[:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "WITH workPlace ORDER BY id(workPlace) " +
         "WITH collect(workPlace) AS workPlaces " +
         "WHERE size(workPlaces) > 1 " +
         "UNWIND tail(workPlaces) AS duplicate " +
         "DETACH DELETE duplicate " +
         "RETURN count(*)")
  Long removeDuplicateWorkPlaces(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId
  );

  @Query("MATCH (person:Person)-[incorrect:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)-[:`FOR`]->(office:Office) " +
         "MERGE (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office) " +
         "ON CREATE SET contact = properties(incorrect) " +
         "MERGE (workPlace)-[:OF]->(person) " +
         "DELETE incorrect " +
         "RETURN count(workPlace)")
  Long repairIncorrectContactTargets();

  @Query("MATCH (person:Person)-[incorrect:LEGACY_IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "MERGE (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office) " +
         "ON CREATE SET contact = properties(incorrect) " +
         "DELETE incorrect " +
         "RETURN count(contact)")
  Long removeAccidentalLegacyContacts();

  @Query("MATCH (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WHERE NOT (person)<-[:OF]-(:WorkPlace)-[:`FOR`]->(office) " +
         "CREATE (workPlace:WorkPlace) " +
         "CREATE (workPlace)-[:OF]->(person) " +
         "CREATE (workPlace)-[:`FOR`]->(office) " +
         "RETURN count(workPlace)")
  Long createMissingWorkPlaces();

  @Query("MATCH (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "MATCH (organization:Organization) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "AND id(organization)=$organizationId " +
         "AND NOT (person)<-[:OF]-(:WorkPlace)-[:`FOR`]->(office) " +
         "CREATE (workPlace:WorkPlace) " +
         "CREATE (workPlace)-[:OF]->(person) " +
         "CREATE (workPlace)-[:`FOR`]->(office) " +
         "CREATE (organization)-[:`IS`]->(workPlace) " +
         "RETURN count(workPlace)")
  Long createWorkPlaceWithOrganizationIfMissing(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId,
    @Param("organizationId") Long organizationId
  );

  @Query("MATCH (workPlace:WorkPlace)-[incorrect:`IS`]->(organization:Organization) " +
         "MERGE (organization)-[:`IS`]->(workPlace) " +
         "DELETE incorrect " +
         "RETURN count(workPlace)")
  Long repairOrganizationDirection();

  @Query("MATCH (person:Person)-[worksIn:WORKS_IN]->(organization:Organization) " +
         "MATCH (workPlace:WorkPlace)-[:OF]->(person) " +
         "WITH person, organization, worksIn, collect(workPlace) AS workPlaces " +
         "FOREACH (workPlace IN workPlaces | MERGE (organization)-[:`IS`]->(workPlace)) " +
         "DELETE worksIn " +
         "RETURN count(DISTINCT person)")
  Long migratePersonOrganizations();

  @Query("MATCH (person:Person)-[incorrect:LEGACY_WORKS_IN]->(organization:Organization) " +
         "MATCH (workPlace:WorkPlace)-[:OF]->(person) " +
         "WITH person, organization, incorrect, collect(workPlace) AS workPlaces " +
         "FOREACH (workPlace IN workPlaces | MERGE (organization)-[:`IS`]->(workPlace)) " +
         "DELETE incorrect " +
         "RETURN count(DISTINCT person)")
  Long removeAccidentalLegacyOrganizations();

  @Query("MATCH (workPlace:WorkPlace) " +
         "WHERE NOT (workPlace)-[:`FOR`]->(:Office) " +
         "OR NOT (workPlace)-[:OF]->(:Person) " +
         "DETACH DELETE workPlace " +
         "RETURN count(workPlace)")
  Long removeInvalidWorkPlaces();
}
