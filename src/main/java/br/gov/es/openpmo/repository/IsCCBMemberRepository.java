package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IsCCBMemberRepository extends Neo4jRepository<IsCCBMemberFor, Long> {

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$workpackId " +
         "RETURN p,c,w")
  List<IsCCBMemberFor> findAllByWorkpackId(Long workpackId);

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
  void deleteAllByPersonIdAndWorkpackId(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (p:Person)-[c:IS_CCB_MEMBER_FOR]->(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson " +
         "RETURN p,c,w")
  List<IsCCBMemberFor> findByPersonIdAndWorkpackId(
    Long idPerson,
    Long idWorkpack
  );

  @Query("MATCH (pl:Plan)-[a:IS_ADOPTED_BY]->(o:Office)<-[i:IS_IN_CONTACT_BOOK_OF]-(p:Person)-[c:IS_CCB_MEMBER_FOR]->" +
         "(w:Workpack) " +
         "WHERE id(w)=$idWorkpack AND id(p)=$idPerson AND id(pl)=$idPlan " +
         "RETURN pl,a,o,i,p,c,w")
  List<IsCCBMemberFor> findByPersonIdAndWorkpackIdAndPlanId(
    Long idPerson,
    Long idWorkpack,
    Long idPlan
  );

  @Query("MATCH (person:Person)-[ccbMember:IS_CCB_MEMBER_FOR{active:true}]->" +
         "(workpack:Workpack)-[isBaselinedBy:IS_BASELINED_BY]->(baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN person, ccbMember, workpack, isBaselinedBy, baseline")
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
  void deleteAllByPersonIdAndOfficeId(
    Long idPerson,
    Long idOffice
  );

}
