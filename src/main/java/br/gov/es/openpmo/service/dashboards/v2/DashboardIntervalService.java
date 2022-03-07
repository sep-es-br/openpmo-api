package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
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

    private final DashboardRepository dashboardRepository;

    private final WorkpackRepository workpackRepository;

    private final BaselineRepository baselineRepository;

    public DashboardIntervalService(
            DashboardRepository dashboardRepository,
            WorkpackRepository workpackRepository,
            BaselineRepository baselineRepository
    ) {
        this.dashboardRepository = dashboardRepository;
        this.workpackRepository = workpackRepository;
        this.baselineRepository = baselineRepository;
    }

    @Override
    @Nullable
    public Interval calculateFor(@Nullable Long workpackId) {
        return Optional.ofNullable(workpackId)
                .filter(this::existsByIdOrElseThrow)
                .map(this::getInterval)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_IS_NULL));
    }

    @NonNull
    private boolean existsByIdOrElseThrow(@NonNull Long workpackId) {
        return Optional.of(workpackId)
                .map(this.workpackRepository::existsById)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    }

    @Nullable
    private Interval getInterval(@NonNull Long workpackId) {
        final List<Long> baselineIds = getActiveBaselineIds(workpackId);

        if (baselineIds.isEmpty()) {
            return this.workpackRepository.findIntervalInSchedulesChildrenOf(workpackId)
                    .map(Interval::new)
                    .orElse(null);
        }

        return this.dashboardRepository.fetchIntervalOfSchedules(workpackId, baselineIds)
                .map(Interval::new)
                .orElse(null);
    }

    private List<Long> getActiveBaselineIds(Long workpackId) {
        List<Baseline> baselines = hasActiveBaseline(workpackId)
                ? findActiveBaseline(workpackId)
                : findAllActiveBaselines(workpackId);

        return baselines.stream()
                .map(Baseline::getId)
                .collect(Collectors.toList());
    }

    private boolean hasActiveBaseline(Long workpackId) {
        return this.workpackRepository.hasActiveBaseline(workpackId);
    }

    private List<Baseline> findActiveBaseline(Long workpackId) {
        return this.baselineRepository.findActiveBaseline(workpackId)
                .map(Collections::singletonList)
                .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
    }

    private List<Baseline> findAllActiveBaselines(Long workpackId) {
        return this.baselineRepository.findAllActiveBaselines(workpackId);
    }


}