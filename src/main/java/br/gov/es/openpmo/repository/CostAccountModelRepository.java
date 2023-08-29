package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface CostAccountModelRepository extends Neo4jRepository<CostAccountModel, Long>, CustomRepository {

  Optional<CostAccountModel> findByPlanModelId(Long planModelId);

  @Query("match (cm:CostAccountModel) " +
    "where id(cm)=$id " +
    "match (cm)-[b:BELONGS_TO]->(pl:PlanModel) " +
    "optional match (cm)<-[i:IS_INSTANCE_BY]-(c:CostAccount) " +
    "optional match (cm)<-[f:FEATURES]-(pm:PropertyModel) " +
    "optional match (pm)-[d:DEFAULTS_TO]->(n) " +
    "with cm, b, pl, i, c, f, pm, d, n " +
    "return cm, b, pl, i, c, f, pm, d, n")
  Optional<CostAccountModel> findByIdWithRelationships(Long id);

}
