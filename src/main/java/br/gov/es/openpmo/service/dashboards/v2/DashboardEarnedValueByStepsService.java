package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepQueryResult;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.EarnedValueAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.INTERVAL_DATE_IN_BASELINE_NOT_FOUND;

@Service
public class DashboardEarnedValueByStepsService implements IDashboardEarnedValueByStepsService {

  private final EarnedValueAnalysisRepository repository;

  private final WorkpackRepository workpackRepository;

  private final BaselineRepository baselineRepository;

  @Autowired
  public DashboardEarnedValueByStepsService(
    final EarnedValueAnalysisRepository repository,
    final WorkpackRepository workpackRepository,
    final BaselineRepository baselineRepository
  ) {
    this.repository = repository;
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
  }

  @Override
  public List<EarnedValueByStep> build(final DashboardParameters parameters, final Optional<DateIntervalQuery> dateIntervalQuery) {
    final Long baselineId = parameters.getBaselineId();
    final Long workpackId = parameters.getWorkpackId();

    final List<Long> baselineIds = Optional.ofNullable(baselineId)
      .map(Collections::singletonList)
      .orElse(this.getActiveBaselineIds(workpackId));

    if (!dateIntervalQuery.isPresent()) {
      return new ArrayList<>();
    }

    final DateIntervalQuery interval = dateIntervalQuery.get();

    final List<EarnedValueByStep> list = new ArrayList<>();
    final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

    for (final YearMonth month : interval.toYearMonths()) {
      final EarnedValueByStepQueryResult value =
        this.getEarnedValueByStep(workpackId, baselineIds, month);

      accumulate.add(value.toEarnedValueByStep(null));
      list.add(accumulate.copy(true));
    }

    return list;
  }

  @Override
  public List<EarnedValueByStep> calculate(final Long workpackId, final Optional<DateIntervalQuery> dateIntervalQuery) {
    final boolean isProject = this.workpackRepository.isProject(workpackId);

    final List<Long> baselineIds = this.getBaselines(workpackId, isProject).stream()
      .map(Baseline::getId)
      .collect(Collectors.toList());

    if (baselineIds.isEmpty()) {
      return Collections.emptyList();
    }

    Long idBaseline = null;
    if (isProject) {
      idBaseline = baselineIds.get(0);
    }

    final DateIntervalQuery interval = dateIntervalQuery
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));

    final List<EarnedValueByStep> list = new ArrayList<>();
    final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

    for (final YearMonth month : interval.toYearMonths()) {
      final EarnedValueByStepQueryResult value =
        this.getEarnedValueByStep(workpackId, baselineIds, month);

      accumulate.add(value.toEarnedValueByStep(idBaseline));
      list.add(accumulate.copy(true));
    }

    return list;
  }

  private List<Baseline> getBaselines(final Long workpackId, boolean isProject) {
    final List<Baseline> baselines =
      this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);

    if (isProject) {
      return baselines;
    }

    for (final Baseline baseline : baselines) {
      if (baseline.isActive()) {
        return Collections.singletonList(baseline);
      }
    }

    return baselines.stream()
      .max(Comparator.comparing(Baseline::getProposalDate))
      .map(Collections::singletonList)
      .orElse(Collections.emptyList());
  }

  private List<Long> getActiveBaselineIds(final Long workpackId) {
    final List<Baseline> baselines = this.hasActiveBaseline(workpackId)
      ? this.findActiveBaseline(workpackId)
      : this.findAllActiveBaselines(workpackId);

    return baselines.stream()
      .map(Baseline::getId)
      .collect(Collectors.toList());
  }

  private EarnedValueByStepQueryResult getEarnedValueByStep(
    final Long workpackId,
    final List<Long> baselineIds,
    final YearMonth month
  ) {
    return this.repository.getEarnedValueByStep(workpackId, baselineIds, month.atDay(1), month.atEndOfMonth());
  }

  private boolean hasActiveBaseline(final Long workpackId) {
    return this.workpackRepository.hasActiveBaseline(workpackId);
  }

  private List<Baseline> findActiveBaseline(final Long workpackId) {
    return this.baselineRepository.findActiveBaseline(workpackId)
      .map(Collections::singletonList)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private List<Baseline> findAllActiveBaselines(final Long workpackId) {
    return this.baselineRepository.findAllActiveBaselines(workpackId);
  }

}
