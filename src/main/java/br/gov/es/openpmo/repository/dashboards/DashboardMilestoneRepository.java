package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.workpacks.Milestone;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface DashboardMilestoneRepository extends Neo4jRepository<Milestone, Long> {

  @Query("match (w:Workpack)<-[i:IS_IN*]-(m:Milestone)<-[f:FEATURES]-(d:Date) " +
    "optional match (w)-[ii:IS_IN*0..]-(:Project{deleted:false,canceled:false})-[:IS_BASELINED_BY]->(b:Baseline) " +
    "with * " +
    "where id(w)=$workpackId and ($baselineId is null or id(b)=$baselineId) " +
    "and w.deleted=false and w.canceled=false " +
    "and m.deleted=false and m.canceled=false " +
    "and b.active=true " +
    "OPTIONAL MATCH (m)<-[iso:IS_SNAPSHOT_OF]-(ms:Milestone)-[c:COMPOSES]->(b) " +
    "OPTIONAL MATCH (d)<-[iso2:IS_SNAPSHOT_OF]-(d3:Date)-[f3:FEATURES]->(ms2:Milestone)-[c2:COMPOSES]->(b) " +
    "return m, f, d, [ " +
    "    [[iso,ms,c]], " +
    "    [[iso2,d3,f3,ms2,c2]] " +
    "]")
  List<Milestone> findByParentId(Long workpackId, Long baselineId);

  @Query("match (w:Workpack)<-[i:IS_IN*]-(m:Milestone)<-[f:FEATURES]-(d:Date) " +
    "where id(w)=$workpackId " +
    "and w.deleted=false and w.canceled=false " +
    "and m.deleted=false and m.canceled=false " +
    "OPTIONAL MATCH (m)<-[iso:IS_SNAPSHOT_OF]-(ms:Milestone)-[c:COMPOSES]->(b) " +
    "OPTIONAL MATCH (d)<-[iso2:IS_SNAPSHOT_OF]-(d3:Date)-[c2:COMPOSES]->(b2) " +
    "return m, f, d, [ " +
    "    [[iso,ms,c,b]], " +
    "    [[iso2,d3,c2,b2]] " +
    "]")
  List<Milestone> findByParentId(Long workpackId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN]->(w:Workpack{deleted:false,canceled:false}) " +
         "where id(m)=$milestoneId " +
         "return id(w)")
  Long findParentIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(w) " +
         "limit 1")
  Long findWorkpackIdByMilestoneId(Long milestoneId);

  @Query("match (m:Milestone{deleted:false,canceled:false})-[:IS_IN*]->(w:Workpack{deleted:false,canceled:false})" +
         "-[:IS_BASELINED_BY]->(b:Baseline{active: true}) " +
         "where id(m)=$milestoneId " +
         "return id(b) " +
         "limit 1")
  Long findBaselineIdByMilestoneId(Long milestoneId);

  @Query("  MATCH     " +
       "      (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false}),     " +
       "      (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m)     " +
       "  WHERE id(w)=$workpackId and (m.completed is null or m.completed=false)   " +
       "  " +
       "  OPTIONAL MATCH (d)<-[:IS_SNAPSHOT_OF]-(sd:Date)-[:COMPOSES]->(:Baseline{active:true})   " +
       "  WITH m,d,sd    " +
       "  WHERE ((sd is null) and (date(datetime(d.value)) < date($refDate)))   " +
       "     OR ((sd is not null)  " +
       " 		and (  " +
       " 			(date(datetime(d.value)) <= date(datetime(sd.value))) " +
       " 			and " +
       " 			(date($refDate) <= date(datetime(sd.value))) " +
       " 		) " +
       " 	) " +
       "  RETURN distinct id(m) ")
  Set<Long> onTime(
    Long workpackId,
    LocalDate refDate
  );

  @Query(" MATCH   " +
       " (b:Baseline)<-[:COMPOSES]-(sm:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->  " +
       " (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false}),  " +
       " (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m),   " +
       " (b)<-[:COMPOSES]-(sd:Date)-[:IS_SNAPSHOT_OF]->(d)  " +
       " WHERE   " +
       "  id(w)=$workpackId and id(b)=$baselineId   " +
       " AND   " +
       "  (m.completed is null or m.completed=false) " +
       " AND  " +
       " (	((sd is null) and date(datetime(d.value)) < date($refDate)) " +
       " 	OR  " +
       " 	((sd is not null)  " +
       " 	  AND  " +
       " 	  ( " +
       " 	    (date(datetime(d.value)) <= date(datetime(sd.value))) and " +
       " 		(date($refDate) <= date(datetime(sd.value))) " +
       " 	  ) " +
       " 	) " +
       " ) " +
       " RETURN distinct id(m) ")
  Set<Long> onTime(
    Long baselineId,
    Long workpackId,
    LocalDate refDate
  );

  @Query("  MATCH     " +
       "      (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false}),     " +
       "      (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m)     " +
       "  WHERE id(w)=$workpackId and (m.completed is null or m.completed=false)   " +
       "  " +
       "  OPTIONAL MATCH (d)<-[:IS_SNAPSHOT_OF]-(sd:Date)-[:COMPOSES]->(:Baseline{active:true})   " +
       "  WITH m,d,sd    " +
       "  WHERE ( (sd is null) and (date(datetime(d.value)) < date($refDate)) )   " +
       "    OR ((sd is not null)  " +
       " 		and (  " +
       " 			(date(datetime(d.value)) > date(datetime(sd.value))) " +
       " 			or " +
       " 			(date($refDate) > date(datetime(sd.value))) " +
       " 		) " +
       " 	) " +
       "  RETURN distinct id(m) ")
  Set<Long> late(
    Long workpackId,
    LocalDate refDate
  );

  @Query(" MATCH   " +
       " (b:Baseline)<-[:COMPOSES]-(sm:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->  " +
       " (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false}),  " +
       " (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m),   " +
       " (b)<-[:COMPOSES]-(sd:Date)-[:IS_SNAPSHOT_OF]->(d)  " +
       " WHERE   " +
       "  id(w)=$workpackId and id(b)=$baselineId   " +
       " AND   " +
       "  (m.completed is null or m.completed=false)   " +
       " AND  " +
       " (	((sd is null) and date(datetime(d.value)) < date($refDate)) " +
       " 	OR  " +
       " 	((sd is not null)  " +
       " 	  AND  " +
       " 	  ( " +
       " 	    (date(datetime(d.value)) > date(datetime(sd.value))) or " +
       " 		(date($refDate) > date(datetime(sd.value))) " +
       " 	  ) " +
       " 	) " +
       " ) " +
       " RETURN distinct id(m) ")
  Set<Long> late(
    Long baselineId,
    Long workpackId,
    LocalDate refDate
  );

  @Query(" MATCH 	(w:Workpack{deleted:false,canceled:false})<-[:IS_IN*0..]-(m:Milestone{deleted:false,canceled:false}) " 
       + " , (m)<-[:FEATURES]-(d:Date)-[:IS_DRIVEN_BY]->(:DateModel{name:'Data'}) " 
       + " WHERE id(w)=$workpackId and m.completed is not null and m.completed=true " 
       + " OPTIONAL MATCH (d)<-[:IS_SNAPSHOT_OF]-(sd:Date)-[:COMPOSES]->(:Baseline{active:true}) " 
       + " with m,d,sd " 
       + " where (sd is null) or (date(datetime(d.value)) <= date(datetime(sd.value))) " 
       + " return distinct id(m) ")
  Set<Long> concluded(Long workpackId);

  @Query(" match   " 
       + " (b:Baseline)<-[:COMPOSES]-(sm:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->  " 
       + " (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false})  " 
       + " -[:IS_BASELINED_BY]->(b),   " 
       + " (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m),   " 
       + " (b)<-[:COMPOSES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d)  " 
       + " where   " 
       + "  id(w)=$workpackId and id(b)=$baselineId   " 
       + " and   " 
       + "  m.completed is not null and m.completed=true   " 
       + " and   " 
       + "  date(datetime(d.value)) <= date(datetime(planDate.value))   " 
       + " return distinct id(m) ")
  Set<Long> concluded(
    Long baselineId,
    Long workpackId
  );

  @Query(" match   " 
       + " (b:Baseline)<-[:COMPOSES]-(sm:Milestone{deleted:false,canceled:false})-[:IS_SNAPSHOT_OF]->  " 
       + " (m:Milestone{deleted:false,canceled:false})-[:IS_IN*0..]->(w:Workpack{deleted:false,canceled:false})  " 
       + " -[:IS_BASELINED_BY]->(b),   " 
       + " (:DateModel{name:'Data'})<-[:IS_DRIVEN_BY]-(d:Date)-[:FEATURES]->(m),   " 
       + " (b)<-[:COMPOSES]-(planDate:Date)-[:IS_SNAPSHOT_OF]->(d)  " 
       + " where   " 
       + "  id(w)=$workpackId and id(b)=$baselineId   " 
       + " and   " 
       + "  m.completed is not null and m.completed=true   " 
       + " and   " 
       + "  date(datetime(d.value)) > date(datetime(planDate.value))   " 
       + " return distinct id(m) ")
  Set<Long> lateConcluded(
    Long baselineId,
    Long workpackId
  );

  @Query(" MATCH 	(w:Workpack{deleted:false,canceled:false})<-[:IS_IN*0..]-(m:Milestone{deleted:false,canceled:false}) " 
       + " , (m)<-[:FEATURES]-(d:Date)-[:IS_DRIVEN_BY]->(:DateModel{name:'Data'}) " 
       + " , (d)<-[:IS_SNAPSHOT_OF]-(sd:Date)-[:COMPOSES]->(:Baseline{active:true}) "        
       + " WHERE id(w)=$workpackId and m.completed is not null and m.completed=true " 
       + " and " 
       + " (date(datetime(d.value)) > date(datetime(sd.value))) " 
       + " return distinct id(m) ")
  Set<Long> lateConcluded(Long workpackId);

}
