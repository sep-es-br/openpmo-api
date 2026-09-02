package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.model.budget.BudgetPlan;
import br.gov.es.openpmo.model.budget.FinancialSource;
import br.gov.es.openpmo.repository.BudgetPlanRepository;
import br.gov.es.openpmo.repository.FinancialSourceRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Resolves SIGEFES values to one canonical node per natural key.
 *
 * The plugin returns value objects, not Neo4j ids. Resolving them here keeps
 * the property and model persistence paths consistent and allows historical
 * values to be reused by subsequent saves.
 */
@Service
public class BudgetReferenceService {

  private static final String NULL_PART = "<null>";

  private final BudgetPlanRepository budgetPlanRepository;
  private final FinancialSourceRepository financialSourceRepository;

  public BudgetReferenceService(
    BudgetPlanRepository budgetPlanRepository,
    FinancialSourceRepository financialSourceRepository
  ) {
    this.budgetPlanRepository = budgetPlanRepository;
    this.financialSourceRepository = financialSourceRepository;
  }

  public Set<BudgetPlan> resolveBudgetPlans(Set<BudgetPlan> values) {
    if (values == null || values.isEmpty()) {
      return values;
    }
    return values.stream()
      .map(this::resolveBudgetPlan)
      .collect(Collectors.toCollection(HashSet::new));
  }

  public Set<FinancialSource> resolveFinancialSources(Set<FinancialSource> values) {
    if (values == null || values.isEmpty()) {
      return values;
    }
    return values.stream()
      .map(this::resolveFinancialSource)
      .collect(Collectors.toCollection(HashSet::new));
  }

  private BudgetPlan resolveBudgetPlan(BudgetPlan value) {
    if (value == null || isBlank(value.getBudgetUnitCode()) || isBlank(value.getBudgetPlanCode())) {
      return value;
    }

    final String identityKey = key(
      "BUDGET_PLAN",
      value.getBudgetUnitCode(),
      value.getBudgetPlanCode()
    );
    return budgetPlanRepository.findByCodes(value.getBudgetUnitCode(), value.getBudgetPlanCode())
      .map(existing -> updateBudgetPlan(existing, value, identityKey))
      .orElseGet(() -> budgetPlanRepository.mergeByIdentity(
        identityKey,
        value.getBudgetUnitCode(),
        value.getBudgetUnitName(),
        value.getBudgetUnitAcronym(),
        value.getBudgetPlanCode(),
        value.getBudgetPlanName()
      ));
  }

  private FinancialSource resolveFinancialSource(FinancialSource value) {
    if (value == null || !hasFinancialSourceIdentity(value)) {
      return value;
    }

    final String identityKey = key(
      "FINANCIAL_SOURCE",
      value.getTypeCode(),
      value.getSourceGroupCode(),
      value.getSourceCode(),
      value.getDetailedSourceCode()
    );
    return financialSourceRepository.findByCodes(
        value.getTypeCode(),
        value.getSourceGroupCode(),
        value.getSourceCode(),
        value.getDetailedSourceCode()
      )
      .map(existing -> updateFinancialSource(existing, value, identityKey))
      .orElseGet(() -> financialSourceRepository.mergeByIdentity(
        identityKey,
        value.getTypeCode(),
        value.getTypeName(),
        value.getSourceGroupCode(),
        value.getSourceGroupName(),
        value.getSourceCode(),
        value.getSourceName(),
        value.getDetailedSourceCode(),
        value.getDetailedSourceName()
      ));
  }

  private BudgetPlan updateBudgetPlan(BudgetPlan existing, BudgetPlan incoming, String identityKey) {
    existing.setIdentityKey(identityKey);
    existing.setBudgetUnitName(incoming.getBudgetUnitName());
    existing.setBudgetUnitAcronym(incoming.getBudgetUnitAcronym());
    existing.setBudgetPlanName(incoming.getBudgetPlanName());
    return budgetPlanRepository.save(existing);
  }

  private FinancialSource updateFinancialSource(
    FinancialSource existing,
    FinancialSource incoming,
    String identityKey
  ) {
    existing.setIdentityKey(identityKey);
    existing.setTypeName(incoming.getTypeName());
    existing.setSourceGroupName(incoming.getSourceGroupName());
    existing.setSourceName(incoming.getSourceName());
    existing.setDetailedSourceName(incoming.getDetailedSourceName());
    return financialSourceRepository.save(existing);
  }

  private boolean hasFinancialSourceIdentity(FinancialSource value) {
    return !isBlank(value.getTypeCode())
      || !isBlank(value.getSourceGroupCode())
      || !isBlank(value.getSourceCode())
      || !isBlank(value.getDetailedSourceCode());
  }

  private String key(String type, String... parts) {
    return type + ":" + java.util.Arrays.stream(parts)
      .map(this::encode)
      .collect(Collectors.joining(":"));
  }

  private String encode(String value) {
    if (value == null) {
      return NULL_PART;
    }
    return Base64.getUrlEncoder().withoutPadding()
      .encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }
}
