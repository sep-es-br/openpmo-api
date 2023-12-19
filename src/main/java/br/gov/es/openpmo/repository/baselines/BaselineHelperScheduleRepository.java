package br.gov.es.openpmo.repository.baselines;

import br.gov.es.openpmo.model.schedule.Schedule;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaselineHelperScheduleRepository extends Neo4jRepository<Schedule, Long> {

  @Query("match (schedule:Schedule) where id(schedule)=$scheduleId " +
         "match (step:Step) where id(step)=$stepId " +
         "create (schedule)<-[:COMPOSES]-(step)")
  void createComposesRelationship(
    Long scheduleId,
    Long stepId
  );

}
