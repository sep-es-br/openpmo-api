package br.gov.es.openpmo.repository.baselines;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaselineHelperWorkpackRepository extends Neo4jRepository<Workpack, Long> {

  @Query("match (w:Workpack), (p:Workpack) " +
         "where id(w)=$childId AND id(p)=$parentId " +
         "create (w)-[:IS_IN]->(p)")
  void createIsInRelationship(
    Long childId,
    Long parentId
  );

  @Query("match (w:Workpack), (c:CostAccount) " +
         "where id(w)=$workpackId and id(c)=$costAccountId " +
         "create (w)<-[:APPLIES_TO]-(c)")
  void createAppliesToRelationship(
    Long workpackId,
    Long costAccountId
  );

  @Query("match (w:Workpack), (s:Schedule) " +
         "where id(w)=$workpackId and id(s)=$scheduleId " +
         "create (w)<-[:FEATURES]-(s)")
  void createFeaturesRelationship(
    Long workpackId,
    Long scheduleId
  );

}
