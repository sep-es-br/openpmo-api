package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsCCBMember;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IsCCBMemberRepository extends Neo4jRepository<IsCCBMember, Long> {

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$workpackId " +
         "RETURN p,c,w")
  List<IsCCBMember> findAllByWorkpackId(Long workpackId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w:Workpack)-[:IS_BASELINED_BY]->(:Baseline) " +
         "WHERE id(p)=$personId " +
         "RETURN w")
  List<Workpack> findAllWorkpacksByPersonId(Long personId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR{active: true}]->(:Workpack) " +
         "WHERE id(p)=$personId " +
         "RETURN count(c)>0")
  boolean isActive(Long personId);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(p)=$idPerson AND id(w)=$idWorkpack " +
         "DETACH DELETE c")
  void deleteAllByPersonIdAndWorkpackId(Long idPerson, Long idWorkpack);

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson " +
         "RETURN p,c,w")
  List<IsCCBMember> findByPersonIdAndWorkpackId(Long idPerson, Long idWorkpack);

  @Query("MATCH (pl:Plan)-[a:IS_ADOPTED_BY]->(o:Office)<-[i:IS_IN_CONTACT_BOOK_OF]-(p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson AND id(pl)=$idPlan " +
         "RETURN pl,a,o,i,p,c,w")
  List<IsCCBMember> findByPersonIdAndWorkpackIdAndPlanId(Long idPerson, Long idWorkpack, Long idPlan);

  @Query("MATCH (person:Person)-[ccbMember:IS_CCB_MEMBER_FOR{active:true}]->(workpack:Workpack)-[isBaselinedBy:IS_BASELINED_BY]->(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN person, ccbMember, workpack, isBaselinedBy, baseline")
  List<Person> findAllActiveMembersOfBaseline(Long idBaseline);

  @Query("MATCH (person:Person)-[ccbMember:IS_CCB_MEMBER_FOR]->(workpack:Workpack) " +
         "WHERE id(person)=$idPerson AND id(workpack)=$idWorkpack " +
         "RETURN person,ccbMember,workpack")
  List<IsCCBMember> findAllByPersonIdAndWorkpackId(Long idPerson, Long idWorkpack);

  @Query(
    "MATCH (person:Person)-[isCCBMemberFor:IS_CCB_MEMBER_FOR]->(workpack:Workpack) " +
    "WHERE id(person)=$idPerson " +
    "RETURN person, isCCBMemberFor, workpack"
  )
  Set<IsCCBMember> findAllCCBMemberOfPerson(Long idPerson);
}
