package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.*;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.TripleConstraintDataChart;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.dashboards.EarnedValueAnalysisData;
import br.gov.es.openpmo.model.dashboards.TripleConstraintData;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class DashboardService implements IDashboardService {

    private final IDashboardTripleConstraintService tripleConstraintService;

    private final IDashboardMilestoneService milestoneService;

    private final IDashboardRiskService riskService;

    private final IDashboardDatasheetService datasheetService;

    private final IDashboardEarnedValueAnalysisService earnedValueAnalysisService;

    private final DashboardRepository dashboardRepository;

    private final BaselineRepository baselineRepository;

    private final WorkpackRepository workpackRepository;

    private final IDashboardIntervalService intervalService;

    public DashboardService(
            IDashboardTripleConstraintService tripleConstraintService,
            IDashboardMilestoneService milestoneService,
            IDashboardRiskService riskService,
            IDashboardDatasheetService datasheetService,
            IDashboardEarnedValueAnalysisService earnedValueAnalysisService,
            DashboardRepository dashboardRepository,
            BaselineRepository baselineRepository,
            WorkpackRepository workpackRepository,
            IDashboardIntervalService intervalService
    ) {
        this.tripleConstraintService = tripleConstraintService;
        this.milestoneService = milestoneService;
        this.riskService = riskService;
        this.datasheetService = datasheetService;
        this.earnedValueAnalysisService = earnedValueAnalysisService;
        this.dashboardRepository = dashboardRepository;
        this.baselineRepository = baselineRepository;
        this.workpackRepository = workpackRepository;
        this.intervalService = intervalService;
    }

    @Override
    @Transactional
    public DashboardResponse build(DashboardParameters parameters) {
        if (parameters == null) {
            return null;
        }

        return new DashboardResponse(
                getRisk(parameters),
                getMilestone(parameters),
                getTripleConstraintList(parameters),
                getDatasheet(parameters),
                getEarnedValueAnalysis(parameters)
        );
    }

    @Override
    @Transactional
    public SimpleDashboard buildSimple(Long workpackId) {
        final Interval interval = intervalService.calculateFor(workpackId);

        if (interval.getStartDate() == null || interval.getEndDate() == null) {
            return null;
        }

        final YearMonth previousMonth = YearMonth.now().minusMonths(1);
        final YearMonth startMonth = YearMonth.from(interval.getStartDate());
        final YearMonth endMonth = YearMonth.from(interval.getEndDate());
        final YearMonth clampDate = clampDate(previousMonth, startMonth, endMonth);

        final DashboardParameters parameters =
                new DashboardParameters(false, workpackId, null, clampDate, null);

        final Optional<PerformanceIndexes> performanceIndexes = Optional.of(parameters)
                .map(this::getEarnedValueAnalysis)
                .map(DashboardEarnedValueAnalysis::getPerformanceIndexes)
                .flatMap(indexes -> indexes.stream().findFirst());

        return new SimpleDashboard(
                getRisk(parameters),
                getMilestone(parameters),
                getTripleConstraint(parameters),
                getCostPerformanceIndex(performanceIndexes),
                getSchedulePerformanceIndex(performanceIndexes),
                getEarnedValue(performanceIndexes)
        );
    }

    private TripleConstraintDataChart getTripleConstraint(DashboardParameters parameters) {
        return getTripleConstraintList(parameters).stream().findFirst().orElse(null);
    }

    private BigDecimal getEarnedValue(Optional<PerformanceIndexes> indexes) {
        return indexes.map(PerformanceIndexes::getEarnedValue).orElse(null);
    }

    private SchedulePerformanceIndex getSchedulePerformanceIndex(Optional<PerformanceIndexes> indexes) {
        return indexes.map(PerformanceIndexes::getSchedulePerformanceIndex).orElse(null);
    }

    private CostPerformanceIndex getCostPerformanceIndex(Optional<PerformanceIndexes> indexes) {
        return indexes.map(PerformanceIndexes::getCostPerformanceIndex).orElse(null);
    }

    private RiskDataChart getRisk(DashboardParameters parameters) {
        return Optional.of(parameters)
                .map(this.riskService::build)
                .orElse(null);
    }

    private MilestoneDataChart getMilestone(DashboardParameters parameters) {
        return Optional.of(parameters)
                .map(this.milestoneService::build)
                .orElse(null);
    }

    private List<TripleConstraintDataChart> getTripleConstraintList(DashboardParameters parameters) {
        final Long workpackId = parameters.getWorkpackId();
        final Long baselineId = parameters.getBaselineId();
        final YearMonth yearMonth = parameters.getYearMonth();

        return Optional.of(parameters)
                .map(this::getTripleConstraintData)
                .map(data -> getTripleConstraintDataChart(workpackId, baselineId, yearMonth, data))
                .orElse(Collections.singletonList(this.tripleConstraintService.build(parameters)));
    }

    private List<TripleConstraintDataChart> getTripleConstraintDataChart(
            Long workpackId,
            Long baselineId,
            YearMonth yearMonth,
            List<TripleConstraintData> tripleConstraintData
    ) {
        List<Baseline> filteredBaselines = getBaselines(workpackId, baselineId);

        List<LocalDate> dateList = tripleConstraintData.stream()
                .map(TripleConstraintData::getMesAno)
                .collect(Collectors.toList());

        YearMonth finalYearMonth = clampYearMonth(yearMonth, dateList);

        return tripleConstraintData
                .stream()
                .map(TripleConstraintData::getResponse)
                .filter(dataChart -> samePeriod(dataChart, finalYearMonth))
                .filter(dataChart -> sameBaseline(dataChart, baselineId, filteredBaselines))
                .collect(Collectors.toList());
    }

    private YearMonth clampYearMonth(YearMonth yearMonth, List<LocalDate> dateList) {
        YearMonth valid = thisOrPreviousMonth(yearMonth);

        final YearMonth minDate = dateList.stream()
                .min(LocalDate::compareTo)
                .map(YearMonth::from)
                .orElse(valid);

        final YearMonth maxDate = dateList.stream()
                .max(LocalDate::compareTo)
                .map(YearMonth::from)
                .orElse(valid);

        return clampDate(valid, minDate, maxDate);
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

    private YearMonth thisOrPreviousMonth(YearMonth test) {
        return Optional.ofNullable(test)
                .orElseGet(() -> YearMonth.now().minusMonths(1));
    }

    private List<Baseline> getBaselines(Long workpackId, Long baselineId) {
        final List<Baseline> baselines =
                this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);

        if (baselineId != null) {
            return baselines.stream()
                    .filter(baseline -> Objects.equals(baseline.getId(), baselineId))
                    .collect(Collectors.toList());
        }

        if (this.workpackRepository.isProject(workpackId)) {
            return baselines;
        }

        for (Baseline baseline : baselines) {
            if (baseline.isActive()) {
                return Collections.singletonList(baseline);
            }
        }

        return baselines.stream()
                .max(Comparator.comparing(Baseline::getProposalDate))
                .map(Collections::singletonList)
                .orElse(null);
    }

    @Nullable
    private List<TripleConstraintData> getTripleConstraintData(DashboardParameters parameters) {
        return this.dashboardRepository.findByWorkpackId(parameters.getWorkpackId())
                .map(Dashboard::getTripleConstraint)
                .map(this::deserializeTripleConstraint)
                .orElse(null);
    }

    private boolean sameBaseline(TripleConstraintDataChart dataChart, Long baselineId, List<Baseline> baselines) {
        if (Objects.equals(dataChart.getIdBaseline(), baselineId)) {
            return true;
        }

        return baselines.stream()
                .map(Baseline::getId)
                .anyMatch(id -> Objects.equals(dataChart.getIdBaseline(), id));
    }

    private boolean samePeriod(TripleConstraintDataChart chart, YearMonth yearMonth) {
        return Optional.of(chart)
                .map(TripleConstraintDataChart::getMesAno)
                .map(YearMonth::from)
                .map(mesAno -> mesAno.compareTo(yearMonth) == 0)
                .orElse(false);
    }

    private List<TripleConstraintData> deserializeTripleConstraint(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<TripleConstraintData>>() {
        }.getType());
    }

    private DatasheetResponse getDatasheet(DashboardParameters parameters) {
        return Optional.of(parameters)
                .map(this.datasheetService::build)
                .orElse(null);
    }

    private DashboardEarnedValueAnalysis getEarnedValueAnalysis(DashboardParameters parameters) {
        final Long workpackId = parameters.getWorkpackId();

        final DashboardEarnedValueAnalysis earnedValueAnalysis = this.dashboardRepository.findByWorkpackId(workpackId)
                .map(Dashboard::getEarnedValueAnalysis)
                .map(json -> new Gson().fromJson(json, EarnedValueAnalysisData.class))
                .map(EarnedValueAnalysisData::getResponse)
                .orElse(this.earnedValueAnalysisService.build(parameters));

        if (earnedValueAnalysis == null) {
            return null;
        }

        final List<PerformanceIndexes> performanceIndexes = filterPerformanceIndexes(earnedValueAnalysis, parameters.getYearMonth());
        final List<EarnedValueByStep> earnedValueBySteps = handleEarnedValueBySteps(earnedValueAnalysis, parameters.getYearMonth());

        final boolean baselinesEmpty = isBaselinesEmpty(workpackId);

        if (performanceIndexes.isEmpty()) {
            performanceIndexes.add(new PerformanceIndexes(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    baselinesEmpty ? null : new CostPerformanceIndex(null, BigDecimal.ZERO),
                    baselinesEmpty ? null : new SchedulePerformanceIndex(null, BigDecimal.ZERO),
                    parameters.getYearMonth()
            ));
        }

        earnedValueAnalysis.setPerformanceIndexes(performanceIndexes);
        earnedValueAnalysis.setEarnedValueByStep(earnedValueBySteps);

        return earnedValueAnalysis;
    }

    private boolean isBaselinesEmpty(Long workpackId) {
        List<Baseline> baselines = hasActiveBaseline(workpackId)
                ? findActiveBaseline(workpackId)
                : findAllActiveBaselines(workpackId);

        return baselines.isEmpty();
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


    private List<PerformanceIndexes> filterPerformanceIndexes(
            DashboardEarnedValueAnalysis earnedValueAnalysis,
            YearMonth yearMonth
    ) {
        final List<LocalDate> dateList = earnedValueAnalysis.getPerformanceIndexes()
                .stream()
                .map(PerformanceIndexes::getDate)
                .map(YearMonth::atEndOfMonth)
                .collect(Collectors.toList());

        final YearMonth clampYearMonth = clampYearMonth(yearMonth, dateList);

        return earnedValueAnalysis.getPerformanceIndexes()
                .stream()
                .filter(indexes -> indexes.getDate().compareTo(clampYearMonth) == 0)
                .collect(Collectors.toList());
    }

    private List<EarnedValueByStep> handleEarnedValueBySteps(
            DashboardEarnedValueAnalysis earnedValueAnalysis,
            YearMonth yearMonth
    ) {
        final List<LocalDate> dateList = earnedValueAnalysis.getEarnedValueByStep()
                .stream()
                .map(EarnedValueByStep::getDate)
                .map(YearMonth::atEndOfMonth)
                .collect(Collectors.toList());

        final YearMonth clampYearMonth = clampYearMonth(yearMonth, dateList);

        final ArrayList<EarnedValueByStep> earnedValueBySteps = new ArrayList<>();

        earnedValueAnalysis.getEarnedValueByStep()
                .forEach(step -> earnedValueBySteps.add(step.copy(step.getDate().compareTo(clampYearMonth) <= 0)));

        return earnedValueBySteps;
    }

}
