package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.dashboards.Dashboard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DashboardRepository extends Neo4jRepository<Dashboard, Long> {

    @Query("match (d:Dashboard)-[:BELONGS_TO]->(w:Workpack{deleted:false,canceled:false}) " +
            "where id(w)=$workpackId " +
            "return d")
    Optional<Dashboard> findByWorkpackId(Long workpackId);

}
