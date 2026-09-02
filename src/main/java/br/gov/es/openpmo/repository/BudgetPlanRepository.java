package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.budget.BudgetPlan;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface BudgetPlanRepository extends Neo4jRepository<BudgetPlan, Long> {

  @Query("MATCH (budgetPlan:BudgetPlan) "
    + "WHERE budgetPlan.budgetUnitCode = $budgetUnitCode "
    + "AND budgetPlan.budgetPlanCode = $budgetPlanCode "
    + "RETURN budgetPlan LIMIT 1")
  Optional<BudgetPlan> findByCodes(String budgetUnitCode, String budgetPlanCode);

  @Query("MERGE (budgetPlan:BudgetPlan {identityKey: $identityKey}) "
    + "SET budgetPlan.budgetUnitCode = $budgetUnitCode, "
    + "budgetPlan.budgetUnitName = $budgetUnitName, "
    + "budgetPlan.budgetUnitAcronym = $budgetUnitAcronym, "
    + "budgetPlan.budgetPlanCode = $budgetPlanCode, "
    + "budgetPlan.budgetPlanName = $budgetPlanName "
    + "RETURN budgetPlan")
  BudgetPlan mergeByIdentity(
    String identityKey,
    String budgetUnitCode,
    String budgetUnitName,
    String budgetUnitAcronym,
    String budgetPlanCode,
    String budgetPlanName
  );
}
