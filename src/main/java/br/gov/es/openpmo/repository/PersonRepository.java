package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.person.queries.AllPersonInOfficeQuery;
import br.gov.es.openpmo.dto.person.queries.AllPersonPermissionQuery;
import br.gov.es.openpmo.dto.person.queries.PersonAndEmailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonRepository extends Neo4jRepository<Person, Long> {


  @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "OPTIONAL MATCH (person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
         "WITH person, isAuthenticatedBy, authService, isInContactBookOf, office, isPortraitOf, avatar " +
         "WHERE isAuthenticatedBy.email=$email " +
         "RETURN person, isAuthenticatedBy, authService, isInContactBookOf, office, isPortraitOf, avatar"
  )
  Optional<Person> findByEmail(String email);

  @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "WHERE id(person)=$id AND authService.server=$authenticationServiceName " +
         "RETURN person AS person, isAuthenticatedBy.email AS email, isAuthenticatedBy, authService"
  )
  Optional<PersonAndEmailQuery> findByIdPersonWithRelationshipAuthServiceAcessoCidadao(Long id, String authenticationServiceName);

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) " +
         "WHERE id(o) = $idOffice " +
         "RETURN p"
  )
  List<Person> findByIdOfficeReturnDistinctPerson(@Param("idOffice") Long idOffice);

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE id(o) = $idPlan RETURN p")
  List<Person> findByIdPlanReturnDistinctPerson(@Param("idPlan") Long idPlan);

  @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE id(o) = $idWorkpack RETURN p")
  List<Person> findByIdWorkpackReturnDistinctPerson(@Param("idWorkpack") Long idWorkpack);

  @Query(
    "MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office)" +
    "WHERE " +
    "id(office)=$officeScope AND " +
    "(lower(person.fullName) CONTAINS lower($name) OR lower(person.name) CONTAINS lower($name) OR $name IS NULL) AND" +
    "(" +
    "	('ALL'=$userFilter OR $userFilter IS NULL) OR " +
    "	('USER'=$userFilter AND (person)-[:IS_AUTHENTICATED_BY]->(:Office)) OR " +
    "	('NON_USER'=$userFilter AND NOT (person)-[:IS_AUTHENTICATED_BY]->(:Office)) " +
    ") AND " +
    "(" +
    "	('ALL'=$stakeholderFilter OR $stakeholderFilter IS NULL) OR " +
    "	('STAKEHOLDER'=$stakeholderFilter AND (person)-[:IS_STAKEHOLDER_IN]->(:Workpack)) OR " +
    "	('NON_STAKEHOLDER'=$stakeholderFilter AND NOT (person)-[:IS_STAKEHOLDER_IN]->(:Workpack)) " +
    ") " +
    "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File)" +
    "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
    "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan) " +
    "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack) " +
    "WITH person, canAccessOffice, canAccessPlan, canAccessWorkpack, plan, workpack, office, isInContactBookOf, " +
    "isPortraitOf, avatar " +
    "WHERE " +
    "(id(plan) IN $planScope OR $planScope IS NULL) AND " +
    "(id(workpack) IN $workpackScope OR $workpackScope IS NULL)  " +
    "RETURN DISTINCT id(person) AS id, " +
    "person.name AS name, " +
    "avatar AS avatar, " +
    "isInContactBookOf.email AS email"
  )
  List<AllPersonInOfficeQuery> findAllFilteringBy(
    String stakeholderFilter,
    String userFilter,
    String name,
    Long officeScope,
    Long[] planScope,
    Long[] workpackScope
  );

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "MATCH (person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
         "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File)" +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
         "WITH person, canAccessOffice, office, canAccessPlan, plan, isInContactBookOf, " +
         "isAdoptedBy, isAuthenticatedBy, authService, isPortraitOf, avatar " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "RETURN person, office, authService, " +
         "isInContactBookOf AS contact, " +
         "canAccessOffice AS canAccessOffice, " +
         "isAuthenticatedBy AS authentication, " +
         "avatar AS avatar, " +
         "isPortraitOf, " +
         "collect(canAccessPlan) AS canAccessPlans, " +
         "collect(plan) AS plans, " +
         "collect(isAdoptedBy)"
  )
  Optional<PersonDetailQuery> findPersonDetailsBy(Long personId, Long officeId);

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WHERE id(person)=$idPerson AND id(office)=$idOffice " +
         "RETURN isInContactBookOf"
  )
  Optional<IsInContactBookOf> findContactBookBy(Long idPerson, Long idOffice);

  @Query(
    "MATCH (person:Person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office:Office) " +
    "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
    "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
    "WITH person, canAccessOffice, office, canAccessPlan, plan, " +
    "isAdoptedBy, canAccessWorkpack, workpack, belongsTo " +
    "WHERE id(person)=$idPerson AND id(office)=$idOffice " +
    "RETURN " +
    "collect(canAccessOffice) AS canAccessOffice, " +
    "collect(canAccessPlan) AS canAccessPlan, " +
    "collect(canAccessWorkpack) AS canAccessWorkpack, " +
    "office, " +
    "person," +
    "collect(plan), " +
    "collect(isAdoptedBy), " +
    "collect(workpack), " +
    "collect(belongsTo)"
  )
  AllPersonPermissionQuery findAllPermissionBy(Long idPerson, Long idOffice);


  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
         "WITH person, isInContactBookOf, office, canAccessPlan, plan, " +
         "isAdoptedBy, canAccessWorkpack, workpack, belongsTo, canAccessOffice " +
         "WHERE person.fullName CONTAINS $partialName " +
         "AND id(workpack)=$idWorkpack " +
         "AND NOT (person)-[:IS_AUTHENTICATED_BY]->(:AuthService) " +
         "RETURN collect(person) AS persons, " +
         "collect(isInContactBookOf) AS contacts, " +
         "office"
  )
  Optional<PersonByFullNameQuery> findPersonsInOfficeByFullName(String partialName, Long idWorkpack);

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
         "WITH person, isInContactBookOf, office, canAccessPlan, plan, " +
         "isAdoptedBy, canAccessWorkpack, workpack, belongsTo, canAccessOffice " +
         "WHERE person.fullName=$fullName " +
         "AND id(workpack)=$idWorkpack " +
         "RETURN person "
  )
  Optional<Person> findPersonByFullName(String fullName, Long idWorkpack);

  @Query("MATCH (person:Person) " +
         "OPTIONAL MATCH (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WITH person, contact, office " +
         "WHERE id(person)=$personId " +
         "RETURN contact, person, office"
  )
  Set<IsInContactBookOf> findAllContactInformationByPersonId(Long personId);

  @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "WHERE person.administrator = true " +
         "RETURN person, authBy, authService"
  )
  Collection<Person> findAllAdministrators();

  @Query("MATCH (workpack:Workpack)<-[proposer:IS_STAKEHOLDER_IN]-(person:Person)  " +
         "WHERE id(person)=$idPerson AND id(workpack)=$idWorkpack " +
         "RETURN proposer, workpack, person " +
         "LIMIT 1")
  Optional<IsStakeholderIn> findProposerById(Long idPerson, Long idWorkpack);


  @Query(
    "MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authServer:AuthServer) " +
    "return person, isAuthenticatedBy, authServer"
  )
  List<Person> findAllUsers();
}
