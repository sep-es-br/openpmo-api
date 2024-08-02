package br.gov.es.openpmo.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

import br.gov.es.openpmo.dto.dashboards.DashboardBaseline;
import br.gov.es.openpmo.dto.dashboards.DashboardDto;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.DashboardWorkpackDetailDto;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardRepository;

public class DashboardCacheUtil {

    @Autowired
    private ApplicationContext applicationContext;

    private final Map<WorkpackKey, DashboardWorkpackDetailDto> mapWorkpackDetail = new HashMap<>(0);
    private Map<Long, DashboardBaseline> mapDashboardBaseline = new HashMap<>(0);
    private boolean loadingAll;

    @PostConstruct
    public void loadAllDashboards() {
        loadAllCache();
    }

    @Async
    public void loadAllCache() {
        if (!loadingAll) {
            loadingAll = true;
            final DashboardRepository dashboardRepository = applicationContext.getBean(DashboardRepository.class);
            final List<DashboardWorkpackDetailDto> listDetail = dashboardRepository.findAllScheduleAndStep(null, null);
            final List<DashboardWorkpackDetailDto> listCost = dashboardRepository.findAllCost(null, null);
            listCost.forEach(
                c -> listDetail.stream().filter(d -> d.getIdWorkpack().equals(c.getIdWorkpack()) && d.getIdPlan().equals(c.getIdPlan())).findFirst().ifPresent(
                    x -> x.setForeseenCost(c.getForeseenCost())));
            final List<DashboardBaseline> baselines = dashboardRepository.findAllBaseline();
            final List<Long> balineIds = getBaselineIds(baselines);

            addPlannedData(dashboardRepository, balineIds, listDetail, null, null, null);

            addActualData(dashboardRepository, listDetail, null, null, null);

            addEarnedValueData(dashboardRepository, listDetail, balineIds, null, null, null, null);

            listDetail.addAll(getMilestones(dashboardRepository, balineIds, null, null));

            listDetail.forEach(c -> mapWorkpackDetail.put(new WorkpackKey(c.getIdWorkpack(), c.getIdPlan()), c));
        }
        loadingAll = false;
    }

    class WorkpackKey {
        private Long idWorkpack;
        private Long idPlan;

        public WorkpackKey(Long idWorkpack, Long idPlan) {
            this.idWorkpack = idWorkpack;
            this.idPlan = idPlan;
        }

        public Long getIdWorkpack() {
            return idWorkpack;
        }

        public Long getIdPlan() {
            return idPlan;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            WorkpackKey that = (WorkpackKey) o;
            return Objects.equals(idWorkpack, that.idWorkpack) && Objects.equals(idPlan, that.idPlan);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idWorkpack, idPlan);
        }

