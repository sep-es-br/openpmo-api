package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BaselineRepository extends Neo4jRepository<Baseline, Long> {

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

    @Query("match (w:Workpack{deleted:false}) " +
           "where id(w)=$workpackId " +
           "optional match (w)<-[:IS_IN*]-(p1:Project{deleted:false})-[:IS_BASELINED_BY]->(b1:Baseline{active:true,cancelation:false}) " +
           "with w,b1 " +
           "optional match (w)-[:IS_IN*]->(p2:Project{deleted:false})-[:IS_BASELINED_BY]->(b2:Baseline{active:true,cancelation:false}), " +
           "   (w)<-[:IS_SNAPSHOT_OF]-(:Workpack{deleted:false})-[:COMPOSES]->(b2) " +
           "with b1,b2 " +
           "return b1,b2")
    List<Baseline> findAllActiveBaselines(Long workpackId);

    @Query("MATCH (b:Baseline)<-[i:IS_BASELINED_BY]-(w:Workpack) " +
            "WHERE id(w)=$idWorkpack " +
            "RETURN b,i,w")
    List<Baseline> findAllByWorkpackId(Long idWorkpack);

    @Query("MATCH (w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(w)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean isSnapshotOfWorkpackComposingBaseline(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (a:Workpack{deleted:false})-[:IS_SNAPSHOT_OF]->(w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(a)=$idWorkpack AND w.category <> 'SNAPSHOT' AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean isSnapshotOfMasterComposingBaseline(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (b:Baseline) " +
            "WHERE id(b)=$idBaseline " +
            "MATCH (s:Workpack{deleted:false}) " +
            "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack{deleted:false}) " +
            "OPTIONAL MATCH (b)<-[:COMPOSES]-(s)-[:IS_IN*]->(sp:Workpack{deleted:false})-[:COMPOSES]->(b) " +
            "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack{deleted:false}) " +
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
            "MATCH (s:Workpack{deleted:false}) " +
            "OPTIONAL MATCH (s)-[:IS_IN*]->(sp:Workpack{deleted:false}) " +
            "OPTIONAL MATCH (sp)-[:IS_SNAPSHOT_OF]->(mp:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(o:Workpack{deleted:false})-[:COMPOSES]->(b) " +
            "OPTIONAL MATCH (s)-[:IS_SNAPSHOT_OF]->(m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(n:Workpack{deleted:false})-[:COMPOSES]->(b), (n)-[:IS_IN*]->(o) " +
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

    @Query("MATCH (w:Workpack{deleted:false})<-[g1:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[c:COMPOSES]->(b:Baseline) " +
            "WHERE id(b)=$idBaseline AND id(w)=$idWorkpack " +
            "RETURN s,c,b, [ " +
            " [(s)-[i1:IS_INSTANCE_BY]->(m1:WorkpackModel) | [i1,m1]], " +
            " [(s)<-[f1:FEATURES]-(p1:Property) | [f1,p1]], " +
            " [(s)<-[fa:FEATURES]-(s1:Schedule) | [fa,s1]], " +
            " [(s)<-[fa]-(s1)<-[c1:COMPOSES]-(st1:Step) | [c1,st1]], " +
            " [(s)<-[fa]-(s1)-[c1]-(st1)-[c2:CONSUMES]->(ca1:CostAccount) | [c2,ca1]], " +
            " [(s)<-[i2:IS_IN*]-(r:Workpack{deleted:false})-[:COMPOSES]->(b) | [i2,r]], " +
            " [(s)<-[i2*]-(r)-[d:COMPOSES]->(b) | [d]], " +
            " [(s)<-[i2*]-(r)-[g2:IS_SNAPSHOT_OF]->(t:Workpack{deleted:false}) | [g2,t]], " +
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

    @Query("MATCH (m:Workpack{deleted:false})<-[i:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false}) " +
            "WHERE id(s)=$idSnapshot " +
            "RETURN m")
    Optional<Workpack> findMasterBySnapshotId(Long idSnapshot);

    @Query("MATCH (w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(w)=$idWorkpack AND id(b)=$idBaseline " +
            "RETURN count(w)>0")
    boolean isWorkpackDeletedAndHasSnapshot(Long idWorkpack, Long idBaseline);

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

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(m)=$idWorkpack AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean workpackHasSnapshot(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false}) " +
            "WHERE id(s)=$idWorkpack " +
            "RETURN count(s)>0")
    boolean workpackHasMaster(
            Long idWorkpack
    );

    @Query("MATCH (w:Workpack{deleted:false})-[:IS_IN]->(v:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
            "WHERE id(w)=$idChild AND id(b)=$idBaseline " +
            "RETURN s")
    Optional<Workpack> findSnapshotOfParentByChildIdAndBaselineId(
            Long idChild,
            Long idBaseline
    );

    @Query("MATCH (m:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false})-[:COMPOSES]->(b:Baseline) " +
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
            "ORDER BY b.activationDate DESC " +
            "LIMIT 1")
    Optional<Baseline> findPreviousBaseline(Long idBaseline, Long idWorkpack);

    @Query("MATCH (b:Baseline)<-[c:COMPOSES]-(w:Workpack{deleted:false})-[iso:IS_SNAPSHOT_OF]->(m:Workpack{deleted:false})-[ibb:IS_BASELINED_BY]->(b) " +
            "WHERE id(b)=$idBaseline " +
            "RETURN w, c, b, [ " +
            " [(w)-[iso2:IS_SNAPSHOT_OF]->(m2:Workpack{deleted:false}) | [iso2, m2]], " +
            " [(w)-[iso2]-(m2)-[a1:IS_INSTANCE_BY]->(mm:WorkpackModel) | [a1, mm]], " +
            " [(w)<-[f1:FEATURES]-(p:Property) | [f1,p]], " +
            " [(w)-[a:IS_INSTANCE_BY]->(m:WorkpackModel) | [a,m]], " +
            " [(w)<-[i:IS_IN*]-(v:Workpack{deleted:false})<-[h:FEATURES]-(q:Property) | [i,v,h,q]], " +
            " [(v)-[iso3:IS_SNAPSHOT_OF]->(m3:Workpack{deleted:false}) | [iso3, m3]], " +
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

    @Query("MATCH (w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack{deleted:false}) " +
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

    @Query("match (w:Workpack{deleted:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
            "where id(b)=$baselineId " +
            "optional match (w)<-[:IS_IN*]->(v:Workpack{deleted:false}) " +
            "with collect(w)+collect(v) as workpackList " +
            "unwind workpackList as workpacks " +
            "return distinct id(workpacks)")
    List<Long> findWorkpacksIdByBaselineId(Long baselineId);

    @Query("match (w:Workpack{deleted:false}) " +
            "where id(w)=$workpackId " +
            "optional match (w)-[:IS_BASELINED_BY]->(bDirect:Baseline) " +
            "where bDirect.active=true or bDirect.status in ['APPROVED', 'PROPOSED'] " +
            "with w,bDirect " +
            "optional match (w)-[:IS_IN*]->(v:Workpack{deleted:false})-[:IS_BASELINED_BY]->(bUp:Baseline)<-[:COMPOSES]-(:Workpack{deleted:false})-[:IS_SNAPSHOT_OF]->(w) " +
            "where bUp.active=true or bUp.status in ['APPROVED', 'PROPOSED'] " +
            "with w,bDirect,bUp " +
            "optional match (w)<-[:IS_IN*]-(v:Workpack{deleted:false})-[:IS_BASELINED_BY]->(bDown:Baseline) " +
            "where bDown.active=true or bDown.status in ['APPROVED', 'PROPOSED'] " +
            "with w,bDirect,bUp,bDown " +
            "unwind case bDirect when null then " +
            "        case bUp when null then " +
            "            case bDown when null then [] else bDown end " +
            "        else bUp end " +
            "    else bDirect end as baselines " +
            "return baselines")
    List<Baseline> findApprovedOrProposedBaselinesByAnyWorkpackId(Long workpackId);

    @Query("MATCH (b:Baseline), (s:Workpack{deleted:false}) " +
            "WHERE id(b)=$baselineId AND id(s)=$snapshotId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithWorkpack(Long baselineId, Long snapshotId);

    @Query("MATCH (b:Baseline), (w:Workpack{deleted:false}) " +
            "WHERE id(b)=$baselineId AND id(w)=$workpackId " +
            "CREATE (b)<-[:IS_BASELINED_BY]-(w)")
    void createIsBaselinedByRelationship(Long baselineId, Long workpackId);

    @Query("MATCH (b:Baseline), (s:Property) " +
            "WHERE id(b)=$baselineId AND id(s)=$propertyId " +
            "CREATE (b)<-[:COMPOSES]-(s)")
    void createComposesRelationshipWithProperty(Long baselineId, Long propertyId);

    @Query("MATCH (baseline:Baseline)<-[:IS_BASELINED_BY]-(:Project{deleted:false})<-[:IS_SNAPSHOT_OF]-(project:Project{deleted:false}) " +
            "WHERE id(baseline)=$idBaseline " +
            "OPTIONAL MATCH (project)<-[:IS_IN*]-(:Deliverable{deleted:false})<-[:FEATURES]-(schedule:Schedule)-[:COMPOSES]->(baseline) " +
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

    @Query("match (b:Baseline)<-[:IS_BASELINED_BY]-(p:Project{deleted:false})<-[:IS_SNAPSHOT_OF]-(ps:Project{deleted:false}) " +
            "where id(b) in $baselineIds " +
            "optional match (w:Workpack{deleted:false})<-[:IS_IN*]->(p) " +
            "where id(w)=$workpackId " +
            "with b,p,ps,w " +
            "optional match (d:Deliverable{deleted:false})-[:IS_IN*]->(p) " +
            "where id(d)=$workpackId " +
            "with b,p,ps,w,d " +
            "optional match (m:Milestone{deleted:false})-[:IS_IN*]->(p) " +
            "where id(m)=$workpackId " +
            "with b,p,ps,w,d,m " +
            "optional match (w)<-[:IS_IN*]-(:Deliverable{deleted:false})-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s1:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1 " +
            "optional match (w)<-[:IS_IN*]-(:Milestone{deleted:false})<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d1:Date)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1 " +
            "optional match (d)-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s2:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2 " +
            "optional match (m)<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d2:Date)-[:COMPOSES]->(baseline) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2 " +
            "optional match (p)<-[:IS_IN*]-(:Deliverable{deleted:false})-[:FEATURES]-(:Schedule)<-[:IS_SNAPSHOT_OF]-(s3:Schedule)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2,s3 " +
            "optional match (p)<-[:IS_IN*]-(:Milestone{deleted:false})<-[:FEATURES]-(:Date)<-[:IS_SNAPSHOT_OF]-(d3:Date)-[:COMPOSES]->(b) " +
            "with b,p,ps,w,d,m,s1,d1,s2,d2,s3,d3 " +
            "with " +
            "    case id(w) when $workpackId then collect(distinct datetime(s1.start)) else [] end + " +
            "    case id(d) when $workpackId then collect(distinct datetime(s2.start)) else [] end + " +
            "    case id(p) when $workpackId then collect(distinct datetime(s3.start)) else [] end + " +
            "    case id(w) when $workpackId then collect(distinct datetime(d1.value)) else [] end + " +
            "    case id(m) when $workpackId then collect(distinct datetime(d2.value)) else [] end + " +
            "    case id(p) when $workpackId then collect(distinct datetime(d3.value)) else [] end as startDatesList, " +
            "    case id(w) when $workpackId then collect(distinct datetime(s1.end)) else [] end + " +
            "    case id(d) when $workpackId then collect(distinct datetime(s2.end)) else [] end + " +
            "    case id(p) when $workpackId then collect(distinct datetime(s3.end)) else [] end as endDatesList " +
            "unwind startDatesList as startDates " +
            "unwind endDatesList as endDates " +
            "return min(startDates) as initialDate, max(endDates) as endDate ")
    Optional<DateIntervalQuery> fetchIntervalOfSchedules(Long workpackId, List<Long> baselineIds);

}
