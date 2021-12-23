package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BaselineRepository extends Neo4jRepository<Baseline, Long> {

  @Query("MATCH (w:Workpack)-[ibb:IS_BASELINED_BY]->(b:Baseline) " +
         "WHERE id(b)=$idBaseline " +
         "RETURN w, [ " +
         " [(w)<-[ii:IS_IN*]-(v:Workpack) | [ii,v]], " +
         " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
         " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], " +
         " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], " +
         " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]] " +
         "]")
  Optional<Workpack> findWorkpackByBaselineId(Long idBaseline);

  @Query("MATCH (w:Workpack)-[s:IS_BASELINED_BY]->(b:Baseline{cancelation: true, status: 'PROPOSED'}) " +
         "WHERE id(w)=$idWorkpack " +
         "RETURN count(b)>0")
  boolean workpackHasCancelationProposal(Long idWorkpack);

  @Query("MATCH (w:Workpack)-[s:IS_BASELINED_BY]->(b:Baseline{status: 'PROPOSED'}) " +
         "WHERE id(w)=$idWorkpack " +
         "RETURN count(b)>0")
  boolean workpackHasPendingBaselines(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline{active: true}) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN count(baseline)>0")
  boolean workpackHasActiveBaseline(Long idWorkpack);

  @Query("MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline{active: true}) " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN baseline")
  Optional<Baseline> findActiveBaselineByWorkpackId(Long idWorkpack);

  @Query("MATCH (b:Baseline)<-[i:IS_BASELINED_BY]-(w:Workpack) " +
         "WHERE id(w)=$idWorkpack " +
         "RETURN b,i,w")
  List<Baseline> findAllByWorkpackId(Long idWorkpack);

  @Query("MATCH (w:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(w)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
         "RETURN count(s)>0")
  boolean isSnapshotOfWorkpackComposingBaseline(
    Long idWorkpack,
    Long idBaseline
  );

  @Query("MATCH (a:Workpack)-[:IS_SNAPSHOT_OF]->(w:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(a)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
         "RETURN count(s)>0")
  boolean isSnapshotOfMasterComposingBaseline(
    Long idWorkpack,
    Long idBaseline
  );

  @Query("MATCH (b:Baseline) " +
         "WHERE id(b)=$idBaseline " +
         "MATCH (s:Workpack{deleted: false}) " +
         "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack{deleted: false}) " +
         "OPTIONAL MATCH (b)<-[:COMPOSES]-(s)-[:IS_IN*]->(sp:Workpack{deleted: false})-[:COMPOSES]->(b) " +
         "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack{deleted: false}) " +
         "WITH s,m,sp,mp,b " +
         "WHERE id(s)=$idChild " +
         "WITH count(DISTINCT s) + count(DISTINCT sp) AS snapshots, " +
         "     count(DISTINCT m) + count(DISTINCT mp) AS masters " +
         "RETURN snapshots = masters")
  boolean hasStructureChanges(
    Long idChild,
    Long idBaseline
  );

  @Query("MATCH (b:Baseline) " +
         "WHERE id(b)=$idBaseline " +
         "MATCH (s:Workpack{deleted: false}) " +
         "OPTIONAL MATCH (s)-[:IS_IN*]->(sp:Workpack{deleted: false}) " +
         "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack{deleted: false})<-[:IS_SNAPSHOT_OF]-(o:Workpack)-[:COMPOSES]->(b) " +
         "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack{deleted: false})<-[:IS_SNAPSHOT_OF]-(n:Workpack)-[:COMPOSES]->(b), (n)-[:IS_IN*]->(o) " +
         "WITH s,m,sp,mp,n,b,o " +
         "WHERE id(s)=$idChild " +
         "WITH m, " +
         "     count(DISTINCT s) + count(DISTINCT sp) AS snapshots, " +
         "     count(DISTINCT o) + count(DISTINCT n) AS others " +
         "RETURN snapshots <> others and count(m)>0")
  boolean hasBaselineStructureChanges(
    Long idChild,
    Long idBaseline
  );

  @Query("MATCH (w:Workpack)<-[g1:IS_SNAPSHOT_OF]-(s:Workpack)-[c:COMPOSES]->(b:Baseline) " +
         "WHERE id(b)=$idBaseline AND id(w)=$idWorkpack " +
         "RETURN s,c,b, [ " +
         " [(s)-[i1:IS_INSTANCE_BY]->(m1:WorkpackModel) | [i1,m1]], " +
         " [(s)<-[f1:FEATURES]-(p1:Property) | [f1,p1]], " +
         " [(s)<-[fa:FEATURES]-(s1:Schedule) | [fa,s1]], " +
         " [(s)<-[fa]-(s1)<-[c1:COMPOSES]-(st1:Step) | [c1,st1]], " +
         " [(s)<-[fa]-(s1)-[c1]-(st1)-[c2:CONSUMES]->(ca1:CostAccount) | [c2,ca1]], " +
         " [(s)<-[i2:IS_IN*]-(r:Workpack)-[:COMPOSES]->(b) | [i2,r]], " +
         " [(s)<-[i2*]-(r)-[d:COMPOSES]->(b) | [d]], " +
         " [(s)<-[i2*]-(r)-[g2:IS_SNAPSHOT_OF]->(t:Workpack) | [g2,t]], " +
         " [(s)<-[i2*]-(r)-[i3:IS_INSTANCE_BY]->(m2:WorkpackModel) | [i3,m2]], " +
         " [(s)<-[i2*]-(r)<-[f2:FEATURES]-(p2:Property) | [f2,p2]], " +
         " [(s)<-[i2*]-(r)<-[fb:FEATURES]-(s2:Schedule) | [fb,s2]], " +
         " [(s)<-[i2*]-(r)<-[fb]-(s2)<-[c2:COMPOSES]-(st2:Step) | [c2,st2]], " +
         " [(s)<-[i2*]-(r)<-[fb]-(s2)<-[c2]-(st2)-[c3:CONSUMES]->(ca2:CostAccount) | [c3,ca2]] " +
         "]")
  Optional<Workpack> findSnapshotWithChildrenAndPropertiesByWorkpackIdAndBaselineId(
    Long idWorkpack,
    Long idBaseline
  );

  @Query("MATCH (m:Workpack)<-[i:IS_SNAPSHOT_OF]-(s:Workpack) " +
         "WHERE id(s)=$idSnapshot " +
         "RETURN m")
  Optional<Workpack> findMasterBySnapshotId(Long idSnapshot);

  @Query("MATCH (w:Workpack{ deleted: true })<-[:IS_SNAPSHOT_OF]-(:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(w)=$idWorkpack AND id(b)=$idBaseline " +
         "RETURN count(w)>0")
  boolean isWorkpackDeletedAndHasSnapshot(Long idWorkpack, Long idBaseline);

  @Query("MATCH (w:Workpack{ deleted: false })-[:IS_BASELINED_BY]->(b:Baseline) " +
         "WHERE id(b)=$idBaseline " +
         "RETURN w, [ " +
         " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
         " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
         " [(w)<-[i:IS_IN*]-(v:Workpack{ deleted: false }) | [i,v]], " +
         " [(v)<-[h:FEATURES]-(q:Property) | [h,q]], " +
         " [(v)-[ii:IS_INSTANCE_BY]->(n:WorkpackModel) | [ii,n]], " +
         " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], " +
         " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], " +
         " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], " +
         " [(v)-[f5:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f5,l,v1,l1]], " +
         " [(v)-[f6:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f6,o,v2,o1]], " +
         " [(v)-[f7:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f7,u,v3,u1]] " +
         "]")
  Optional<Workpack> findNotDeletedWorkpackWithPropertiesAndModelAndChildrenByBaselineId(Long idBaseline);

  @Query("MATCH (baseline:Baseline)<-[isBaselinedBy:IS_BASELINED_BY]-(workpack:Workpack), " +
         "(proposer:Person)-[proposerRelation:IS_STAKEHOLDER_IN]->(workpack) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN proposerRelation, proposer, workpack")
  Optional<IsStakeholderIn> findBaselineProposer(Long idBaseline);

  @Query("MATCH (m:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(m)=$idWorkpack AND id(b)=$idBaseline " +
         "RETURN count(s)>0")
  boolean workpackHasSnapshot(
    Long idWorkpack,
    Long idBaseline
  );

  @Query("MATCH (m:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack) " +
         "WHERE id(s)=$idWorkpack " +
         "RETURN count(s)>0")
  boolean workpackHasMaster(
    Long idWorkpack
  );

  @Query("MATCH (w:Workpack)-[:IS_IN]->(v:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(w)=$idChild AND id(b)=$idBaseline " +
         "RETURN s")
  Optional<Workpack> findSnapshotOfParentByChildIdAndBaselineId(
    Long idChild,
    Long idBaseline
  );

  @Query("MATCH (m:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(m)=$idMaster AND id(b)=$idBaseline " +
         "RETURN s")
  Optional<Workpack> findSnapshotByMasterIdAndBaselineId(
    Long idMaster,
    Long idBaseline
  );

  @Query("MATCH (m:Workpack)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
         "WHERE id(m)=$idMaster AND id(b)=$idBaseline " +
         "RETURN count(s)>0")
  boolean existsSnapshotByMasterIdAndBaselineId(
    Long idMaster,
    Long idBaseline
  );

  @Query("MATCH (baseline:Baseline)<-[isBaselinedBy:IS_BASELINED_BY]-(workpack:Workpack) " +
         "MATCH (workpack)-[isInstanceBy:IS_INSTANCE_BY]->(instance:WorkpackModel) " +
         "MATCH (baseline)<-[isProposedBy:IS_PROPOSED_BY]-(person:Person) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN baseline, isProposedBy, isBaselinedBy, workpack, person, isInstanceBy, instance")
  Optional<Baseline> findBaselineDetailById(Long idBaseline);

  @Query("MATCH (w:Workpack)-[ibb:IS_BASELINED_BY]->(b:Baseline) " +
         "WHERE id(b)=$idBaseline AND id(w)=$idWorkpack " +
         "OPTIONAL MATCH (w)-[ibb2:IS_BASELINED_BY]->(b2:Baseline) " +
         "WITH w, ibb, b, ibb2, b2 " +
         "WHERE b2.activationDate < b.proposalDate " +
         "RETURN w, ibb2, b2 " +
         "ORDER BY b.activationDate DESC " +
         "LIMIT 1")
  Optional<Baseline> findPreviousBaseline(Long idBaseline, Long idWorkpack);

  @Query("MATCH (b:Baseline)<-[c:COMPOSES]-(w:Workpack)-[iso:IS_SNAPSHOT_OF]->(m:Workpack)-[ibb:IS_BASELINED_BY]->(b) " +
         "WHERE id(b)=$idBaseline " +
         "RETURN w, c, b, [ " +
         " [(w)-[iso2:IS_SNAPSHOT_OF]->(m2:Workpack) | [iso2, m2]], " +
         " [(w)-[iso2]-(m2)-[a1:IS_INSTANCE_BY]->(mm:WorkpackModel) | [a1, mm]], " +
         " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
         " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
         " [(w)<-[i:IS_IN*]-(v:Workpack{ deleted: false})<-[h:FEATURES]-(q:Property) | [i,v,h,q]], " +
         " [(v)-[iso3:IS_SNAPSHOT_OF]->(m3:Workpack) | [iso3, m3]], " +
         " [(v)-[iso3]-(m3)-[a2:IS_INSTANCE_BY]->(mm2:WorkpackModel) | [a2, mm2]], " +
         " [(v)-[ii:IS_INSTANCE_BY]->(n:WorkpackModel) | [ii,n]], " +
         " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], " +
         " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], " +
         " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]], " +
         " [(v)-[f5:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f5,l,v1,l1]], " +
         " [(v)-[f6:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f6,o,v2,o1]], " +
         " [(v)-[f7:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f7,u,v3,u1]] " +
         "]")
  Optional<Workpack> findWorkpackProjectSnapshotFromBaseline(Long idBaseline);

  @Query("MATCH (w:Workpack{ deleted: true })<-[:IS_SNAPSHOT_OF]-(s:Workpack) " +
         "WHERE id(s)=$idWorkpack " +
         "RETURN count(w)>0")
  boolean isMasterDeleted(Long idWorkpack);

  @Query(
    "MATCH (master:Workpack) " +
    "WHERE id(master)=$idWorkpack " +
    "OPTIONAL MATCH (master)<-[isIn:IS_IN*]-(deliverable:Deliverable) " +
    "WITH master, isIn, deliverable " +
    "RETURN deliverable, [" +
    "  [(deliverable)-[instanceBy:IS_INSTANCE_BY]->(deliverableModel:WorkpackModel) | [instanceBy, deliverableModel] ]" +
    "]"
  )
  Set<Workpack> findDeliverableWorkpacksOfProjectMaster(Long idWorkpack);

  @Query("MATCH (master:Workpack)  " +
         "WHERE id(master)=$idWorkpack  " +
         "OPTIONAL MATCH (master)<-[isIn:IS_IN*]-(children:Workpack)<-[features:FEATURES]-(schedule:Schedule) " +
         "WITH master, isIn, children, features, schedule " +
         "RETURN children, features, schedule, [ " +
         "  [(children)-[instanceBy:IS_INSTANCE_BY]->(childrenModel:WorkpackModel) | [instanceBy, childrenModel] ] " +
         "]")
  Set<Workpack> findAllWorkpacksWithSchedule(Long idWorkpack);

  @Query("MATCH (baseline:Baseline) " +
         "WHERE id(baseline)=$idBaseline " +
         "RETURN baseline.cancelation")
  boolean isCancelBaseline(Long idBaseline);

  @Query("match (b:Baseline)<-[:IS_BASELINED_BY]-(w:Workpack) " +
         "where id(b)=$baselineId " +
         "return id(w) ")
  Optional<Long> findWorkpackIdByBaselineId(Long baselineId);

}
