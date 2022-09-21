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
import java.util.Collection;
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
  public List<EarnedValueByStep> build(final DashboardParameters parameters) {
    final Long baselineId = parameters.getBaselineId();
    final Long workpackId = parameters.getWorkpackId();

    final List<Long> baselineIds = Optional.ofNullable(baselineId)
      .map(Collections::singletonList)
      .orElse(this.getActiveBaselineIds(workpackId));

    final DateIntervalQuery interval = this.findIntervalInSnapshots(workpackId, baselineIds);

    final List<EarnedValueByStep> list = new ArrayList<>();
    final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

    for(final YearMonth month : interval.toYearMonths()) {
      final EarnedValueByStepQueryResult value =
        this.getEarnedValueByStep(workpackId, baselineIds, month);

      accumulate.add(value.toEarnedValueByStep());
      list.add(accumulate.copy(true));
    }

    return list;
  }

  @Override
  public List<EarnedValueByStep> calculate(final Long workpackId) {
    final Optional<List<Long>> maybeBaselineIds = Optional.ofNullable(this.getBaselines(workpackId))
      .map(Collection::stream)
      .map(b -> b.map(Baseline::getId))
      .map(b -> b.collect(Collectors.toList()));

    if(!maybeBaselineIds.isPresent()) {
      return new ArrayList<>();
    }

    final DateIntervalQuery interval = this.findIntervalInSnapshots(workpackId, maybeBaselineIds.get());

    final List<EarnedValueByStep> list = new ArrayList<>();
    final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

    for(final YearMonth month : interval.toYearMonths()) {
      final EarnedValueByStepQueryResult value =
        this.getEarnedValueByStep(workpackId, maybeBaselineIds.get(), month);

      accumulate.add(value.toEarnedValueByStep());
      list.add(accumulate.copy(true));
    }

    return list;
  }

  private List<Baseline> getBaselines(final Long workpackId) {
    final List<Baseline> baselines =
      this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);

    if(this.workpackRepository.isProject(workpackId)) {
      return baselines;
    }

    for(final Baseline baseline : baselines) {
      if(baseline.isActive()) {
        return Collections.singletonList(baseline);
      }
    }

    return baselines.stream()
      .max(Comparator.comparing(Baseline::getProposalDate))
      .map(Collections::singletonList)
      .orElse(null);
  }

  private List<Long> getActiveBaselineIds(final Long workpackId) {
    final List<Baseline> baselines = this.hasActiveBaseline(workpackId)
      ? this.findActiveBaseline(workpackId)
      : this.findAllActiveBaselines(workpackId);

    return baselines.stream()
      .map(Baseline::getId)
      .collect(Collectors.toList());
  }

  private DateIntervalQuery findIntervalInSnapshots(
    final Long workpackId,
    final List<Long> idBaseline
  ) {
    return this.baselineRepository.fetchIntervalOfSchedules(workpackId, idBaseline)
      .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
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
