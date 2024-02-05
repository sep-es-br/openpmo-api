package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BaselineRepository extends Neo4jRepository<Baseline, Long>, CustomRepository {

    @Query("MATCH (w:Workpack{deleted:false})-[ibb:IS_BASELINED_BY]->(b:Baseline) " +
            "WHERE id(b)=$idBaseline " +
            "RETURN w, [ " +
            " [(w)<-[ii:IS_IN*]-(v:Workpack{deleted:false}) | [ii,v]], " +
            " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
            " [(w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) | [f2,l,v1,l1]], " +
            " [(w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) | [f3,o,v2,o1]], " +
            " [(w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) | [f4,u,v3,u1]] " +
            "]")
    Optional<Workpack> findWorkpackByBaselineId(Long idBaseline);

    @Query("MATCH (w:Workpack{deleted:false})-[s:IS_BASELINED_BY]->(b:Baseline{cancelation: true, status: 'PROPOSED'}) " +
            "WHERE id(w)=$idWorkpack " +
            "RETURN count(b)>0")
    boolean workpackHasCancelationProposal(Long idWorkpack);

    @Query("MATCH (w:Workpack{deleted:false})-[s:IS_BASELINED_BY]->(b:Baseline{status: 'PROPOSED'}) " +
            "WHERE id(w)=$idWorkpack " +
            "RETURN count(b)>0")
    boolean workpackHasPendingBaselines(Long idWorkpack);

    @Query("MATCH (workpack:Workpack{deleted:false})-[:IS_BASELINED_BY]->(baseline:Baseline{active: true}) " +
            "WHERE id(workpack)=$idWorkpack " +
            "RETURN count(baseline)>0")
    boolean workpackHasActiveBaseline(Long idWorkpack);

    @Query("MATCH (workpack:Workpack{deleted:false})-[:IS_BASELINED_BY]->(baseline:Baseline{active: true,cancelation:false}) " +
            "WHERE id(workpack)=$idWorkpack " +
            "RETURN baseline")
    Optional<Baseline> findActiveBaseline(Long idWorkpack);

    @Query("MATCH (w:Workpack{deleted:false}) " +
            "WHERE id(w)=$workpackId " +
            "OPTIONAL MATCH (w)<-[:IS_IN*]-(p1:Project{deleted:false})-[:IS_BASELINED_BY]->(b1:Baseline{active:true," +
            "cancelation:false}) " +
            "WITH w,b1 " +
            "OPTIONAL MATCH (w)-[:IS_IN*]->(p2:Project{deleted:false})-[:IS_BASELINED_BY]->(b2:Baseline{active:true," +
            "cancelation:false}), " +
            "   (w)<-[:IS_SNAPSHOT_OF]-(:Workpack)-[:COMPOSES]->(b2) " +
            "WITH b1,b2 " +
            "RETURN b1,b2")
    List<Baseline> findAllActiveBaselines(Long workpackId);

    @Query("MATCH (b:Baseline)<-[i:IS_BASELINED_BY]-(w:Workpack) " +
            "WHERE id(w)=$idWorkpack " +
            "RETURN b,i,w")
    List<Baseline> findAllByWorkpackId(Long idWorkpack);

    @Query("MATCH (w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(w)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean isSnapshotOfWorkpackComposingBaseline(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (a:Workpack)-[:IS_SNAPSHOT_OF]->(w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->" +
            "(b:Baseline) " +
            "WHERE id(a)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean isSnapshotOfMasterComposingBaseline(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (b:Baseline) " +
            "WHERE id(b)=$idBaseline " +
            "MATCH (s:Workpack) " +
            "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack) " +
            "OPTIONAL MATCH (b)<-[:COMPOSES]-(s)-[:IS_IN*]->(sp:Workpack)-[:COMPOSES]->(b) " +
            "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack) " +
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
            "MATCH (s:Workpack) " +
            "OPTIONAL MATCH (s)-[:IS_IN*]->(sp:Workpack) " +
            "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(o:Workpack)-[:COMPOSES]->(b) " +
            "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(n:Workpack)-[:COMPOSES]->(b), " +
            "(n)-[:IS_IN*]->(o) " +
            "WITH s,m,sp,mp,n,b,o " +
            "WHERE id(s)=$idChild " +
            "WITH m, " +
            "     count(DISTINCT s) + count(DISTINCT sp) AS snapshots, " +
            "     count(DISTINCT o) + count(DISTINCT n) AS others " +
            "RETURN snapshots <> others AND count(m)>0")
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

    @Query(
            "MATCH (m:Workpack)<-[i:IS_SNAPSHOT_OF]-(s:Workpack) " +
                    "MATCH (m)-[iib:IS_INSTANCE_BY]->(wm:WorkpackModel) " +
                    "WHERE id(s)=$idSnapshot " +
                    "RETURN m, iib, wm"
    )
    Optional<Workpack> findMasterBySnapshotId(Long idSnapshot);

    @Query("MATCH (w:Workpack{deleted:true})<-[:IS_SNAPSHOT_OF]-(:Workpack)-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(w)=$idWorkpack AND id(b)=$idBaseline " +
            "RETURN count(w)>0")
    boolean isWorkpackDeletedAndHasSnapshot(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
            "WHERE id(b)=$idBaseline " +
            "RETURN w, [ " +
            " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
            " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
            " [(w)<-[i:IS_IN*]-(v:Workpack{deleted:false}) | [i,v]], " +
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

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(m)=$idWorkpack AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean workpackHasSnapshot(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack) " +
            "WHERE id(s)=$idWorkpack " +
            "RETURN count(s)>0")
    boolean workpackHasMaster(
            Long idWorkpack
    );

    @Query("MATCH (w:Workpack{deleted:false})-[:IS_IN]->(v:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->" +
            "(b:Baseline) " +
            "WHERE id(w)=$idChild AND id(b)=$idBaseline " +
            "RETURN s")
    Optional<Workpack> findSnapshotOfParentByChildIdAndBaselineId(
            Long idChild,
            Long idBaseline
    );

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(m)=$idMaster AND id(b)=$idBaseline " +
            "RETURN s")
    Optional<Workpack> findSnapshotByMasterIdAndBaselineId(
            Long idMaster,
            Long idBaseline
    );

    @Query("MATCH (baseline:Baseline)<-[isBaselinedBy:IS_BASELINED_BY]-(workpack:Workpack{deleted:false}) " +
            "MATCH (workpack)-[isInstanceBy:IS_INSTANCE_BY]->(instance:WorkpackModel) " +
            "MATCH (baseline)<-[isProposedBy:IS_PROPOSED_BY]-(person:Person) " +
            "WHERE id(baseline)=$idBaseline " +
            "RETURN baseline, isProposedBy, isBaselinedBy, workpack, person, isInstanceBy, instance")
    Optional<Baseline> findBaselineDetailById(Long idBaseline);

    @Query("MATCH (w:Workpack{deleted:false})-[ibb:IS_BASELINED_BY]->(b:Baseline) " +
            "WHERE id(b)=$idBaseline AND id(w)=$idWorkpack " +
            "OPTIONAL MATCH (w)-[ibb2:IS_BASELINED_BY]->(b2:Baseline) " +
            "WITH w, ibb, b, ibb2, b2 " +
            "WHERE b2.activationDate < b.proposalDate " +
            "RETURN w, ibb2, b2 " +
            "ORDER BY b2.activationDate DESC " +
            "LIMIT 1")
    Optional<Baseline> findPreviousBaseline(
            Long idBaseline,
            Long idWorkpack
    );

    @Query("MATCH (b:Baseline)<-[c:COMPOSES]-(w:Workpack)-[iso:IS_SNAPSHOT_OF]->(m:Workpack)" +
            "-[ibb:IS_BASELINED_BY]->(b) " +
            "WHERE id(b)=$idBaseline " +
            "RETURN w, c, b, [ " +
            " [(w)-[iso2:IS_SNAPSHOT_OF]->(m2:Workpack) | [iso2, m2]], " +
            " [(w)-[iso2]-(m2)-[a1:IS_INSTANCE_BY]->(mm:WorkpackModel) | [a1, mm]], " +
            " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
            " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
            " [(w)<-[i:IS_IN*]-(v:Workpack)<-[h:FEATURES]-(q:Property) | [i,v,h,q]], " +
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

    @Query("MATCH (w:Workpack{deleted:true})<-[:IS_SNAPSHOT_OF]-(s:Workpack) " +
            "WHERE id(s)=$idWorkpack " +
            "RETURN count(w)>0")
    boolean isMasterDeleted(Long idWorkpack);

    @Query("MATCH (master:Workpack{deleted:false}) " +
            "WHERE id(master)=$idWorkpack " +
            "OPTIONAL MATCH (master)<-[isIn:IS_IN*]-(deliverable:Deliverable{deleted:false}) " +
            "WITH master, isIn, deliverable " +
            "RETURN deliverable, [" +
            "  [(deliverable)-[instanceBy:IS_INSTANCE_BY]->(deliverableModel:WorkpackModel) | [instanceBy, deliverableModel] ]" +
            "]")
    Set<Workpack> findDeliverableWorkpacksOfProjectMaster(Long idWorkpack);

    @Query("MATCH (baseline:Baseline) " +
            "WHERE id(baseline)=$idBaseline " +
            "RETURN baseline.cancelation")
    boolean isCancelBaseline(Long idBaseline);

    @Query("MATCH (b:Baseline)<-[:IS_BASELINED_BY]-(w:Workpack) " +
            "WHERE id(b)=$baselineId " +
            "RETURN id(w) ")
    Optional<Long> findWorkpackIdByBaselineId(Long baselineId);

    @Query("MATCH (w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
            "WHERE id(b)=$baselineId " +
            "OPTIONAL MATCH (w)<-[:IS_IN*]->(v:Workpack{deleted:false}) " +
            "WITH collect(w)+collect(v) AS workpackList " +
            "UNWIND workpackList AS workpacks " +
            "RETURN DISTINCT id(workpacks)")
    List<Long> findWorkpacksIdByBaselineId(Long baselineId);

    @Query("MATCH (w:Workpack{deleted:false}) " +
            "WHERE id(w)=$workpackId " +
            "OPTIONAL MATCH (w)-[:IS_BASELINED_BY]->(bDirect:Baseline) " +
            "WHERE bDirect.active=true OR bDirect.status IN ['APPROVED', 'PROPOSED'] " +
            "WITH w,bDirect " +
            "OPTIONAL MATCH (w)-[:IS_IN*]->(v:Workpack{deleted:false})-[:IS_BASELINED_BY]->(bUp:Baseline)<-[:COMPOSES]-" +
            "(:Workpack{deleted:false})-[:IS_SNAPSHOT_OF]->(w) " +
            "WHERE bUp.active=true OR bUp.status IN ['APPROVED', 'PROPOSED'] " +
            "WITH w,bDirect,bUp " +
            "OPTIONAL MATCH (w)<-[:IS_IN*]-(v:Workpack{deleted:false})-[:IS_BASELINED_BY]->(bDown:Baseline) " +
            "WHERE bDown.active=true OR bDown.status IN ['APPROVED', 'PROPOSED'] " +
            "WITH w,bDirect,bUp,bDown " +
            "UNWIND CASE WHEN bDirect is null THEN " +
            "        CASE WHEN bUp is null THEN " +
            "            CASE WHEN bDown is null THEN [] ELSE bDown END " +
            "        ELSE bUp END " +
            "    ELSE bDirect END AS baselines " +
            "RETURN baselines")
    List<Baseline> findApprovedOrProposedBaselinesByAnyWorkpackId(Long workpackId);


    @Query("MATCH (w:Workpack{deleted:false})-[ibw:IS_BASELINED_BY]->(bDirect:Baseline)  " +
        "WHERE id(w) IN $ids AND (bDirect.active=true OR bDirect.status IN ['APPROVED', 'PROPOSED']) " +
        "RETURN w,ibw, bDirect")
    List<Baseline> findApprovedOrProposedBaselinesByWorkpackIds(List<Long> ids);

    @Query("MATCH (b:Baseline), (s:Workpack) " +
            "WHERE id(b)=$baselineId AND id(s)=$snapshotId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithWorkpack(
            Long baselineId,
            Long snapshotId
    );

    @Query("MATCH (b:Baseline), (w:Workpack{deleted:false}) " +
            "WHERE id(b)=$baselineId AND id(w)=$workpackId " +
            "CREATE (b)<-[:IS_BASELINED_BY]-(w)")
    void createIsBaselinedByRelationship(
            @Param("baselineId") Long baselineId,
            @Param("workpackId") Long workpackId
    );

    @Query("MATCH (b:Baseline), (s:Property) " +
            "WHERE id(b)=$baselineId AND id(s)=$propertyId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithProperty(
            Long baselineId,
            Long propertyId
    );

    @Query("MATCH (baseline:Baseline)<-[:IS_BASELINED_BY]-(:Project{deleted:false})<-[:IS_SNAPSHOT_OF]-(project:Project) " +
            "WHERE id(baseline)=$idBaseline " +
            "OPTIONAL MATCH (project)<-[:IS_IN*]-(:Deliverable{deleted:false})<-[:FEATURES]-(schedule:Schedule)-[:COMPOSES]->" +
            "(baseline) " +
            "OPTIONAL MATCH (project)<-[:IS_IN*]-(m:Milestone{deleted:false})-[:COMPOSES]->(baseline) " +
            "WITH baseline, project, schedule, " +
            "    collect(DISTINCT datetime(schedule.end)) AS scheduleEndDates, " +
            "    collect(DISTINCT datetime(schedule.start)) AS scheduleStartDates, " +
            "    collect(DISTINCT datetime(m.date)) AS dates " +
            "UNWIND (scheduleStartDates+dates) AS startDates " +
            "UNWIND (scheduleEndDates) AS unwindScheduleEndDates " +
            "RETURN " +
            "    min(startDates) AS initialDate, " +
            "    max(unwindScheduleEndDates) AS endDate")
    Optional<DateIntervalQuery> findScheduleIntervalInSnapshotsOfBaseline(Long idBaseline);

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='APPROVED' " +
            "AND ($term IS NULL OR $term = '' OR nameScore > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllApprovedByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='REJECTED' " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllRejectedByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='PROPOSED' AND NOT (b)-[:IS_EVALUATED_BY]->(p) " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllWaitingMyEvaluationByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='PROPOSED' AND (b)-[:IS_EVALUATED_BY]->(p) " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllWaitingOthersEvaluationByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

}
