package br.gov.es.openpmo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Schedule;

public interface ScheduleRepository extends Neo4jRepository<Schedule, Long> {

    @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE ID(w) = $idWorkpack "
            + "RETURN s, f, w, [ "
            + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
            + "  [(st)-[c1:CONSUMES]->(ca:CostAccount) | [c1, ca] ]"
            + "]")
    List<Schedule> findAllByWorkpack(@Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (s:Schedule)-[f:FEATURES]->(w:Workpack) WHERE ID(s) = $id "
           + "RETURN s, f, w, [ "
           + "  [(s)<-[c:COMPOSES]-(st:Step) | [c, st] ],"
           + "  [(st)-[c1:CONSUMES]->(ca:CostAccount) | [c1, ca] ]"
           + "]")
    Optional<Schedule> findByIdSchedule(@Param("id") Long id);
}
