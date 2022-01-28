package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface RiskResponseRepository extends Neo4jRepository<RiskResponse, Long> {

  @Query("MATCH (response:RiskResponse)-[:MITIGATES]->(risk:Risk) " +
      "WHERE id(risk)=$riskId " +
      "RETURN response")
  Collection<RiskResponse> findAllByRiskId(Long riskId);

  @Query("MATCH (response:RiskResponse)-[:MITIGATES]->(risk:Risk) " +
      "WHERE id(response)=$riskResponseId " +
      "RETURN risk")
  Optional<Risk> findRiskByRiskResponseId(Long riskResponseId);

}
