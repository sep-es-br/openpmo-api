package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.person.queries.AllPersonInOfficeQuery;
import br.gov.es.openpmo.dto.person.queries.PersonByFullNameQuery;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonPermissionDetailQuery;
import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

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

  @Query(
    value =
      "MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
      "OPTIONAL MATCH (office)<-[:IS_ADOPTED_BY]-(plan:Plan) " +
      "OPTIONAL MATCH (plan)<-[:BELONGS_TO]-(workpack:Workpack) " +
      "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
      "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
      "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan) " +
      "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
      "WITH * " +
      "WHERE " +
      "    id(office)=$officeScope AND " +
      "    ( " +
      "        toLower(person.fullName) CONTAINS toLower($name) OR " +
      "        toLower(person.name) CONTAINS toLower($name) OR " +
      "        $name IS NULL " +
      "    ) " +
      "    AND " +
      "    ( " +
      "        ('ALL'=$userFilter OR $userFilter IS NULL) OR " +
      "        ('USER'=$userFilter AND exists((person)-[:IS_AUTHENTICATED_BY]->(:AuthService))) OR " +
      "        ('NON_USER'=$userFilter AND NOT exists((person)-[:IS_AUTHENTICATED_BY]->(:AuthService))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "      ('ALL'=$stakeholderFilter OR $stakeholderFilter IS NULL) OR " +
      "      ('STAKEHOLDER'=$stakeholderFilter AND exists((person)-[:IS_STAKEHOLDER_IN]->(workpack))) OR " +
      "      ('NON_STAKEHOLDER'=$stakeholderFilter AND NOT exists((person)-[:IS_STAKEHOLDER_IN]->(:Workpack))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "      ('ALL'=$ccbMemberFilter OR $ccbMemberFilter IS NULL) OR " +
      "      ('CCB_MEMBERS'=$ccbMemberFilter AND exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) OR " +
      "      ('NON_CCB_MEMBERS'=$ccbMemberFilter AND NOT exists((person)-[:IS_CCB_MEMBER_FOR]->(:Workpack))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "        ( " +
      "            ( " +
      "                $workpackScope IS NULL OR $workpackScope = [] AND " +
      "                (exists((person)-[:IS_STAKEHOLDER_IN]->(workpack)) OR exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $workpackScope IS NOT NULL OR $workpackScope <> [] " +
      "                AND " +
      "                (exists((person)-[:IS_STAKEHOLDER_IN]->(workpack)) OR exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) " +
      "                AND " +
      "                id(workpack) IN $workpackScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            ( " +
      "                $planScope IS NULL OR $planScope = [] AND " +
      "                exists((person)-[:CAN_ACCESS_PLAN]->(plan)) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $planScope IS NOT NULL OR $planScope <> [] AND " +
      "                exists((person)-[:CAN_ACCESS_PLAN]->(plan)) AND " +
      "                id(plan) IN $planScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            ( " +
      "                $workpackScope IS NULL OR $workpackScope = [] AND " +
      "                exists((person)-[:CAN_ACCESS_WORKPACK]->(workpack)) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $workpackScope IS NOT NULL OR $workpackScope <> [] AND " +
      "                exists((person)-[:CAN_ACCESS_WORKPACK]->(workpack)) AND " +
      "                id(workpack) IN $workpackScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            exists((person)-[:CAN_ACCESS_OFFICE]->(office)) " +
      "        ) " +
      "    ) " +
      "RETURN " +
      "    DISTINCT id(person) AS id, " +
      "    person.name AS name, " +
      "    person.fullName AS fullName, " +
      "    avatar, " +
      "    isInContactBookOf.email AS email " +
      "ORDER BY person.name ",
    countQuery =
      "MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
      "OPTIONAL MATCH (office)<-[:IS_ADOPTED_BY]-(plan:Plan) " +
      "OPTIONAL MATCH (plan)<-[:BELONGS_TO]-(workpack:Workpack) " +
      "OPTIONAL MATCH (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
      "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
      "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan) " +
      "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
      "WITH * " +
      "WHERE " +
      "    id(office)=$officeScope AND " +
      "    ( " +
      "        toLower(person.fullName) CONTAINS toLower($name) OR " +
      "        toLower(person.name) CONTAINS toLower($name) OR " +
      "        $name IS NULL " +
      "    ) " +
      "    AND " +
      "    ( " +
      "        ('ALL'=$userFilter OR $userFilter IS NULL) OR " +
      "        ('USER'=$userFilter AND exists((person)-[:IS_AUTHENTICATED_BY]->(:AuthService))) OR " +
      "        ('NON_USER'=$userFilter AND NOT exists((person)-[:IS_AUTHENTICATED_BY]->(:AuthService))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "      ('ALL'=$stakeholderFilter OR $stakeholderFilter IS NULL) OR " +
      "      ('STAKEHOLDER'=$stakeholderFilter AND exists((person)-[:IS_STAKEHOLDER_IN]->(workpack))) OR " +
      "      ('NON_STAKEHOLDER'=$stakeholderFilter AND NOT exists((person)-[:IS_STAKEHOLDER_IN]->(:Workpack))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "      ('ALL'=$ccbMemberFilter OR $ccbMemberFilter IS NULL) OR " +
      "      ('CCB_MEMBERS'=$ccbMemberFilter AND exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) OR " +
      "      ('NON_CCB_MEMBERS'=$ccbMemberFilter AND NOT exists((person)-[:IS_CCB_MEMBER_FOR]->(:Workpack))) " +
      "    ) " +
      "    AND " +
      "    ( " +
      "        ( " +
      "            ( " +
      "                $workpackScope IS NULL OR $workpackScope = [] AND " +
      "                (exists((person)-[:IS_STAKEHOLDER_IN]->(workpack)) OR exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $workpackScope IS NOT NULL OR $workpackScope <> [] " +
      "                AND " +
      "                (exists((person)-[:IS_STAKEHOLDER_IN]->(workpack)) OR exists((person)-[:IS_CCB_MEMBER_FOR]->(workpack))) " +
      "                AND " +
      "                id(workpack) IN $workpackScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            ( " +
      "                $planScope IS NULL OR $planScope = [] AND " +
      "                exists((person)-[:CAN_ACCESS_PLAN]->(plan)) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $planScope IS NOT NULL OR $planScope <> [] AND " +
      "                exists((person)-[:CAN_ACCESS_PLAN]->(plan)) AND " +
      "                id(plan) IN $planScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            ( " +
      "                $workpackScope IS NULL OR $workpackScope = [] AND " +
      "                exists((person)-[:CAN_ACCESS_WORKPACK]->(workpack)) " +
      "            ) " +
      "            OR " +
      "            ( " +
      "                $workpackScope IS NOT NULL OR $workpackScope <> [] AND " +
      "                exists((person)-[:CAN_ACCESS_WORKPACK]->(workpack)) AND " +
      "                id(workpack) IN $workpackScope " +
      "            ) " +
      "        ) " +
      "        OR " +
      "        ( " +
      "            exists((person)-[:CAN_ACCESS_OFFICE]->(office)) " +
      "        ) " +
      "    ) " +
      "RETURN count(DISTINCT id(person)) "
  )
  Page<AllPersonInOfficeQuery> findAllFilteringBy(
    StakeholderFilterEnum stakeholderFilter,
    UserFilterEnum userFilter,
    CcbMemberFilterEnum ccbMemberFilter,
    String name,
    Long officeScope,
    Long[] planScope,
    Long[] workpackScope,
    Pageable pageable
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
         "RETURN isInContactBookOf")
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

  @Query("MATCH (person:Person), (office:Office) " +
         "WHERE id(person)=$personId AND id(office)=$officeId " +
         "OPTIONAL MATCH (workpack:Workpack)-[]->(plan:Plan)-[]->(office) " +
         "WITH * " +
         "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
         "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan) " +
         "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
         "OPTIONAL MATCH (person)-[isStakeholderIn:IS_STAKEHOLDER_IN]->(workpack) " +
         "OPTIONAL MATCH (person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR{active: true}]->(workpack) " +
         "WITH " +
         "    person, " +
         "    office, " +
         "    plan, " +
         "    workpack, " +
         "    canAccessOffice, " +
         "    canAccessPlan, " +
         "    canAccessWorkpack, " +
         "    isStakeholderIn, " +
         "    isCCBMemberFor " +
         "RETURN " +
         "    person, " +
         "    office, " +
         "    plan, " +
         "    workpack, " +
         "    canAccessOffice, " +
         "    canAccessPlan, " +
         "    canAccessWorkpack, " +
         "    isStakeholderIn, " +
         "    isCCBMemberFor")
  Set<PersonPermissionDetailQuery> findPermissions(
    Long personId,
    Long officeId
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

}
