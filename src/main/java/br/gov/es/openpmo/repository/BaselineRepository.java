package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineConsumesStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.dto.baselines.TripleConstraintDto;
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
            "WHERE id(w)=$idWorkpack AND (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND id(b)=$idBaseline " +
            "RETURN count(s)>0")
    boolean isSnapshotOfWorkpackComposingBaseline(
            Long idWorkpack,
            Long idBaseline
    );

    @Query("MATCH (a:Workpack)-[:IS_SNAPSHOT_OF]->(w:Workpack{deleted:false})<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->" +
            "(b:Baseline) " +
            "WHERE id(a)=$idWorkpack AND (w.category <> 'SNAPSHOT' OR w.category IS NULL) AND id(b)=$idBaseline " +
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

    @Query("MATCH (m:Workpack {deleted:false}) " +
            "WHERE id(m) = $idWorkpack " +
            "OPTIONAL MATCH (m)<-[:IS_SNAPSHOT_OF]-(s:Workpack)-[:COMPOSES]->(b1:Baseline {active:true}) " +
            "OPTIONAL MATCH (m)-[:IS_IN]->(p:Project)-[:IS_BASELINED_BY]->(b2:Baseline {active:true}) " +
            "RETURN (count(s) > 0 OR count(b2) > 0) AS hasAnyConditionMet")
    boolean workpackHasSnapshotOrProjectWithBaseline(
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

    @Query(
        "MATCH (workpack:Workpack)-[:IS_BASELINED_BY]->(baseline:Baseline) " +
        "WHERE ID(workpack) = $idWorkpack " +
        "RETURN ID(workpack) AS idWorkpack, id(baseline) AS idBaseline " +
        ", baseline.activationDate AS activationDate, baseline.proposalDate AS proposalDate " +
        ", baseline.status AS status, baseline.name AS name, baseline.description AS description " +
        ", baseline.message AS message, baseline.active AS active "
    )
    List<BaselineResultDto> findAllInWorkpackByIdWorkpack(Long idWorkpack);


    @Query(
        "MATCH (children:Workpack{deleted:false})-[:COMPOSES]->(baseline:Baseline) " +
        "WHERE ID(baseline) = $id AND ANY(label IN labels(children) WHERE label IN ['Milestone', 'Deliverable']) " +
        "WITH DISTINCT id(children)  AS ids " +
        "MATCH (snapshot:Workpack)-[:IS_SNAPSHOT_OF]->(master:Workpack)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) " +
        "WHERE ID(snapshot) IN [ids] " +
        "RETURN ID(master) AS idMaster, ID(snapshot) AS id, snapshot.name AS name, snapshot.fullName AS fullName " +
        ", snapshot.date AS date, model.fontIcon AS fontIcon, labels(snapshot) AS label"
    )
    List<BaselineWorkpackDto> findAllWorkpacBaselineById(final Long id);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(workpack:Deliverable)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step) " +
        "WHERE ID(workpack) IN $ids " +
        "RETURN ID(master) AS idMaster, id(workpack) AS idWorkpack, id(schedule) AS idSchedule , schedule.end AS end " +
        ", schedule.start AS start, ID(step) AS idStep, step.plannedWork AS plannedWork "
    )
    List<BaselineScheduleStep> findAllBaselineScheduleStepById(final List<Long> ids);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(workpack:Deliverable)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step), " +
        "(step)-[consumes:CONSUMES]->(cost:CostAccount) " +
        "WHERE ID(workpack) IN $ids " +
        "RETURN id(master) AS idMaster, id(workpack) AS idWorkpack, id(step) AS idStep, id(consumes) AS idConsumes, consumes.plannedCost AS plannedCost "
    )
    List<BaselineConsumesStep> findAllStepConsumesById(final List<Long> ids);

    @Query(
            "MATCH (children:Workpack{deleted:false})-[:IS_IN*]->(parent:Workpack) " +
                    "WHERE ID(parent) = $id AND ((ANY(label IN labels(children) WHERE label IN ['Deliverable']) " +
                    "AND (children)<-[:FEATURES]-(:Schedule)) OR ANY(label IN labels(children) WHERE label IN ['Milestone']) ) " +
                    "WITH DISTINCT ID(children)  AS ids " +
                    "MATCH (master:Workpack)-[instanceBy:IS_INSTANCE_BY]->(model:WorkpackModel) " +
                    "WHERE ID(master) IN [ids] " +
                    "RETURN ID(master) AS idMaster, ID(master) AS id, master.name AS name, master.fullName AS fullName " +
                    ", master.date AS date, model.fontIcon AS fontIcon, labels(master) AS label "
    )
    List<BaselineWorkpackDto> findAllWorkpacMasterById(final Long id);

    @Query(
        "MATCH (workpack:Deliverable)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step) " +
            "WHERE ID(workpack) IN $ids " +
            "RETURN ID(workpack) AS idMaster, id(workpack) AS idWorkpack, id(schedule) AS idSchedule " +
            ", schedule.end AS end, schedule.start AS start, id(step) AS idStep, step.plannedWork AS plannedWork "
    )
    List<BaselineScheduleStep> findAllScheduleStepMasterById(final List<Long> ids);

    @Query(
        "MATCH (workpack:Deliverable)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step), " +
            "(step)-[consumes:CONSUMES]->(cost:CostAccount) " +
            "WHERE ID(workpack) IN $ids " +
            "RETURN ID(workpack) AS idMaster, ID(workpack) AS idWorkpack, ID(step) AS idStep, ID(consumes) AS idConsumes, consumes.plannedCost AS plannedCost "
    )
    List<BaselineConsumesStep> findAllStepConsumesMasterById(final List<Long> ids);

    @Query("MATCH (s:Step)-[:COMPOSES]->(schedule:Schedule)-[:FEATURES]->(d:Deliverable) " +
        "WHERE ID(d) IN $ids " +
        "RETURN ID(schedule) AS idSchedule, ID(s) AS idStep, s.actualWork AS actualWork " +
        ", s.plannedWork AS plannedWork, s.periodFromStart AS periodFromStart  ")
    List<BaselineStepSubmitDto> findAllStepByScheduleIds(List<Long> ids);

    @Query("MATCH (s:Step)<-[:IS_SNAPSHOT_OF]-(snapshotStep:Step)-[:COMPOSES]->(snapshot:Schedule)-[:IS_SNAPSHOT_OF]->(schedule:Schedule)-[:FEATURES]->(d:Deliverable) " +
        ",(snapshotStep)-[:COMPOSES]->(b:Baseline)  " +
        "WHERE ID(d) IN $ids AND ID(b) = $baseline " +
        "RETURN ID(schedule) AS idSchedule, ID(s) AS idStep, snapshotStep.actualWork AS actualWork " +
        ", snapshotStep.plannedWork AS plannedWork, snapshotStep.periodFromStart AS periodFromStart  ")
    List<BaselineStepSubmitDto> findAllStepSnapshotByScheduleIds(List<Long> ids, Long baseline);

    @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE id(w) IN $idWorkpack "
        + "RETURN ID(s) AS idSchedule, ID(w) AS idWorkpack, s.end AS end, s.start AS start ")
    List<BaselineScheduleSubmitDto> findAllByWorkpackId(List<Long> idWorkpack);

    @Query("MATCH (b:Baseline)<-[:COMPOSES]-(snapshot:Schedule)-[:IS_SNAPSHOT_OF]->(master:Schedule)-[:FEATURES]->(w:Workpack) " +
        "WHERE ID(w) IN $idWorkpack AND ID(b) = $baseline " +
        "RETURN ID(master) AS idSchedule, ID(w) AS idWorkpack, snapshot.end AS end, snapshot.start AS start ")
    List<BaselineScheduleSubmitDto> findAllSnapshotByWorkpackId(List<Long> idWorkpack, Long baseline);

    @Query("MATCH (c:CostAccount)<-[co:CONSUMES]-(s:Step)-[cp:COMPOSES]->(sc:Schedule)-[:FEATURES]->(d:Deliverable) " +
        "WHERE id(d) IN $ids " +
        "RETURN ID(s) AS idStep, ID(c) AS idCostAccount, co.actualCost AS actualCost, co.plannedCost AS plannedCost "
    )
    List<BaselineConsumesStepSubmitDto> findAllByScheduleId(List<Long> ids);

    @Query(
        "MATCH (master:Step)-[cp:COMPOSES]->(sc:Schedule)-[:FEATURES]->(d:Deliverable) " +
            ", (master)<-[:IS_SNAPSHOT_OF]-(snapshot:Step)-[:COMPOSES]->(baseline:Baseline) " +
            ", (snapshot)-[consumeSnapshot:CONSUMES]->(:CostAccount)-[:IS_SNAPSHOT_OF]->(c:CostAccount) " +
            "WHERE ID(d) IN $ids AND ID(baseline) = $baseline " +
            "RETURN ID(master) AS idStep, ID(c) AS idCostAccount, consumeSnapshot.actualCost AS actualCost, consumeSnapshot.plannedCost AS plannedCost "
    )
    List<BaselineConsumesStepSubmitDto> findAllSnapshotByScheduleId(List<Long> ids, Long baseline);

    @Query(
        "MATCH (model:WorkpackModel)<-[:IS_INSTANCE_BY]-(master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
        "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(snapshot) WHERE label IN ['Milestone', 'Deliverable']) " +
        "RETURN ID(master) AS idWorkpack, master.name AS name, master.fullName AS fullName " +
        ", model.fontIcon AS fontIcon, labels(master) AS labels, snapshot.date AS date, snapshot.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraintSnapshot(Long idBaseline);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
        ", (snapshot)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step)-[:COMPOSES]-(baseline) " +
        "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(snapshot) WHERE label IN ['Milestone', 'Deliverable']) " +
        "RETURN ID(master) AS idWorkpack, schedule.start AS start, schedule.end AS end " +
        ", toString(SUM(toFloat(step.plannedWork))) AS sumPlannedWork, step.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraintSnapshotScheduleAndPlannedWork(Long idBaseline);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
            ", (snapshot)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step)-[consume:CONSUMES]->(cc:CostAccount)-[:COMPOSES]-(baseline) " +
            "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(snapshot) WHERE label IN ['Milestone', 'Deliverable']) " +
            "RETURN ID(master) AS idWorkpack, toString(SUM(toFloat(consume.plannedCost))) AS sumPlannedCost, step.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraintSnapshotScheduleAndPlannedCost(Long idBaseline);


    @Query(
        "MATCH (w:Workpack{deleted:false})-[ibb:IS_BASELINED_BY]->(b:Baseline) " +
        "WHERE id(b)=$idBaseline " +
        "RETURN w "
    )
    Optional<Workpack> findWorkpackByBaselineIdThin(Long idBaseline);

    @Query(
        "MATCH (model:WorkpackModel)<-[:IS_INSTANCE_BY]-(master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
            "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(master) WHERE label IN ['Milestone', 'Deliverable']) " +
            "RETURN ID(master) AS idWorkpack, master.name AS name, master.fullName AS fullName " +
            ", model.fontIcon AS fontIcon, labels(master) AS labels, master.date AS date, master.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraint(Long idBaseline);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
            ", (master)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step)<-[:IS_SNAPSHOT_OF]-(st:Step)-[:COMPOSES]-(baseline) " +
            "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(master) WHERE label IN ['Milestone', 'Deliverable']) " +
            "RETURN ID(master) AS idWorkpack, schedule.start AS start, schedule.end AS end " +
            ", toString(SUM(toFloat(step.plannedWork))) AS sumPlannedWork, step.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraintScheduleAndPlannedWork(Long idBaseline);

    @Query(
        "MATCH (master:Workpack)<-[:IS_SNAPSHOT_OF]-(snapshot:Workpack)-[:COMPOSES]->(baseline:Baseline) " +
            ", (master)<-[:FEATURES]-(schedule:Schedule)<-[:COMPOSES]-(step:Step)-[consume:CONSUMES]->(cc:CostAccount)<-[:IS_SNAPSHOT_OF]-(sc:CostAccount)-[:COMPOSES]-(baseline) " +
            "WHERE ID(baseline) = $idBaseline AND ANY(label IN labels(master) WHERE label IN ['Milestone', 'Deliverable']) " +
            "RETURN ID(master) AS idWorkpack, toString(SUM(toFloat(consume.plannedCost))) AS sumPlannedCost, step.category AS category "
    )
    List<TripleConstraintDto> findAllTripleConstraintScheduleAndPlannedCost(Long idBaseline);

    @Query(
        "MATCH (workpack:Workpack)<-[:FEATURES]-(:UnitSelection)-[:VALUES]->(measure:UnitMeasure)  " +
        "WHERE id(workpack) IN $ids " +
        "RETURN DISTINCT ID(workpack) AS id, measure.name AS name ")
    List<EntityDto> findUnitMeasureNameOfDeliverableWorkpack(List<Long> ids);

    @Query(
            "MATCH (b:Baseline) WHERE id(b) = $baselineId " +
                    "SET b.status = $status " +
                    "RETURN b "
    )
    void setStatusBaseline(Long baselineId, String status);
}
