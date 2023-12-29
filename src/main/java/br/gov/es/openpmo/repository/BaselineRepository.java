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
            "OPTIONAL MATCH (w)<-[ii:IS_IN*]-(v:Workpack{deleted:false}) " +
            "OPTIONAL MATCH (w)<-[f1:FEATURES]-(p:Property) " +
            "OPTIONAL MATCH (w)<-[f2:FEATURES]-(l:LocalitySelection)-[v1:VALUES]->(l1:Locality) " +
            "OPTIONAL MATCH (w)<-[f3:FEATURES]-(o:OrganizationSelection)-[v2:VALUES]->(o1:Organization) " +
            "OPTIONAL MATCH (w)<-[f4:FEATURES]-(u:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure) " +
            "RETURN w, [ " +
            " [[ii,v]], " +
            " [[f1,p]], " +
            " [[f2,l,v1,l1]], " +
            " [[f3,o,v2,o1]], " +
            " [[f4,u,v3,u1]] " +
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
            "OPTIONAL MATCH (s)-[i1:IS_INSTANCE_BY]->(m1:WorkpackModel) " +
            "OPTIONAL MATCH (s)<-[f1:FEATURES]-(p1:Property) " +
            "OPTIONAL MATCH (s)<-[fa:FEATURES]-(s1:Schedule) " +
            "OPTIONAL MATCH (s1)<-[c1:COMPOSES]-(st1:Step) " +
            "OPTIONAL MATCH (st1)-[c2:CONSUMES]->(ca1:CostAccount) " +
            "OPTIONAL MATCH (s)<-[i2:IS_IN*]-(r:Workpack)-[:COMPOSES]->(b) " +
            "OPTIONAL MATCH (r)-[d:COMPOSES]->(b) " +
            "OPTIONAL MATCH (r)-[g2:IS_SNAPSHOT_OF]->(t:Workpack) " +
            "OPTIONAL MATCH (r)-[i3:IS_INSTANCE_BY]->(m2:WorkpackModel) " +
            "OPTIONAL MATCH (r)<-[f2:FEATURES]-(p2:Property) " +
            "OPTIONAL MATCH (r)<-[fb:FEATURES]-(s2:Schedule) " +
            "OPTIONAL MATCH (s2)<-[c2:COMPOSES]-(st2:Step) " +
            "OPTIONAL MATCH (st2)-[c3:CONSUMES]->(ca2:CostAccount) " +
            "RETURN s,c,b, [ " +
            " [[i1,m1]], " +
            " [[f1,p1]], " +
            " [[fa,s1]], " +
            " [[c1,st1]], " +
            " [[c2,ca1]], " +
            " [[i2,r]], " +
            " [[d]], " +
            " [[g2,t]], " +
            " [[i3,m2]], " +
            " [[f2,p2]], " +
            " [[fb,s2]], " +
            " [[c2,st2]], " +
            " [[c3,ca2]] " +
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

    @Query(" MATCH (w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
        " , (w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) " +
        " WHERE id(b)=$idBaseline " +
        " OPTIONAL MATCH (w)<-[i:IS_IN*0..]-(v:Workpack{deleted:false}) " +
        " OPTIONAL MATCH (v)<-[f1:FEATURES]-(p:Property) " +
        " OPTIONAL MATCH (p)-[v1:VALUES]->(l1:Locality) " +
        " OPTIONAL MATCH (p)-[v2:VALUES]->(o1:Organization) " +
        " OPTIONAL MATCH (p)-[v3:VALUES]->(u1:UnitMeasure) " +
        " RETURN w, [   " +
        "  [a,m], " +
        "  [i,v], " +
        "  [f1,p], " +
        "  [v1,l1], " +
        "  [v2,o1],   " +
        "  [v3,u1] " +
        " ] ")
    Optional<Workpack> findNotDeletedWorkpackWithPropertiesAndModelAndChildrenByBaselineId(Long idBaseline);

    @Query(" MATCH (w:Workpack{deleted:false})-[a:IS_INSTANCE_BY]->(m:WorkpackModel) " +
        " WHERE id(w)=$idWorkpack " +
        " OPTIONAL MATCH (w)<-[i:IS_IN*0..]-(v:Workpack{deleted:false}) " +
        " OPTIONAL MATCH (v)-[viib:IS_INSTANCE_BY]->(vm:WorkpackModel) " +
        " OPTIONAL MATCH (v)<-[f1:FEATURES]-(p:Property) " +
        " OPTIONAL MATCH (p)-[v1:VALUES]->(l1:Locality) " +
        " OPTIONAL MATCH (p)-[v2:VALUES]->(o1:Organization) " +
        " OPTIONAL MATCH (p)-[v3:VALUES]->(u1:UnitMeasure) " +
        " RETURN w, [   " +
        "  [a,m], " +
        "  [i,v], " +
        "  [viib,vm], " +
        "  [f1,p], " +
        "  [v1,l1], " +
        "  [v2,o1],   " +
        "  [v3,u1] " +
        " ] ")
    Optional<Workpack> findNotDeletedWorkpackWithPropertiesAndModelAndChildrenByWorkpackId(Long idWorkpack);


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

    @Query(" MATCH (b:Baseline)<-[c:COMPOSES]-(w:Workpack)-[iso:IS_SNAPSHOT_OF]->(mw:Workpack) " +
        " -[ibb:IS_BASELINED_BY]->(b)  " +
        " WHERE id(b)=$idBaseline  " +
        " OPTIONAL MATCH (w)-[iso2:IS_SNAPSHOT_OF]->(m2:Workpack)  " +
        " OPTIONAL MATCH (m2)-[a1:IS_INSTANCE_BY]->(mm:WorkpackModel)  " +
        " OPTIONAL MATCH (w)<-[f1:FEATURES]-(p:Property)  " +
        " OPTIONAL MATCH (w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel)  " +
        " OPTIONAL MATCH (w)<-[i:IS_IN*]-(v:Workpack)<-[h:FEATURES]-(q:Property)  " +
        " OPTIONAL MATCH (n:WorkpackModel)<-[ii:IS_INSTANCE_BY]-(v)-[iso3:IS_SNAPSHOT_OF]->(m3:Workpack)-[a2:IS_INSTANCE_BY]->(mm2:WorkpackModel)  " +
        " OPTIONAL MATCH (w)<-[f2:FEATURES]-(ls1:LocalitySelection)-[v1:VALUES]->(l1:Locality)  " +
        " OPTIONAL MATCH (w)<-[f3:FEATURES]-(os1:OrganizationSelection)-[v2:VALUES]->(o1:Organization)  " +
        " OPTIONAL MATCH (w)<-[f4:FEATURES]-(us1:UnitSelection)-[v3:VALUES]->(u1:UnitMeasure)  " +
        " OPTIONAL MATCH (v)-[f5:FEATURES]-(ls2:LocalitySelection)-[v4:VALUES]->(l2:Locality)  " +
        " OPTIONAL MATCH (v)-[f6:FEATURES]-(os2:OrganizationSelection)-[v5:VALUES]->(o2:Organization)  " +
        " OPTIONAL MATCH (v)-[f7:FEATURES]-(us2:UnitSelection)-[v6:VALUES]->(u2:UnitMeasure) " +
        " RETURN w, c, b, [  " +
        "  [iso2, m2],  " +
        "  [a1, mm],  " +
        "  [f1,p],  " +
        "  [a,m],  " +
        "  [i,v,h,q],  " +
        "  [iso3, m3, a2, mm2, ii, n],  " +
        "  [f2,ls1,v1,l1],  " +
        "  [f3,os1,v2,o1],  " +
        "  [f4,us1,v3,u1],  " +
        "  [f5,ls2,v4,l2],  " +
        "  [f6,os2,v5,o2],  " +
        "  [f7,us2,v6,u2]  " +
        " ] ")
    Optional<Workpack> findWorkpackProjectSnapshotFromBaseline(Long idBaseline);

    @Query("MATCH (w:Workpack{deleted:true})<-[:IS_SNAPSHOT_OF]-(s:Workpack) " +
            "WHERE id(s)=$idWorkpack " +
            "RETURN count(w)>0")
    boolean isMasterDeleted(Long idWorkpack);

    @Query("MATCH (master:Workpack{deleted:false}) " +
            "WHERE id(master)=$idWorkpack " +
            "OPTIONAL MATCH (master)<-[isIn:IS_IN*]-(deliverable:Deliverable{deleted:false}) " +
            "WITH master, isIn, deliverable " +
            "OPTIONAL MATCH (deliverable)-[instanceBy:IS_INSTANCE_BY]->(deliverableModel:WorkpackModel) " +
            "RETURN deliverable, [" +
            "  [ [instanceBy, deliverableModel] ]" +
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

    @Query("MATCH (b:Baseline) WHERE id(b)=$baselineId " +
            "MATCH (s:Workpack) WHERE id(s)=$snapshotId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithWorkpack(
            Long baselineId,
            Long snapshotId
    );

    @Query("MATCH (b:Baseline) WHERE id(b)=$baselineId " +
            "MATCH (w:Workpack{deleted:false}) WHERE id(w)=$workpackId " +
            "CREATE (b)<-[:IS_BASELINED_BY]-(w)")
    void createIsBaselinedByRelationship(
            @Param("baselineId") Long baselineId,
            @Param("workpackId") Long workpackId
    );

    @Query("MATCH (b:Baseline) WHERE id(b)=$baselineId  " +
            "MATCH (s:Property) WHERE id(s)=$propertyId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithProperty(
            Long baselineId,
            Long propertyId
    );

    @Query("MATCH (baseline:Baseline)<-[:IS_BASELINED_BY]-(:Project{deleted:false})<-[:IS_SNAPSHOT_OF]-(project:Project) " +
            "WHERE id(baseline)=$idBaseline " +
            "OPTIONAL MATCH (project)<-[:IS_IN*]-(:Deliverable{deleted:false})<-[:FEATURES]-(schedule:Schedule)-[:COMPOSES]->" +
            "(baseline) " +
            "OPTIONAL MATCH (project)<-[:IS_IN*]-(:Milestone{deleted:false})<-[:FEATURES]-(date:Date)-[:COMPOSES]->(baseline) " +
            "WITH baseline, project, schedule, " +
            "    collect(DISTINCT datetime(schedule.end)) AS scheduleEndDates, " +
            "    collect(DISTINCT datetime(schedule.start)) AS scheduleStartDates, " +
            "    collect(DISTINCT datetime(date.value)) AS dates " +
            "UNWIND (scheduleStartDates+dates) AS startDates " +
            "UNWIND (scheduleEndDates) AS unwindScheduleEndDates " +
            "RETURN " +
            "    min(startDates) AS initialDate, " +
            "    max(unwindScheduleEndDates) AS endDate")
    Optional<DateIntervalQuery> findScheduleIntervalInSnapshotsOfBaseline(Long idBaseline);

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w), " +
            "(w)-[iib:IS_INSTANCE_BY]->(model:WorkpackModel), " +
            "(model)<-[f1:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[idb:IS_DRIVEN_BY]-" +
            "(nameProperty:Property)-[f2:FEATURES]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='APPROVED' " +
            "AND ($term IS NULL OR $term = '' OR nameScore > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c, iib, model, f1, f2, nameModel, idb  " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllApprovedByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w), " +
            "(w)-[iib:IS_INSTANCE_BY]->(model:WorkpackModel), " +
            "(model)<-[f1:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[idb:IS_DRIVEN_BY]-" +
            "(nameProperty:Property)-[f2:FEATURES]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='REJECTED' " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c, iib, model, f1, f2, nameModel, idb " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllRejectedByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w), " +
            "(w)-[iib:IS_INSTANCE_BY]->(model:WorkpackModel), " +
            "(model)<-[f1:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[idb:IS_DRIVEN_BY]-" +
            "(nameProperty:Property)-[f2:FEATURES]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='PROPOSED' AND NOT (b)-[:IS_EVALUATED_BY]->(p) " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c, iib, model, f1, f2, nameModel, idb " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllWaitingMyEvaluationByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

    @Query("MATCH (w:Workpack)-[ii:IS_BASELINED_BY]->(b:Baseline), " +
            "(p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w)," +
            "(w)-[iib:IS_INSTANCE_BY]->(model:WorkpackModel), " +
            "(model)<-[f1:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[idb:IS_DRIVEN_BY]-" +
            "(nameProperty:Property)-[f2:FEATURES]->(w) " +
            "WITH *, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(b.description), apoc.text.clean($term)) AS descriptionScore " +
            "WITH *, CASE WHEN nameScore > descriptionScore THEN nameScore ELSE descriptionScore END AS score " +
            "WHERE id(p)=$personId AND b.status='PROPOSED' AND (b)-[:IS_EVALUATED_BY]->(p) " +
            "AND ($term IS NULL OR $term = '' OR score > $searchCutOffScore) " +
            "RETURN b, w, ii, p, c, iib, model, f1, f2, nameModel, idb " +
            "ORDER BY b.proposalDate")
    List<Baseline> findAllWaitingOthersEvaluationByPersonId(
            Long personId,
            String term,
            Double searchCutOffScore
    );

}
