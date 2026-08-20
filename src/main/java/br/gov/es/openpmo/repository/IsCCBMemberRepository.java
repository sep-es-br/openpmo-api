package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.workpacks.Workpack;
import java.util.List;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IsCCBMemberRepository extends Neo4jRepository<IsCCBMemberFor, Long> {

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack)<-[:IS_IN*0..]-(n:Workpack)\n" +
            "WHERE $workpackId IN [id(w), id(n)]\n" +
            "RETURN p,c,w")
  List<IsCCBMemberFor> findAllByWorkpackId(Long workpackId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w:Workpack)-[:IS_BASELINED_BY]->(:Baseline) " +
         "WHERE id(p)=$personId " +
         "RETURN w")
  List<Workpack> findAllWorkpacksByPersonId(Long personId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR{active: true}]->(target) " +
         "WHERE id(p)=$personId AND (target:Office OR target:Plan OR target:Workpack) " +
         "RETURN count(c)>0")
  boolean isActive(@Param("personId") Long personId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(p)=$idPerson AND id(w)=$idWorkpack " +
         "DETACH DELETE c")
  void deleteAllByPersonIdAndWorkpackId(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(o:Office) " +
       "WHERE id(p)=$idPerson AND id(o)=$idOffice " +
       "DELETE c")
       void deleteAllByPersonIdAndOfficeId(
       Long idPerson,
       Long idOffice
       );

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(plan:Plan) " +
       "WHERE id(p)=$idPerson AND id(plan)=$idPlan " +
       "DELETE c")
       void deleteAllByPersonIdAndPlanId(
       Long idPerson,
       Long idPlan
       );

  @Query(
          "MATCH (p:Person)-[:IS_CCB_MEMBER_FOR]->(n) " +
          "WHERE id(p) = $idPerson AND id(n) = $idTarget " +
          "RETURN count(p) > 0"
        )
  boolean existsCCMForPersonAndTarget(Long idPerson, Long idTarget);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson " +
         "RETURN p,c,w")
  List<IsCCBMemberFor> findByPersonIdAndWorkpackId(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (pl:Plan)-[a:IS_ADOPTED_BY]->(o:Office)" +
         "<-[i:IS_IN_CONTACT_BOOK_OF]-(p:Person)-[c:IS_CCB_MEMBER_FOR]->" +
         "(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson AND id(pl)=$idPlan " +
         "OPTIONAL MATCH (p)-[aut:IS_AUTHENTICATED_BY]-(autS:AuthService) " +
         "RETURN pl,a,o,i,p,c,w,aut,autS")
  List<IsCCBMemberFor> findByPersonIdAndWorkpackIdAndPlanId(
    Long idPerson,
    Long idWorkpack,
    Long idPlan
  );

  @Query(
     "MATCH (workpack:Workpack)-[isBaselinedBy:IS_BASELINED_BY]->(baseline:Baseline) " +
     "WHERE id(baseline)=$idBaseline " +

     "MATCH (workpack)-[:BELONGS_TO {linked:false}]->(plan:Plan) " +

     "OPTIONAL MATCH (workpack)-[:IS_IN*0..]->(parent:Workpack)-[:BELONGS_TO {linked:false}]->(plan) " +
     "WITH workpack, isBaselinedBy, baseline, plan, collect(DISTINCT parent) + workpack AS workpacks " +

     "MATCH (person:Person)-[ccbMember:IS_CCB_MEMBER_FOR{active:true}]->(target) " +
     "WHERE target IN workpacks " +
     "   OR target = plan " +
     "   OR (target:Office AND (target)<-[:IS_ADOPTED_BY]-(plan)) " +
   
     "RETURN DISTINCT person, ccbMember, workpack, isBaselinedBy, baseline"
   )
   Set<Person> findAllActiveMembersOfBaseline(Long idBaseline);

  @Query("MATCH (person:Person)-[ccbMember:IS_CCB_MEMBER_FOR]->(workpack:Workpack) " +
         "WHERE id(person)=$idPerson AND id(workpack)=$idWorkpack " +
         "RETURN person,ccbMember,workpack")
  List<IsCCBMemberFor> findAllByPersonIdAndWorkpackId(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (person:Person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR]->(workpack:Workpack) " +
         "WHERE id(person)=$idPerson " +
         "RETURN person, isCCBMemberFor, workpack")
  Set<IsCCBMemberFor> findAllCCBMemberOfPerson(Long idPerson);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(:Workpack)-[:BELONGS_TO]->(:Plan)-[:IS_ADOPTED_BY]->(o:Office) " +
         "WHERE id(p)=$idPerson AND id(o)=$idOffice " +
         "DETACH DELETE c")
  void deleteAllByPersonIdAndOfficeIdViaWorkpack(
    Long idPerson,
    Long idOffice
  );

  @Query("MATCH (p:Person) where id(p) = $personId " +
          "MATCH (w:Workpack) where id(w) = $workpackId " +
          "CREATE (p)-[r:IS_CCB_MEMBER_FOR {inRole: $role, workLocation: $workLocation, active: $active}]->(w) " +
          "RETURN r")
  IsCCBMemberFor createIsCCBMemberFor(@Param("personId") Long personId,
                                    @Param("workpackId") Long workpackId,
                                    @Param("role") String role,
                                    @Param("workLocation") String workLocation,
                                    @Param("active") Boolean active);

  @Query("MATCH (p:Person) WHERE id(p) = $personId " +
       "MATCH (o:Office) " +
       "WHERE id(o) = $officeId " +
       "CREATE (p)-[r:IS_CCB_MEMBER_FOR {inRole: $role, active: $active}]->(o) " +
       "RETURN r")
  IsCCBMemberFor createIsCCBMemberForByOffice(
       @Param("personId") Long personId,
       @Param("officeId") Long officeId,
       @Param("role") String role,
       @Param("active") Boolean active
       );

  @Query("MATCH (p:Person) WHERE id(p) = $personId " +
       "MATCH (plan:Plan) " +
       "WHERE id(plan) = $planId " +
       "CREATE (p)-[r:IS_CCB_MEMBER_FOR {inRole: $role, active: $active}]->(plan) " +
       "RETURN r")
  IsCCBMemberFor createIsCCBMemberForByPlan(
       @Param("personId") Long personId,
       @Param("planId") Long planId,
       @Param("role") String role,
       @Param("active") Boolean active
       );

  @Query("MATCH (p:Person)-[r:IS_CCB_MEMBER_FOR]->(o:Office) " +
       "WHERE id(p) = $idPerson AND id(o) = $idOffice " +
       "RETURN r.inRole")
  List<String> findCcbRolesByPersonAndOffice(
       Long idPerson,
       Long idOffice
       );

  @Query("MATCH (p:Person)-[r:IS_CCB_MEMBER_FOR]->(pl:Plan) " +
       "WHERE id(p) = $idPerson AND id(pl) = $idPlan " +
       "RETURN r.inRole")
  List<String> findCcbRolesByPersonAndPlan(
       Long idPerson,
       Long idPlan
       );

  @Query("MATCH (p:Person)-[:IS_CCB_MEMBER_FOR]->(o:Office) " +
       "WHERE id(o) = $idOffice " +
       "RETURN DISTINCT p")
  List<Person> findAllPersonsByOfficeId(Long idOffice);

  @Query("MATCH (p:Person)-[:IS_CCB_MEMBER_FOR]->(plan:Plan) " +
       "WHERE id(plan) = $idPlan " +
       "RETURN DISTINCT p")
  List<Person> findAllPersonsByPlanId(Long idPlan);
}
