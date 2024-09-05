package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.person.detail.permissions.WorkpackPermissionDetailDto;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  @Query("MATCH (p:Person) WHERE id(p)=$id RETURN p")
  Optional<Person> findByIdThin(Long id);

  @Query(
    "MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
    "WHERE id(person)=$id AND authService.server=$authenticationServiceName " +
    "RETURN " +
    "  person AS person, " +
    "  isAuthenticatedBy.key AS key, " +
    "  isAuthenticatedBy.email AS email "
  )
  Optional<PersonQuery> findByIdPersonWithRelationshipAuthServiceAcessoCidadao(
    @Param("id") Long id,
    @Param("authenticationServiceName") String authenticationServiceName
  );

  @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) " +
         "WHERE id(o) = $idOffice " +
         "RETURN p"
  )
  List<Person> findByIdOfficeReturnDistinctPerson(@Param("idOffice") Long idOffice);

  @Query("MATCH (p:Person)-[:IS_IN_CONTACT_BOOK_OF| CAN_ACCESS_OFFICE|CAN_ACCESS_PLAN|CAN_ACCESS_WORKPACK|IS_STAKEHOLDER_IN|IS_CCB_MEMBER_FOR]->()-[:IS_IN|BELONGS_TO|IS_ADOPTED_BY*0..]->(e) " +
   "WHERE id(e) in $list " +
   "and ($name is null or " +
   " (apoc.text.clean(p.name) contains apoc.text.clean($name) or apoc.text.clean(p.fullName) contains apoc.text.clean($name)) " +
   ") " +
   "and " +
   " ( " +
   "  ('ALL'=$userFilter OR $userFilter IS NULL) OR " +
   "  ('USER'=$userFilter AND exists((p)-[:IS_AUTHENTICATED_BY]->(:AuthService))) OR " +
   "  ('NON_USER'=$userFilter AND NOT exists((p)-[:IS_AUTHENTICATED_BY]->(:AuthService))) " +
   " ) " +
   "AND " +
   "( " +
   " ('ALL'=$stakeholderFilter OR $stakeholderFilter IS NULL) OR " +
   " ('STAKEHOLDER'=$stakeholderFilter AND exists((p)-[:IS_STAKEHOLDER_IN]->()) OR " +
   " ('NON_STAKEHOLDER'=$stakeholderFilter AND NOT exists((p)-[:IS_STAKEHOLDER_IN]->())) " +
   " ) " +
   ") " +
   "AND " +
   "( " +
   " ('ALL'=$ccbMemberFilter OR $ccbMemberFilter IS NULL) OR " +
   " ('CCB_MEMBERS'=$ccbMemberFilter AND exists((p)-[:IS_CCB_MEMBER_FOR]->())) OR " +
   " ('NON_CCB_MEMBERS'=$ccbMemberFilter AND NOT exists((p)-[:IS_CCB_MEMBER_FOR]->())) " +
   ") " +
   "with distinct p " +
   "OPTIONAL MATCH (p)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
   "return distinct p, avatar " +
   "order by apoc.text.clean(p.name)")
  Streamable<Person> findAllFilteringBy(
    StakeholderFilterEnum stakeholderFilter,
    UserFilterEnum userFilter,
    CcbMemberFilterEnum ccbMemberFilter,
    String name,
    Long[] list
  );

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File)" +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
         "WITH person, office, canAccessPlan, plan, isInContactBookOf, " +
         "isAdoptedBy, isAuthenticatedBy, authService, isPortraitOf, avatar " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "RETURN person, office, authService, " +
         "isInContactBookOf AS contact, " +
         "isAuthenticatedBy AS authentication, " +
         "avatar AS avatar, " +
         "isPortraitOf, " +
         "collect(canAccessPlan) AS canAccessPlans, " +
         "collect(plan) AS plans, " +
         "collect(isAdoptedBy)")
  Optional<PersonDetailQuery> findPersonDetailsBy(
    Long personId,
    Long officeId
  );

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WHERE id(person)=$idPerson AND id(office)=$idOffice " +
         "RETURN person, office, isInContactBookOf")
  Optional<IsInContactBookOf> findContactBookBy(
    Long idPerson,
    Long idOffice
  );

  @Query("MATCH (p:Person), (o:Office)<-[]-(pl:Plan)<-[]-(w:Workpack) " +
         "WHERE id(p)=$idPerson AND id(o)=$idOffice " +
         "OPTIONAL MATCH (p)-[cao:CAN_ACCESS_OFFICE]->(o) " +
         "OPTIONAL MATCH (p)-[cap:CAN_ACCESS_PLAN]->(pl) " +
         "OPTIONAL MATCH (p)-[caw:CAN_ACCESS_WORKPACK]->(w) " +
         "WITH p, o, pl, w, cao, cap, caw " +
         "DETACH DELETE cao, cap, caw")
  void deleteAllPermissionsBy(
    Long idPerson,
    Long idOffice
  );

  @Query("MATCH (p:Person)-[i:IS_IN_CONTACT_BOOK_OF]->(o:Office)<-[]-(:Plan)<-[]-(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND toLower(p.fullName) CONTAINS toLower($partialName) " +
         "AND NOT (p)-[:IS_AUTHENTICATED_BY]->(:AuthService) " +
         "RETURN collect(DISTINCT p) AS persons, collect(i) AS contacts, o AS office")
  Optional<PersonByFullNameQuery> findPersonsInOfficeByFullName(
    String partialName,
    Long idWorkpack
  );

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)" +
         "-[belongsTo:BELONGS_TO]->(plan) " +
         "WITH person, isInContactBookOf, office, canAccessPlan, plan, " +
         "isAdoptedBy, canAccessWorkpack, workpack, belongsTo, canAccessOffice " +
         "WHERE person.fullName=$fullName " +
         "AND id(workpack)=$idWorkpack " +
         "RETURN person ")
  Optional<Person> findPersonByFullName(
    String fullName,
    Long idWorkpack
  );

  @Query("MATCH (person:Person {fullName: $fullName})-[:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
    "OPTIONAL MATCH (person)-[:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[:BELONGS_TO]->(:Plan)-[:IS_ADOPTED_BY]->(office) " +
    "WITH * " +
    "WHERE id(workpack)=$idWorkpack " +
    "RETURN count(person)>0")
  boolean existsPersonByFullName(
    String fullName,
    Long idWorkpack
  );

  @Query("MATCH (person:Person) " +
         "OPTIONAL MATCH (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WITH person, contact, office " +
         "WHERE id(person)=$personId " +
         "RETURN contact, person, office")
  Set<IsInContactBookOf> findAllContactInformationByPersonId(Long personId);

  @Query("MATCH (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WHERE id(person)=$personId " +
         "RETURN office")
  Set<Office> findOfficesByPersonId(Long personId);

  @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "WHERE person.administrator = true " +
         "RETURN person, authBy, authService")
  Collection<Person> findAllAdministrators();

  @Query("MATCH (workpack:Workpack)<-[proposer:IS_STAKEHOLDER_IN]-(person:Person)  " +
         "WHERE id(person)=$idPerson AND id(workpack)=$idWorkpack " +
         "RETURN proposer, workpack, person " +
         "LIMIT 1")
  Optional<IsStakeholderIn> findProposerById(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "OPTIONAL MATCH (person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
         "WITH person, isAuthenticatedBy, authService, isInContactBookOf, office, isPortraitOf, avatar " +
         "WHERE isAuthenticatedBy.key=$key " +
         "RETURN person, isAuthenticatedBy, authService, isInContactBookOf, office, isPortraitOf, avatar")
  Optional<Person> findByKey(@Param("key") String key);

  @Query("MATCH (person:Person)<-[:IS_FAVORITED_BY]-(workpack:Workpack) " +
         "MATCH (workpack)-[instanceBy:IS_INSTANCE_BY]->(workpackModel:WorkpackModel) " +
         "MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)" +
         "WHERE id(person)=$personId " +
         "AND id(plan)=$planId " +
         "RETURN workpack, instanceBy, workpackModel")
  Set<Workpack> findAllFavoriteWorkpackByPersonIdAndPlanId(
    Long personId,
    Long planId
  );

  @Query("MATCH (person:Person)  " +
         "OPTIONAL MATCH (workpack:Workpack)-[isFavoritedBy:IS_FAVORITED_BY]->(person) " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
         "WITH * " +
         "WHERE id(person)=$personId " +
         "RETURN person, belongsTo, isFavoritedBy, plan, workpack")
  Optional<Person> findPersonWithFavoriteWorkpacks(Long personId);

  @Query("MATCH (person:Person)-[:IS_AUTHENTICATED_BY {key: $key}]->(:AuthService) " +
    "RETURN count(person)>0")
  boolean existsByKey(@Param("key") String key);

  @Query (
      "MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) " +
      ", (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
      "WHERE id(person)=$personId AND ID(office) = $idOffice AND canAccessWorkpack.permissionLevel <> 'NONE' " +
      "RETURN ID(workpack) AS id, workpack.name AS name, canAccessWorkpack.permissionLevel AS accessLevel " +
      ", model.fontIcon AS icon, ID(plan) AS idPlan "
  )
  List<WorkpackPermissionDetailDto> findAllWorkpackPermissionDetailDtoByIdPerson(Long personId, Long idOffice);

  @Query (
      "MATCH (workpack:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel) " +
          ", (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
          "WHERE id(workpack) IN $workpackIds " +
          "RETURN ID(workpack) AS id, workpack.name AS name, 'NONE' AS accessLevel " +
          ", model.fontIcon AS icon, ID(plan) AS idPlan "
  )
  List<WorkpackPermissionDetailDto> findAllWorkpackPermissionDetailDtoByIdWorkpack(List<Long> workpackIds);


  @Query (
      "MATCH (person:Person)-[isStakeholderIn:IS_STAKEHOLDER_IN{active: true}]->(workpack:Workpack) " +
          ", (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
          "WHERE id(person)=$personId AND ID(office) = $idOffice " +
          "RETURN person, isStakeholderIn, workpack "
  )
  List<IsStakeholderIn> findAllIsStakeholderInByIdPerson(Long personId, Long idOffice);

  @Query (
      "MATCH (person:Person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR{active: true}]->(workpack:Workpack) " +
          ", (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
          "WHERE id(person)=$personId AND ID(office) = $idOffice " +
          "RETURN person, isCCBMemberFor, workpack "
  )
  List<IsCCBMemberFor> findAllIsCCBMemberForByIdPerson(Long personId, Long idOffice);

  @Query (
          "MATCH (p:Person) WHERE id(p) = $personId " +
          "SET p.name = $name, " +
          "p.fullName = $fullName " +
          "RETURN p "
  )
  Person updatePerson(Long personId, String name, String fullName);

  @Query (
          "MATCH (p:Person) WHERE id(p) = $personId " +
                  "SET p.name = $name " +
                  "RETURN p "
  )
  Person updateNamePerson(Long personId, String name);

  @Query (
          "MATCH (p:Person) WHERE id(p) = $personId " +
                  "SET p.idOffice = $idOffice, " +
                  "p.idPlan = $idPlan, " +
                  "p.idWorkpack = $idWorkpack, " +
                  "p.idWorkpackModelLinked = $idWorkpackModelLinked " +
                  "RETURN p "
  )
  Person updateLocalWork(Long personId, Long idOffice, Long idPlan, Long idWorkpack, Long idWorkpackModelLinked);

  @Query (
          "MATCH (p:Person) WHERE id(p) = $personId " +
                  "SET p.administrator = $administrator " +
                  "RETURN p "
  )
  Person setAdministratorStatus(Long personId, boolean administrator);
}
