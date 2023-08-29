package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.schedule.CostSchedule;
import br.gov.es.openpmo.dto.schedule.DistributionStrategy;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import br.gov.es.openpmo.utils.MapPair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class GetCostAccountGroupedByDistributedParts {

  private final CostAccountService costAccountService;

  public GetCostAccountGroupedByDistributedParts(
    final CostAccountService costAccountService
  ) {
    this.costAccountService = costAccountService;
  }

  private static Map<Long, BigDecimal> getParts(
    final long months,
    final BigDecimal decimal,
    final int scale,
    final DistributionStrategy distribution
  ) {
    switch (distribution) {
      case LINEAR:
        return new StepValueLinearAllocator().execute(months, decimal, scale);
      case SIGMOIDAL:
        return new StepValueSigmoidalAllocator().execute(months, decimal, scale);
      default:
        throw new NegocioException();
    }
  }

  public Map<CostAccount, MapPair<Long, BigDecimal>> execute(
    final long plannedWorkMonths,
    final long actualWorkMonths,
    final DistributionStrategy distribution,
    final Iterable<? extends CostAccountValueAllocatorParameter> consumes
  ) {
    final Map<CostAccount, MapPair<Long, BigDecimal>> mapCostToParts = new HashMap<>();

    for (final CostAccountValueAllocatorParameter consumesData : consumes) {
      final CostAccount costAccount = this.findCostAccountById(consumesData.getIdCostAccount());
      final int scale = 2;
      final Map<Long, BigDecimal> plannedCostParts = getParts(
        plannedWorkMonths,
        consumesData.getPlannedCost(),
        scale,
        distribution
      );
      final Map<Long, BigDecimal> actualCostParts = getParts(
        actualWorkMonths,
        consumesData.getActualCost(),
        scale,
        distribution
      );
      mapCostToParts.put(
        costAccount,
        MapPair.of(
          actualCostParts,
          plannedCostParts
        )
      );
    }

    return mapCostToParts;
  }

  private CostAccount findCostAccountById(final Long costAccountId) {
    return this.costAccountService.findById(costAccountId);
  }
}
