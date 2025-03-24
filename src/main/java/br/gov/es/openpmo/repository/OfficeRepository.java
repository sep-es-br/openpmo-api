package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfficeRepository extends Neo4jRepository<Office, Long>, CustomRepository {

  @Query("MATCH (o:Office) WHERE id(o)=$id return o")
  Optional<Office> findByIdThin(Long id);


  @Query(
      "MATCH (person:Person)-[canAccessOffice:CAN_ACCESS_OFFICE]->(office:Office) " +
      "WHERE ID(person) = $idPerson AND ID(office) = $idOffice AND canAccessOffice.permissionLevel <> 'NONE' " +
      "RETURN canAccessOffice "
  )
  List<CanAccessOffice> findAllCanAccessOfficeByIdPerson(Long idPerson, Long idOffice);

  @Query("MATCH (plan:Plan)-[isAdoptedBy:IS_ADOPTED_BY]->(office:Office) " +
         "WHERE id(plan)=$planId " +
         "RETURN office")
  Optional<Office> findOfficeByPlanId(@Param("planId") Long planId);

  @Query("MATCH (o:Office)<-[:IS_ADOPTED_BY]-(pm:PlanModel) " +
         "WHERE id(o)=$idOffice " +
         "RETURN pm")
  List<PlanModel> findAllPlanModelsByOfficeId(Long idOffice);

  @Query("MATCH (o:Office) " +
         "WITH o, " +
         "apoc.text.levenshteinSimilarity(apoc.text.clean(o.name), apoc.text.clean($name)) AS nameScore, " +
         "apoc.text.levenshteinSimilarity(apoc.text.clean(o.fullName), apoc.text.clean($name)) AS fullNameScore " +
         "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
         "WHERE score > $searchCutOffScore " +
         "RETURN o " +
         "ORDER BY score DESC")
  List<Office> findAllOfficeByNameOrFullName(
    @Param("name") String name,
    @Param("searchCutOffScore") double searchCutOffScore
  );

}
