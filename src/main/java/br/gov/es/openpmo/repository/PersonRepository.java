package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.person.queries.*;
import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
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

    @Query("match (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "optional match (office)<-[:IS_ADOPTED_BY]-(plan:Plan) " +
            "optional match (plan)<-[:BELONGS_TO]-(workpack:Workpack) " +
            "optional match (person)<-[isPortraitOf:IS_A_PORTRAIT_OF]-(avatar:File) " +
            "optional match (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
            "optional match (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan) " +
            "optional match (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
            "with " +
            "    person, " +
            "    canAccessOffice, " +
            "    canAccessPlan, " +
            "    canAccessWorkpack, " +
            "    plan, " +
            "    workpack, " +
            "    office, " +
            "    isInContactBookOf, " +
            "    isPortraitOf, " +
            "    avatar " +
            "where " +
            "    id(office)=$officeScope and " +
            "    ( " +
            "        toLower(person.fullName) contains toLower($name) or " +
            "        toLower(person.name) contains toLower($name) or " +
            "        $name is null " +
            "    ) " +
            "    and " +
            "    ( " +
            "        ('ALL'=$userFilter or $userFilter is null) or " +
            "        ('USER'=$userFilter and (person)-[:IS_AUTHENTICATED_BY]->(:AuthService)) or " +
            "        ('NON_USER'=$userFilter and not (person)-[:IS_AUTHENTICATED_BY]->(:AuthService)) " +
            "    ) " +
            "    and " +
            "    ( " +
            "      ('ALL'=$stakeholderFilter or $stakeholderFilter is null) or " +
            "      ('STAKEHOLDER'=$stakeholderFilter and (person)-[:IS_STAKEHOLDER_IN]->(workpack)) or " +
            "      ('NON_STAKEHOLDER'=$stakeholderFilter and not (person)-[:IS_STAKEHOLDER_IN]->()) " +
            "    ) " +
            "    and " +
            "    ( " +
            "      ('ALL'=$ccbMemberFilter or $ccbMemberFilter is null) or " +
            "      ('CCB_MEMBERS'=$ccbMemberFilter and (person)-[:IS_CCB_MEMBER_FOR]->(workpack)) or " +
            "      ('NON_CCB_MEMBERS'=$ccbMemberFilter and not (person)-[:IS_CCB_MEMBER_FOR]->()) " +
            "    ) " +
            "    and " +
            "    ( " +
            "        ( " +
            "            $workpackScope is not null " +
            "            and " +
            "            ((person)-[:IS_STAKEHOLDER_IN]->(workpack) or (person)-[:IS_CCB_MEMBER_FOR]->(workpack)) " +
            "            and " +
            "            id(workpack) in $workpackScope " +
            "        ) " +
            "        or " +
            "        ( " +
            "            $planScope is not null and " +
            "            (person)-[:CAN_ACCESS_PLAN]->(plan) and " +
            "            id(plan) in $planScope " +
            "        ) " +
            "        or " +
            "        ( " +
            "            $workpackScope is not null and " +
            "            (person)-[:CAN_ACCESS_WORKPACK]->(workpack) and " +
            "            id(workpack) in $workpackScope " +
            "        ) " +
            "        or " +
            "        ( " +
            "            (person)-[:CAN_ACCESS_OFFICE]->(office) " +
            "        ) " +
            "    ) " +
            "return " +
            "    distinct person, " +
            "    id(person) as id, " +
            "    person.name as name, " +
            "    avatar, " +
            "    isInContactBookOf.email as email")
    List<AllPersonInOfficeQuery> findAllFilteringBy(
            StakeholderFilterEnum stakeholderFilter,
            UserFilterEnum userFilter,
            CcbMemberFilterEnum ccbMemberFilter,
            String name,
            Long officeScope,
            Long[] planScope,
            Long[] workpackScope
    );

    @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "OPTIONAL MATCH (person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
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
            "collect(isAdoptedBy)")
    Optional<PersonDetailQuery> findPersonDetailsBy(Long personId, Long officeId);

    @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "WHERE id(person)=$idPerson AND id(office)=$idOffice " +
            "RETURN isInContactBookOf")
    Optional<IsInContactBookOf> findContactBookBy(Long idPerson, Long idOffice);

    @Query("MATCH (person:Person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office:Office) " +
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
            "collect(belongsTo)")
    AllPersonPermissionQuery findAllPermissionBy(Long idPerson, Long idOffice);

    @Query("match (p:Person), (o:Office)<-[]-(pl:Plan)<-[]-(w:Workpack) " +
            "where id(p)=$idPerson and id(o)=$idOffice " +
            "optional match (p)-[cao:CAN_ACCESS_OFFICE]->(o) " +
            "optional match (p)-[cap:CAN_ACCESS_PLAN]->(pl) " +
            "optional match (p)-[caw:CAN_ACCESS_WORKPACK]->(w) " +
            "with p, o, pl, w, cao, cap, caw " +
            "detach delete cao, cap, caw")
    void deleteAllPermissionsBy(Long idPerson, Long idOffice);

    @Query("match (p:Person)-[i:IS_IN_CONTACT_BOOK_OF]->(o:Office)<-[]-(:Plan)<-[]-(w:Workpack) " +
            "where id(w)=$idWorkpack and toLower(p.fullName) contains toLower($partialName) " +
            "and not (p)-[:IS_AUTHENTICATED_BY]->(:AuthService) " +
            "return collect(distinct p) as persons, collect(i) as contacts, o as office")
    Optional<PersonByFullNameQuery> findPersonsInOfficeByFullName(String partialName, Long idWorkpack);

    @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "OPTIONAL MATCH (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
            "OPTIONAL MATCH (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office) " +
            "OPTIONAL MATCH (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack:Workpack)-[belongsTo:BELONGS_TO]->(plan) " +
            "WITH person, isInContactBookOf, office, canAccessPlan, plan, " +
            "isAdoptedBy, canAccessWorkpack, workpack, belongsTo, canAccessOffice " +
            "WHERE person.fullName=$fullName " +
            "AND id(workpack)=$idWorkpack " +
            "RETURN person ")
    Optional<Person> findPersonByFullName(String fullName, Long idWorkpack);

    @Query("MATCH (person:Person) " +
            "OPTIONAL MATCH (person)-[contact:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "WITH person, contact, office " +
            "WHERE id(person)=$personId " +
            "RETURN contact, person, office")
    Set<IsInContactBookOf> findAllContactInformationByPersonId(Long personId);

    @Query("match (person:Person)-[:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
            "where id(person)=$personId " +
            "return office")
    Set<Office> findOfficesByPersonId(Long personId);

    @Query("MATCH (person:Person)-[authBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
            "WHERE person.administrator = true " +
            "RETURN person, authBy, authService")
    Collection<Person> findAllAdministrators();

    @Query("MATCH (workpack:Workpack)<-[proposer:IS_STAKEHOLDER_IN]-(person:Person)  " +
            "WHERE id(person)=$idPerson AND id(workpack)=$idWorkpack " +
            "RETURN proposer, workpack, person " +
            "LIMIT 1")
    Optional<IsStakeholderIn> findProposerById(Long idPerson, Long idWorkpack);

    @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authServer:AuthServer) " +
            "return person, isAuthenticatedBy, authServer")
    List<Person> findAllUsers();

    @Query("match (person:Person), (office:Office)<-[]-(plan:Plan)<-[]-(workpack:Workpack) " +
            "where id(person)=$personId and id(office)=$officeId " +
            "optional match (person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office) " +
            "optional match (person)-[canAccessPlan:CAN_ACCESS_PLAN]->(plan) " +
            "optional match (person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
            "optional match (person)-[isStakeholderIn:IS_STAKEHOLDER_IN]->(workpack) " +
            "optional match (person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR{active: true}]->(workpack) " +
            "with " +
            "    person, " +
            "    office, " +
            "    plan, " +
            "    workpack, " +
            "    canAccessOffice, " +
            "    canAccessPlan, " +
            "    canAccessWorkpack, " +
            "    isStakeholderIn, " +
            "    isCCBMemberFor " +
            "return " +
            "    person, " +
            "    office, " +
            "    plan, " +
            "    workpack, " +
            "    canAccessOffice, " +
            "    canAccessPlan, " +
            "    canAccessWorkpack, " +
            "    isStakeholderIn, " +
            "    isCCBMemberFor")
    List<PersonPermissionDetailQuery> findPermissions(Long personId, Long officeId);
}
