package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

@Repository
public interface ProjectModelRepository extends Neo4jRepository<ProjectModel, Long> {

    @Query("MATCH (p:ProjectModel) WHERE p.notificationsSessionActive = true RETURN p")
    List<ProjectModel> findAllWithNotificationsSessionActive();

}
