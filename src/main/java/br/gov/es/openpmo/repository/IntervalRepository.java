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

  @Query("MATCH (workpack:Workpack{deleted:false,canceled:false}) " +
    "WHERE id(workpack)=$workpackId " +
    "RETURN workpack, [ " +
    " [(workpack)<-[i1:IS_IN*]-(v:Workpack{deleted:false,canceled:false}) | [i1, v]], " +
    " [(workpack)<-[f1:FEATURES]-(s1:Schedule) | [f1,s1]], " +
    " [(workpack)<-[i2:IS_IN*]-(d:Deliverable{deleted:false,canceled:false})<-[f2:FEATURES]-(s2:Schedule) | [i2,d,f2,s2]], " +
    " [(workpack)<-[f3:FEATURES]-(d1:Date) | [f3,d1]], " +
    " [(workpack)<-[i3:IS_IN*]-(m:Milestone{deleted:false,canceled:false})<-[f4:FEATURES]-(d2:Date) | [i3,m,f4,d2]] " +
    "]")
  Optional<Workpack> findWorkpackById(@Param("workpackId") Long workpackId);

  @Query("MATCH (b:Baseline)<-[ibb:IS_BASELINED_BY]-(p:Project{deleted:false}) " +
    "WHERE id(b) IN $baselineIds " +
    "RETURN b, ibb, p, [ " +
    " [(p)<-[i1:IS_IN*]->(w1:Workpack{deleted:false}) | [i1, w1]], " +
    " [(p)<-[i2:IS_IN*]->(w2:Workpack{deleted:false})<-[i3:IS_IN*]-(dw1:Deliverable{deleted:false})<-[f1:FEATURES]-(s1:Schedule)<-[iso1:IS_SNAPSHOT_OF]-(ss1:Schedule)-[c1:COMPOSES]->(b) | [i2,w2,i3,dw1,f1,s1,iso1,ss1,c1]], " +
    " [(p)<-[i4:IS_IN*]->(w3:Workpack{deleted:false})<-[i5:IS_IN*]-(mw1:Milestone{deleted:false})<-[f2:FEATURES]-(d1:Date)<-[iso2:IS_SNAPSHOT_OF]-(ds1:Date)-[c2:COMPOSES]->(b) | [i4,w3,i5,mw1,f2,d1,iso2,ds1,c2]], " +
    " [(p)<-[i6:IS_IN*]->(w4:Workpack{deleted:false})<-[f3:FEATURES]-(s2:Schedule)<-[iso3:IS_SNAPSHOT_OF]-(ss2:Schedule)-[c3:COMPOSES]->(b) | [i6,w4,f3,s2,iso3,ss2,c3]], " +
    " [(p)<-[i7:IS_IN*]->(w5:Workpack{deleted:false})<-[f4:FEATURES]-(d2:Date)<-[iso4:IS_SNAPSHOT_OF]-(ds2:Date)-[c4:COMPOSES]->(b) | [i7,w5,f4,d2,iso4,ds2,c4]], " +
    " [(p)<-[i8:IS_IN*]-(dw2:Deliverable{deleted:false})<-[f5:FEATURES]-(s3:Schedule)<-[iso5:IS_SNAPSHOT_OF]-(ss3:Schedule)-[c5:COMPOSES]->(b) | [dw2,f5,s3,iso5,ss3,c5]], " +
    " [(p)<-[i9:IS_IN*]-(mw2:Milestone{deleted:false})<-[f6:FEATURES]-(d3:Date)<-[iso6:IS_SNAPSHOT_OF]-(ds3:Date)-[c6:COMPOSES]->(b) | [i9,mw2,f6,d3,iso6,ds3,c6]] " +
    "]")
  List<Baseline> findBaselineByIds(@Param("baselineIds") List<Long> baselineIds);

}
