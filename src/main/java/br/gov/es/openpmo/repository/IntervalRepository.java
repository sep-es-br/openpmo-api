package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntervalRepository extends Neo4jRepository<Workpack, Long>, CustomRepository {

  @Query(" MATCH (w:Workpack{deleted:false,canceled:false})   " +
      " WHERE id(w)=$workpackId   " +
      "     OPTIONAL MATCH (w)<-[i1:IS_IN*0..]-(v:Workpack{deleted:false,canceled:false})   " +
      "     OPTIONAL MATCH (v)<-[f1:FEATURES]-(s1:Schedule) where 'Deliverable' in labels(w)    " +
      "     OPTIONAL MATCH (v)<-[f3:FEATURES]-(d1:Date) where 'Milestone' in labels(w) and (d1)-[:IS_DRIVEN_BY]->(:DateModel{name:'Data'}) " +
      " RETURN w, [   " +
      "     [ [i1, v]],   " +
      "     [ [f1,s1]],   " +
      "     [ [f3,d1]] " +
      " ]	 ")
  Optional<Workpack> findWorkpackById(@Param("workpackId") Long workpackId);

  @Query(" MATCH (b:Baseline)<-[ibb:IS_BASELINED_BY]-(p:Project{deleted:false})   " 
  + " WHERE id(b) IN $baselineIds " 
  + " OPTIONAL MATCH (p)<-[i1:IS_IN*]-(w:Workpack{deleted:false}) " 
  + " OPTIONAL MATCH (w)<-[f1:FEATURES]-(s1:Schedule)<-[iso1:IS_SNAPSHOT_OF]-(ss1:Schedule)-[c1:COMPOSES]->(b)  " 
  + "     where 'Deliverable' in labels(w) " 
  + " OPTIONAL MATCH (w)<-[f2:FEATURES]-(d1:Date)<-[iso2:IS_SNAPSHOT_OF]-(ds1:Date)-[c2:COMPOSES]->(b) " 
  + "     where 'Milestone' in labels(w) and (d1)-[:IS_DRIVEN_BY]->(:DateModel{name:'Data'}) " 
  + " RETURN b, ibb, p, [ " 
  + "     [i1, w], " 
  + "     [f1,s1,iso1,ss1,c1], "
  + "     [f2,d1,iso2,ds1,c2] "
  + " ] " )
  List<Baseline> findBaselineByIds(@Param("baselineIds") List<Long> baselineIds);

}