        @Override
        public String toString() {
            return "WorkpackKey{" + "idWorkpack=" + idWorkpack + ", idPlan=" + idPlan + '}';
        }
    }

    private void addActualData(DashboardRepository dashboardRepository, List<DashboardWorkpackDetailDto> listDetail
        , List<Long> workpackIds, LocalDate date, Long idPlan) {
        final List<DashboardWorkpackDetailDto> actualWork = dashboardRepository.findAllActualWork(workpackIds, date, idPlan);
        final List<DashboardWorkpackDetailDto> actualCost = dashboardRepository.findAllActualCost(workpackIds, date, idPlan);

        actualWork.forEach(
            a -> listDetail.stream().filter(d -> d.getIdWorkpack().equals(a.getIdWorkpack()) &&  d.getIdPlan().equals(a.getIdPlan())).findFirst().ifPresent(
                x -> {
                    x.setActualWork(a.getActualWork());
                    x.setForeseenWorkRefMonth(a.getForeseenWorkRefMonth());
                }));
        actualCost.forEach(
            a -> listDetail.stream().filter(d -> d.getIdWorkpack().equals(a.getIdWorkpack()) &&  d.getIdPlan().equals(a.getIdPlan())).findFirst().ifPresent(
                x -> x.setActualCost(a.getActualCost())));

        listDetail.stream().filter(d -> d.getActualWork() == null).forEach(x -> x.setActualWork(BigDecimal.ZERO));

    }

    private void addPlannedData(final DashboardRepository dashboardRepository, final List<Long> balineIds
        , final List<DashboardWorkpackDetailDto> listDetail, List<Long> workpackIds, LocalDate date, Long idPlan) {
        final List<DashboardWorkpackDetailDto> snapshotDetail =  dashboardRepository.findAllScheduleAndStepBaseline(balineIds, workpackIds, idPlan);
        final List<DashboardWorkpackDetailDto> snapshotCost = dashboardRepository.findAllCostBaseline(balineIds, workpackIds, idPlan);
        snapshotCost.forEach(c -> snapshotDetail.stream().filter(
            d -> d.getIdWorkpack().equals(c.getIdWorkpack()) && d.getIdPlan().equals(c.getIdPlan())).findFirst().ifPresent(
            x -> x.setPlannedCost(c.getPlannedCost())));
        snapshotDetail.forEach(
            s -> listDetail.stream().filter(l -> l.getIdWorkpack().equals(s.getIdWorkpack()) && l.getIdPlan().equals(s.getIdPlan())).findFirst().ifPresent(
                w -> {
                    w.setPlannedCost(s.getPlannedCost());
                    w.setPlannedWork(s.getPlannedWork());
                    w.setBaselineStart(s.getBaselineStart());
                    w.setBaselineEnd(s.getBaselineEnd());
                })
        );

        final List<DashboardWorkpackDetailDto> snapshotCostLimitedMont = dashboardRepository.findAllCostBaseline(balineIds, workpackIds, date, idPlan);
        snapshotCostLimitedMont.forEach(c -> listDetail.stream().filter(
            d -> d.getIdWorkpack().equals(c.getIdWorkpack()) && d.getIdPlan().equals(c.getIdPlan())).findFirst().ifPresent(
            x -> x.setPlannedCostRefMonth(c.getPlannedCostRefMonth())));
    }

    private void addEarnedValueData(DashboardRepository dashboardRepository, List<DashboardWorkpackDetailDto> listDetail,
                                    final List<Long> baselineIds, List<Long> workpackIds, LocalDate date, Long idPlan, Long idWorkpack) {

        final List<DashboardWorkpackDetailDto> earnedValue = dashboardRepository.findAllEarnedValueBaselineByTotalBaseline(returnSnapshotStepIds(idWorkpack), baselineIds, workpackIds, date, idPlan);

        earnedValue.forEach(
                a -> listDetail.stream().filter(d -> d.getIdWorkpack().equals(a.getIdWorkpack()) && d.getIdPlan().equals(a.getIdPlan())).findFirst().ifPresent(
                        x -> {
                            x.setEarnedValue(a.getEarnedValue());
                        }));
    }

    private List<DashboardWorkpackDetailDto> getMilestones(DashboardRepository dashboardRepository
        , final List<Long> balineIds, List<Long> workpackIds, Long idPlan) {
        final List<DashboardWorkpackDetailDto> milestoneDetail = dashboardRepository.findAllMilestoneMaster(workpackIds, idPlan);
        final List<DashboardWorkpackDetailDto> milestoneBaseline = dashboardRepository.findAllMilestoneBaseline(balineIds, workpackIds, idPlan);
        milestoneBaseline.forEach(b -> milestoneDetail.stream().filter(
            m -> m.getIdWorkpack().equals(b.getIdWorkpack()) && m.getIdPlan().equals(b.getIdPlan())).findFirst().ifPresent(x -> {
            x.setBaselineStart(b.getStart());
            x.setBaselineEnd(b.getEnd());
        }));
        return milestoneDetail;
    }

    private List<Long> getBaselineIds(List<DashboardBaseline> baselines) {
        mapDashboardBaseline = new HashMap<>(0);

        List<DashboardBaseline> proposed = baselines.stream().filter(b -> !b.isActive()).collect(Collectors.toList());
        for (DashboardBaseline b : proposed) {
            DashboardBaseline ref = mapDashboardBaseline.get(b.getIdWorkpack());
            if (ref == null) {
                mapDashboardBaseline.put(b.getIdWorkpack(), b);
                continue;
            }
            if (b.getProposalDate().isAfter(ref.getProposalDate())) {
                mapDashboardBaseline.put(b.getIdWorkpack(), b);
            }
        }
        Set<DashboardBaseline> actives = baselines.stream().filter(
            DashboardBaseline::isActive).collect(Collectors.toSet());
        actives.forEach(b -> mapDashboardBaseline.put(b.getIdWorkpack(), b));

        return mapDashboardBaseline.values().stream().map(DashboardBaseline::getIdBaseline).distinct().collect(
            Collectors.toList());
    }

    private DashboardDto getDashboardDto(Long idWorkpack, boolean isDeliverable, final Long idPlan) {
        final WorkpackRepository workpackRepository = applicationContext.getBean(WorkpackRepository.class);
        Set<Long> ids = isDeliverable
                        ? Collections.singleton(idWorkpack)
                        : workpackRepository.findAllChildren(idWorkpack);

        if (ids.isEmpty()) {
            return null;
        }
        final DashboardDto dto = new DashboardDto();
        ids.forEach(id -> {
            DashboardWorkpackDetailDto detail = new DashboardWorkpackDetailDto(mapWorkpackDetail.get(new WorkpackKey(id, idPlan)));
            if (detail.getIdWorkpack() != null) {
                dto.getWorkpacks().add(detail);
            }
        });
        return dto;
    }

    public DashboardMonthDto getDashboardMonthDto(Long idWorkpack, boolean isDeliverable, Long idPlan) {
        DashboardDto dashboard = getDashboardDto(idWorkpack, isDeliverable, idPlan);
        if (dashboard == null || dashboard.getWorkpacks().isEmpty() ) return null;
        return DashboardMonthDto.of(dashboard);
    }

    public DashboardMonthDto getListDashboardWorkpackDetailById(Long idWorkpack, Long idBaseline, LocalDate date, Long idPlan) {
        final DashboardRepository dashboardRepository = applicationContext.getBean(DashboardRepository.class);
        final WorkpackRepository workpackRepository = applicationContext.getBean(WorkpackRepository.class);
        Set<Long> ids = workpackRepository.findAllChildren(idWorkpack);
        List<Long> workpackIds = new ArrayList<>(ids);

        workpackIds.add(idWorkpack);
        final List<DashboardWorkpackDetailDto> listDetail = dashboardRepository.findAllScheduleAndStep(workpackIds, idPlan);
        final List<DashboardWorkpackDetailDto> listCost = dashboardRepository.findAllCost(workpackIds, idPlan);
        listCost.forEach(
            c -> listDetail.stream().filter(d -> d.getIdWorkpack().equals(c.getIdWorkpack())).findFirst().ifPresent(
                x -> x.setForeseenCost(c.getForeseenCost())));

        List<Long> baselineIds = idBaseline != null
                         ? Collections.singletonList(idBaseline)
                         : mapDashboardBaseline.values().stream().map(DashboardBaseline::getIdBaseline).collect(
                             Collectors.toList());

        addPlannedData(dashboardRepository, baselineIds, listDetail, workpackIds, date, idPlan);

        addActualData(dashboardRepository, listDetail, workpackIds, date, idPlan);

        addEarnedValueData(dashboardRepository, listDetail, baselineIds, workpackIds, date, idPlan, idWorkpack);

        listDetail.addAll(getMilestones(dashboardRepository, baselineIds, workpackIds, idPlan));

        if (CollectionUtils.isEmpty(listDetail)) {
            return null;
        }

        DashboardDto dashboardDto = new DashboardDto(date);
        dashboardDto.setWorkpacks(listDetail);

        return DashboardMonthDto.of(dashboardDto);
    }

    public List<EarnedValueByStepDto> getDashboardEarnedValueAnalysis(Long idWorkpack, Long idBaseline, LocalDate date, Long idPlan) {
        final DashboardRepository dashboardRepository = applicationContext.getBean(DashboardRepository.class);
        final WorkpackRepository workpackRepository = applicationContext.getBean(WorkpackRepository.class);
        Set<Long> ids = workpackRepository.findAllChildren(idWorkpack);

        final List<Long> workpackIds = new ArrayList<>(ids);

        workpackIds.add(idWorkpack);
        final List<EarnedValueByStepDto> steps = dashboardRepository.findAllEarnedValuesStep(workpackIds, idPlan);

        final List<EarnedValueByStepDto> listEstimatedCost = dashboardRepository.findAllEarnedValueEstimatedCost(workpackIds, idPlan);
        listEstimatedCost.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setEstimatedCost(ac.getEstimatedCost())));

        final List<EarnedValueByStepDto> listActualCost = dashboardRepository.findAllEarnedValueActualCost(workpackIds, date, idPlan);
        listActualCost.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setActualCost(ac.getActualCost())));

        final List<EarnedValueByStepDto> listActualWork = dashboardRepository.findAllEarnedValueActualWork(workpackIds, date, idPlan);
        listActualWork.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setActualWork(ac.getActualWork())));

        List<Long> baselineIds = idBaseline != null
                                 ? Collections.singletonList(idBaseline)
                                 : mapDashboardBaseline.values().stream().map(DashboardBaseline::getIdBaseline).collect(
                                     Collectors.toList());

        final List<EarnedValueByStepDto> listPlannedCost = dashboardRepository.findAllEarnedValuePlannedCost(baselineIds, workpackIds, idPlan);
        listPlannedCost.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setPlannedCost(ac.getPlannedCost())));

        final List<EarnedValueByStepDto> listPlannedWork = dashboardRepository.findAllEarnedValuePlannedWork(baselineIds, workpackIds, idPlan);
        listPlannedWork.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setPlannedWork(ac.getPlannedWork())));

        final List<EarnedValueByStepDto> listEarnedValue = dashboardRepository.findAllEarnedValueByTotalBaseline(returnSnapshotStepIds(idWorkpack), baselineIds, workpackIds, idPlan);
        listEarnedValue.forEach(ac -> steps.stream().filter(e -> e.getDate().equals(ac.getDate())).findFirst().ifPresent(
            x -> x.setEarnedValue(ac.getEarnedValue())));

        loadDataEarnedValueAnalysis(steps);

        return steps;
    }

    private void loadDataEarnedValueAnalysis(final List<EarnedValueByStepDto> steps) {
        BigDecimal plannedCost = BigDecimal.ZERO;
        BigDecimal actualCost = BigDecimal.ZERO;
        BigDecimal estimatedCost = BigDecimal.ZERO;
        BigDecimal plannedWork = BigDecimal.ZERO;
        BigDecimal actualWork = BigDecimal.ZERO;
        BigDecimal earnedValue = BigDecimal.ZERO;
        for (EarnedValueByStepDto step : steps) {
            if (step.getPlannedCost() == null) {
                step.setPlannedCost(BigDecimal.ZERO);
            }
            if (step.getPlannedWork() == null) {
                step.setPlannedWork(BigDecimal.ZERO);
            }
            if (step.getActualWork() == null) {
                step.setActualWork(BigDecimal.ZERO);
            }
            if (step.getActualCost() == null) {
                step.setActualCost(BigDecimal.ZERO);
            }
            if (step.getEstimatedCost() == null) {
                step.setEstimatedCost(BigDecimal.ZERO);
            }
            if (step.getEarnedValue() == null) {
                step.setEarnedValue(BigDecimal.ZERO);
            }

            step.setEarnedValue(step.getEarnedValue().add(earnedValue));
            step.setPlannedCost(step.getPlannedCost().add(plannedCost));
            step.setPlannedWork(step.getPlannedWork().add(plannedWork));
            step.setEstimatedCost(step.getEstimatedCost().add(estimatedCost));
            step.setActualWork(step.getActualWork().add(actualWork));
            step.setActualCost(step.getActualCost().add(actualCost));


            plannedCost = step.getPlannedCost();
            actualCost = step.getActualCost();
            estimatedCost = step.getEstimatedCost();
            plannedWork = step.getPlannedWork();
            actualWork = step.getActualWork();
            earnedValue = step.getEarnedValue();

        }
    }

    /**
     * Return all step ids from snapshot
     * @param idWorkpack <code>List</code> of workpack ids
     * @return <code>Set</code> of step ids
     */
    private Set<Long> returnSnapshotStepIds(Long idWorkpack) {
        final ScheduleRepository scheduleRepository = applicationContext.getBean(ScheduleRepository.class);
        final StepRepository stepRepository = applicationContext.getBean(StepRepository.class);

        List<Long> deliverableWorkpackIds = scheduleRepository.findAllDeliverable(idWorkpack);

        final List<Schedule> schedules = scheduleRepository.findAllByWorkpacks(deliverableWorkpackIds);
        final Set<Long> idsSchedule = schedules.stream().map(Schedule::getId).collect(Collectors.toSet());
        final List<ScheduleDto> snapshots = scheduleRepository.findSnapshotByMasterIds(new ArrayList<>(idsSchedule));
        final Set<Long> idsSnapshots = snapshots.stream().map(ScheduleDto::getIdSnapshot).collect(Collectors.toSet());
        final List<StepDto> stepSnapshot = stepRepository.findAllStepsnapshotByScheduleSnapshotIds(new ArrayList<>(idsSnapshots));

        return stepSnapshot.stream().map(StepDto::getId).collect(Collectors.toSet());
        // teste
    }


}
