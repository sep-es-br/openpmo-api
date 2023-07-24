package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardIntervalService implements IDashboardIntervalService {

  private final FindWorkpackInterval findWorkpackInterval;

  private final FindWorkpackBaselineInterval findWorkpackBaselineInterval;

  private final WorkpackRepository workpackRepository;

  private final BaselineRepository baselineRepository;

  public DashboardIntervalService(
    final FindWorkpackInterval findWorkpackInterval,
    final FindWorkpackBaselineInterval findWorkpackBaselineInterval,
    final WorkpackRepository workpackRepository,
    final BaselineRepository baselineRepository
  ) {
    this.findWorkpackInterval = findWorkpackInterval;
    this.findWorkpackBaselineInterval = findWorkpackBaselineInterval;
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
  }

  @Override
  @Nullable
  public Interval calculateFor(@Nullable final Long workpackId) {
    return Optional.ofNullable(workpackId)
      .filter(this::existsByIdOrElseThrow)
      .map(this::getInterval)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_IS_NULL));
  }

  @NonNull
  private boolean existsByIdOrElseThrow(@NonNull final Long workpackId) {
    return Optional.of(workpackId)
      .map(this.workpackRepository::existsById)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  @Nullable
  private Interval getInterval(@NonNull final Long workpackId) {
    final List<Long> baselineIds = this.getActiveBaselineIds(workpackId);

    if(baselineIds.isEmpty()) {
      return this.findWorkpackInterval.execute(workpackId)
        .map(Interval::new)
        .orElse(null);
    }

    return this.findWorkpackBaselineInterval.execute(workpackId, baselineIds)
      .map(Interval::new)
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
