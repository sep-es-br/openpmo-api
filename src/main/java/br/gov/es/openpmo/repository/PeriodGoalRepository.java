package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.indicators.PeriodGoal;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PeriodGoalRepository extends Neo4jRepository<PeriodGoal, Long> {
}
