package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.dashboards.EarnedValueAnalysisData;
import br.gov.es.openpmo.model.dashboards.TripleConstraintData;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import com.google.gson.Gson;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AsyncDashboardService implements IAsyncDashboardService {

    private final DashboardRepository dashboardRepository;

    private final IDashboardTripleConstraintService tripleConstraintService;

    private final IDashboardEarnedValueAnalysisService earnedValueAnalysisService;

    private final WorkpackRepository workpackRepository;

    private final IDashboardIntervalService intervalService;

    public AsyncDashboardService(
      DashboardRepository dashboardRepository,
      IDashboardTripleConstraintService tripleConstraintService,
      IDashboardEarnedValueAnalysisService earnedValueAnalysisService,
      WorkpackRepository workpackRepository,
      IDashboardIntervalService intervalService
    ) {
        this.dashboardRepository = dashboardRepository;
        this.tripleConstraintService = tripleConstraintService;
        this.earnedValueAnalysisService = earnedValueAnalysisService;
        this.workpackRepository = workpackRepository;
        this.intervalService = intervalService;
    }

    @Override
    public void calculate(@NonNull Long worpackId) {
        final Dashboard dashboard = getDashboard(worpackId);

        Optional.of(worpackId)
          .map(this::getTripleConstraint)
          .ifPresent(dashboard::setTripleConstraint);

        Optional.of(worpackId)
          .map(this::getEarnedValueAnalysis)
          .ifPresent(dashboard::setEarnedValueAnalysis);

        this.dashboardRepository.save(dashboard);
    }

    private Dashboard getDashboard(@NonNull Long worpackId) {
        return this.dashboardRepository
          .findByWorkpackId(worpackId)
          .orElse(createDashboard(worpackId));
    }

    private Dashboard createDashboard(@NonNull Long worpackId) {
        final Dashboard dashboard = new Dashboard();

        Optional.of(worpackId)
          .map(this::getWorkpack)
          .ifPresent(dashboard::setWorkpack);

        return dashboard;
    }

    private Workpack getWorkpack(@NonNull Long worpackId) {
        return this.workpackRepository.findById(worpackId)
          .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    }

    private String getEarnedValueAnalysis(@NonNull Long worpackId) {
        return Optional.of(worpackId)
          .map(this::calculateEarnedValueAnalysis)
          .map(EarnedValueAnalysisData::of)
          .map(new Gson()::toJson)
          .orElse(null);
    }

    private DashboardEarnedValueAnalysis calculateEarnedValueAnalysis(@NonNull Long worpackId) {
        return this.earnedValueAnalysisService.calculate(worpackId);
    }

    private String getTripleConstraint(@NonNull Long worpackId) {
        return Optional.of(worpackId)
          .map(this::calculateTripleConstraintDataChart)
          .map(this::convertToTripleConstraintData)
          .map(new Gson()::toJson)
          .orElse(null);
    }

    private List<TripleConstraintData> convertToTripleConstraintData(List<TripleConstraintDataChart> charts) {
        return charts.stream()
          .map(TripleConstraintData::of)
          .collect(Collectors.toList());
    }

    private List<TripleConstraintDataChart> calculateTripleConstraintDataChart(Long worpackId) {
        return Optional.of(worpackId)
          .flatMap(this.tripleConstraintService::calculate)
          .orElse(Collections.singletonList(this.tripleConstraintService.build(getParams(worpackId))));
    }

    DashboardParameters getParams(Long workpackId) {
        final Interval interval = intervalService.calculateFor(workpackId);

        if (interval.getStartDate() == null || interval.getEndDate() == null) {
            return null;
        }

        final YearMonth previousMonth = YearMonth.now().minusMonths(1);
        final YearMonth startMonth = YearMonth.from(interval.getStartDate());
        final YearMonth endMonth = YearMonth.from(interval.getEndDate());
        final YearMonth clampDate = clampDate(previousMonth, startMonth, endMonth);

        return new DashboardParameters(false, workpackId, null, clampDate, null);
    }

    private YearMonth clampDate(YearMonth underTest, YearMonth minDate, YearMonth maxDate) {
        if (underTest.isBefore(minDate)) {
            return minDate;
        }
        if (underTest.isAfter(maxDate)) {
            return maxDate;
        }
        return underTest;
    }

}
