package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsFavoritedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IsFavoritedByRepository extends Neo4jRepository<IsFavoritedBy, Long> {

  @Query("OPTIONAL MATCH (person:Person)  " +
         "OPTIONAL MATCH (workpack:Workpack)-[isFavoritedBy:IS_FAVORITED_BY]->(person) " +
         "OPTIONAL MATCH (workpack)-[belongsTo:BELONGS_TO]->(plan:Plan) " +
         "WITH * " +
         "WHERE id(person)=$personId " +
         "AND id(workpack)=$workpackId " +
         "AND id(plan)=$planId " +
         "RETURN isFavoritedBy")
  Optional<IsFavoritedBy> findIsFavoritedByPersonIdAndWorkpackIdAndPlanId(
    Long personId,
    Long workpackId,
    Long planId
  );

}
