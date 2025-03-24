package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsFavoritedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IsFavoritedByRepository extends Neo4jRepository<IsFavoritedBy, Long> {

  @Query(
    "MATCH (person:Person)<-[isFavoritedBy:IS_FAVORITED_BY]-(workpack:Workpack)  " +
    "MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
    "WITH * " +
    "WHERE id(person)=$personId AND id(workpack)=$workpackId AND id(plan)=$planId " +
    "RETURN isFavoritedBy"
  )
  Optional<IsFavoritedBy> findIsFavoritedByPersonIdAndWorkpackIdAndPlanId(
    Long personId,
    Long workpackId,
    Long planId
  );

  @Query("MATCH (p:Person) where id(p) = $personId " +
          "MATCH (w:Workpack) where id(w) = $workpackId " +
          "CREATE (w)-[r:IS_FAVORITED_BY { " +
          "idPlan: $planId " +
          "}]->(p) " +
          "RETURN r")
  IsFavoritedBy createIsFavoriteBy(Long personId, Long workpackId,
                                            Long planId);
}
