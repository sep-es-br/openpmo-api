package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface CostAccountModelRepository extends Neo4jRepository<CostAccountModel, Long>, CustomRepository {

  Optional<CostAccountModel> findByPlanModelId(Long planModelId);


}
