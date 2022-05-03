package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.schedule.*;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;

@Service
public class ScheduleService {

    private final StepRepository stepRepository;
    private final ScheduleRepository scheduleRepository;
    private final IAsyncDashboardService dashboardService;
    private final CostAccountService costAccountService;
    private final WorkpackService workpackService;
    private final ModelMapper modelMapper;

    @Autowired
    public ScheduleService(
            final StepRepository stepRepository,
            final ScheduleRepository scheduleRepository,
            final IAsyncDashboardService dashboardService,
            final CostAccountService costAccountService,
            final WorkpackService workpackService,
            final ModelMapper modelMapper
    ) {
        this.stepRepository = stepRepository;
        this.scheduleRepository = scheduleRepository;
        this.dashboardService = dashboardService;
        this.costAccountService = costAccountService;
        this.workpackService = workpackService;
        this.modelMapper = modelMapper;
    }

    private static void setScheduleDtoWorkpack(final Schedule schedule, final ScheduleDto scheduleDto) {
        final Workpack workpack = schedule.getWorkpack();
        scheduleDto.setIdWorkpack(getWorkpackId(workpack));
    }

    private static Long getWorkpackId(final Workpack workpack) {
        return workpack.getId();
    }

    private static void addsStepsToMap(final Iterable<? extends Step> steps, final Map<? super Integer, List<Step>> mapsYearToStep) {
        for (Step step : steps) {
            mapsYearToStep.computeIfAbsent(step.getYear(), ifIsMissingStepList -> new ArrayList<>());
            mapsYearToStep.get(step.getYear()).add(step);
        }
    }

    private static void sortGroupsByYear(final List<? extends GroupStepDto> groups) {
        groups.sort(Comparator.comparing(GroupStepDto::getYear));
    }

    private static void sortStepsByPeriodFromStart(final List<? extends StepDto> steps) {
        steps.sort(Comparator.comparing(StepDto::getPeriodFromStart));
    }

    private static void ifEndDateIsBeforeStartDateThrowsException(final ScheduleParamDto scheduleParamDto) {
        final LocalDate start = getStart(scheduleParamDto);
        final LocalDate end = getEnd(scheduleParamDto);

        if (end.isBefore(start)) {
            throw new NegocioException(SCHEDULE_START_DATE_AFTER_DATE_ERROR);
        }
    }

    private static void setScheduleStartDate(final ScheduleParamDto scheduleParamDto, final Schedule schedule) {
        schedule.setStart(getStart(scheduleParamDto));
    }

    private static void setScheduleEndDate(final ScheduleParamDto scheduleParamDto, final Schedule schedule) {
        schedule.setEnd(getEnd(scheduleParamDto));
    }

    private static NegocioException scheduleAlreadyExistsException() {
        return new NegocioException(SCHEDULE_ALREADY_EXISTS);
    }

    private static LocalDate getStart(final ScheduleParamDto scheduleParamDto) {
        return scheduleParamDto.getStart();
    }

    private static LocalDate getEnd(final ScheduleParamDto scheduleParamDto) {
        return scheduleParamDto.getEnd();
    }

    private static long getPlannedWorkMonths(final ScheduleParamDto scheduleParamDto) {
        final LocalDate first = getStart(scheduleParamDto).withDayOfMonth(1);
        final LocalDate second = getEnd(scheduleParamDto).withDayOfMonth(1);

        return ChronoUnit.MONTHS.between(first, second) + 1L;
    }

    private static long getActualWorkMonths(final ScheduleParamDto scheduleParamDto) {
        final LocalDate start = getStart(scheduleParamDto).withDayOfMonth(1);
        final LocalDate end = getEnd(scheduleParamDto).withDayOfMonth(1);
        final LocalDate now = LocalDate.now();

        if (now.isBefore(start)) {
            return 0L;
        }

        if (now.isAfter(end)) {
            return ChronoUnit.MONTHS.between(start, end) + 1L;
        }

        return ChronoUnit.MONTHS.between(start, now) + 1L;
    }

    private static void addsStepToSteps(
            final Map<CostAccount, Pair<BigDecimal, BigDecimal>> map,
            final Collection<? super Step> steps,
            final long plannedWorkMonths,
            final long actualWorkMonths,
            final long currentMonth,
            final ScheduleParamDto scheduleParamDto
    ) {
        final Step step = new Step();

        setPlannedWork(plannedWorkMonths, scheduleParamDto, step);
        addsConsumesToStep(currentMonth, actualWorkMonths, map, step);

        step.setPeriodFromStart(currentMonth);
        step.setActualWork(BigDecimal.ZERO);

        if (currentMonth < actualWorkMonths) {
            setActualWork(actualWorkMonths, scheduleParamDto, step);
        }

        steps.add(step);
    }

    private static BigDecimal getParts(final long months, final BigDecimal decimal) {
        if (decimal == null) {
            return BigDecimal.ZERO;
        }
        return decimal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
    }

    private static void setPlannedWork(final long numberOfMonths, final ScheduleParamDto scheduleParamDto, final Step step) {
        final BigDecimal plannedWork = scheduleParamDto.getPlannedWork();
        step.setPlannedWork(BigDecimal.ZERO);

        if (Objects.nonNull(plannedWork)) {
            step.setPlannedWork(getParts(numberOfMonths, plannedWork));
        }
    }

    private static void setActualWork(final long numberOfMonths, final ScheduleParamDto scheduleParamDto, final Step step) {
        final BigDecimal actualWork = scheduleParamDto.getActualWork();
        step.setActualWork(BigDecimal.ZERO);

        if (Objects.nonNull(actualWork)) {
            step.setActualWork(getParts(numberOfMonths, actualWork));
        }
    }

    private static void addsConsumesToStep(
            final long currentMonth,
            final long actualWorkMonths,
            final Map<CostAccount, Pair<BigDecimal, BigDecimal>> mapCostToParts,
            final Step step
    ) {
        if (mapCostToParts.isEmpty()) {
            return;
        }

        step.setConsumes(new HashSet<>());

        for (Map.Entry<CostAccount, Pair<BigDecimal, BigDecimal>> entry : mapCostToParts.entrySet()) {
            CostAccount costAccount = entry.getKey();
            Pair<BigDecimal, BigDecimal> pair = entry.getValue();
            addsConsumesToStep(step, costAccount, pair, currentMonth < actualWorkMonths);
        }
    }

    private static void addsConsumesToStep(
            final Step step,
            final CostAccount costAccount,
            final Pair<BigDecimal, BigDecimal> pair,
            final boolean includeActualCost
    ) {
        BigDecimal actualCost = BigDecimal.ZERO;

        if (includeActualCost) {
            actualCost = pair.getFirst();
        }

        final Consumes consumes = new Consumes(
                null,
                actualCost,
                pair.getSecond(),
                costAccount,
                step
        );

        step.getConsumes().add(consumes);
    }

    public List<ScheduleDto> findAll(final Long idWorkpack) {
        final List<Schedule> schedules = this.scheduleRepository.findAllByWorkpack(idWorkpack);
        List<ScheduleDto> list = new ArrayList<>();

        for (Schedule schedule : schedules) {
            ScheduleDto scheduleDto = mapsToSheduleDto(schedule);
            list.add(scheduleDto);
        }

        return list;
    }

    public ScheduleDto mapsToSheduleDto(final Schedule schedule) {
        final ScheduleDto scheduleDto = this.mapsToScheduleDto(schedule);

        if (Objects.nonNull(schedule.getSteps())) {
            this.setScheduleDtoGroupStep(schedule, scheduleDto);
        }

        setScheduleDtoWorkpack(schedule, scheduleDto);

        this.scheduleRepository.findSnapshotByMasterId(schedule.getId()).ifPresent(snapshot -> {
            scheduleDto.setBaselineStart(snapshot.getStart());
            scheduleDto.setBaselineEnd(snapshot.getEnd());
        });

        return scheduleDto;
    }

    private <T> ScheduleDto mapsToScheduleDto(final T source) {
        return this.modelMapper.map(source, ScheduleDto.class);
    }

    private void setScheduleDtoGroupStep(final Schedule schedule, final ScheduleDto scheduleDto) {
        final List<GroupStepDto> groupStep = this.getGroupStep(schedule.getSteps());
        scheduleDto.setGroupStep(groupStep);
    }

    private List<GroupStepDto> getGroupStep(final Iterable<? extends Step> steps) {
        final List<GroupStepDto> groups = new ArrayList<>();
        if (Objects.nonNull(steps)) {
            this.addsStepsToGroupSteps(steps, groups);
        }
        return groups;
    }

    private void addsStepsToGroupSteps(final Iterable<? extends Step> steps, final List<GroupStepDto> groups) {
        final Map<Integer, List<Step>> mapYearToSteps = new HashMap<>();
        addsStepsToMap(steps, mapYearToSteps);
        this.addsStepsFromMapToGroupSteps(mapYearToSteps, groups);
        sortGroupsByYear(groups);
    }

    private void addsStepsFromMapToGroupSteps(
            final Map<Integer, ? extends List<Step>> map,
            final Collection<? super GroupStepDto> groups
    ) {
        for (Map.Entry<Integer, ? extends List<Step>> entry : map.entrySet()) {
            Integer year = entry.getKey();
            List<Step> stepList = entry.getValue();
            this.addsStepToGroupSteps(groups, year, stepList);
        }
    }

    private void addsStepToGroupSteps(
            final Collection<? super GroupStepDto> groups,
            final Integer year,
            final Iterable<? extends Step> stepList
    ) {
        final GroupStepDto group = new GroupStepDto();
        final List<StepDto> groupSteps = group.getSteps();

        this.addsStepsToGroupSteps(stepList, groupSteps);
        sortStepsByPeriodFromStart(groupSteps);

        group.setYear(year);
        groups.add(group);
    }

    private void addsStepsToGroupSteps(final Iterable<? extends Step> stepList, final Collection<? super StepDto> groupSteps) {
        for (Step step : stepList) {
            this.addsStepToGroupSteps(groupSteps, step);
        }
    }

    private void addsStepToGroupSteps(final Collection<? super StepDto> groupSteps, final Step step) {
        final StepDto dto = this.mapsToStepDto(step);
        final Step snapshotStep = this.stepRepository.findSnapshotOfActiveBaseline(step.getId()).orElse(null);
        dto.setBaselinePlannedWork(Optional.ofNullable(snapshotStep).map(Step::getPlannedWork).orElse(null));

        for (ConsumesDto consumes : dto.getConsumes()) {
            consumes.setBaselinePlannedCost(null);
            Optional.ofNullable(snapshotStep)
                    .map(Step::getConsumes)
                    .flatMap(consumesSnapshot -> getBaselinePlannedCost(consumes, consumesSnapshot))
                    .ifPresent(consumes::setBaselinePlannedCost);
        }

        groupSteps.add(dto);
    }

    private Optional<BigDecimal> getBaselinePlannedCost(ConsumesDto consumes, Set<Consumes> consumesSnapshot) {
        for (Consumes snapshot : consumesSnapshot) {
            if (snapshot.getIdCostAccountMaster().equals(consumes.getIdCostAccount())) {
                BigDecimal plannedCost = snapshot.getPlannedCost();
                return Optional.of(plannedCost);
            }
        }
        return Optional.empty();
    }

    private StepDto mapsToStepDto(final Step step) {
        final StepDto stepDto = new StepDto();

        stepDto.setId(step.getId());
        Schedule schedule = step.getSchedule();
        stepDto.setIdSchedule(schedule.getId());
        stepDto.setScheduleStart(schedule.getStart());
        stepDto.setScheduleEnd(schedule.getEnd());
        stepDto.setActualWork(step.getActualWork());
        stepDto.setPlannedWork(step.getPlannedWork());
        stepDto.setPeriodFromStart(step.getPeriodFromStartDate());
        stepDto.setConsumes(new HashSet<>());

        for (Consumes consumes : step.getConsumes()) {
            ConsumesDto consumesDto = new ConsumesDto();
            consumesDto.setId(consumes.getId());
            consumesDto.setActualCost(consumes.getActualCost());
            consumesDto.setPlannedCost(consumes.getPlannedCost());
            consumesDto.setCostAccount(EntityDto.of(consumes.getCostAccount()));
            stepDto.getConsumes().add(consumesDto);
        }

        return stepDto;
    }

    public Schedule save(final ScheduleParamDto scheduleParamDto) {
        this.validatesScheduleParamDto(scheduleParamDto);
        final Schedule schedule = this.scheduleRepository.save(this.mapToSchedule(scheduleParamDto));
        final List<Deliverable> deliverables = this.stepRepository.findDeliverablesByScheduleId(schedule.getId());
        updateDashboards(deliverables);
        return schedule;
    }

    public void updateDashboards(List<Deliverable> deliverables) {
        final List<Long> deliverablesId = new ArrayList<>();
        for (Deliverable deliverable : deliverables) {
            Long deliverableId = deliverable.getId();
            deliverablesId.add(deliverableId);
        }

        final List<Long> workpacksId = new ArrayList<>();
        for (Workpack workpack : this.stepRepository.findAllDeliverablesAndAscendents(deliverablesId)) {
            Long id = workpack.getId();
            workpacksId.add(id);
        }

        for (Long aLong : workpacksId) {
            this.dashboardService.calculate(aLong);
        }
    }

    private void validatesScheduleParamDto(final ScheduleParamDto scheduleParamDto) {
        this.ifScheduleAlreadyExistsThrowsException(scheduleParamDto);
        ifEndDateIsBeforeStartDateThrowsException(scheduleParamDto);
    }

    private Schedule mapToSchedule(final ScheduleParamDto scheduleParamDto) {
        final Schedule schedule = new Schedule();

        setScheduleStartDate(scheduleParamDto, schedule);
        setScheduleEndDate(scheduleParamDto, schedule);

        this.setScheduleSteps(scheduleParamDto, schedule);
        this.setScheduleWorkpack(scheduleParamDto, schedule);

        return schedule;
    }

    private void ifScheduleAlreadyExistsThrowsException(final ScheduleParamDto scheduleParamDto) {
        if (!this.scheduleRepository.findAllByWorkpack(scheduleParamDto.getIdWorkpack()).isEmpty()) {
            throw scheduleAlreadyExistsException();
        }
    }

    private void setScheduleSteps(final ScheduleParamDto scheduleParamDto, final Schedule schedule) {
        schedule.setSteps(this.getSteps(scheduleParamDto));
    }

    private void setScheduleWorkpack(final ScheduleParamDto scheduleParamDto, final Schedule schedule) {
        schedule.setWorkpack(this.getWorkpack(scheduleParamDto));
    }

    private Set<Step> getSteps(final ScheduleParamDto scheduleParamDto) {
        final Set<Step> steps = new HashSet<>();

        final long plannedWorkMonths = getPlannedWorkMonths(scheduleParamDto);
        final long actualWorkMonths = getActualWorkMonths(scheduleParamDto);

        final Map<CostAccount, Pair<BigDecimal, BigDecimal>> costsMap =
                this.getCostsMap(scheduleParamDto, plannedWorkMonths, actualWorkMonths);

        for (long currentMonth = 0; currentMonth < plannedWorkMonths; currentMonth++) {
            addsStepToSteps(costsMap, steps, plannedWorkMonths, actualWorkMonths, currentMonth, scheduleParamDto);
        }

        return steps;
    }

    private Workpack getWorkpack(final ScheduleParamDto scheduleParamDto) {
        return this.findWorkpackById(scheduleParamDto);
    }

    private Map<CostAccount, Pair<BigDecimal, BigDecimal>> getCostsMap(
            final ScheduleParamDto scheduleParamDto,
            final long plannedWorkMonths,
            final long actualWorkMonths
    ) {
        final Map<CostAccount, Pair<BigDecimal, BigDecimal>> mapCostToParts = new HashMap<>();

        for (CostSchedule costSchedule : scheduleParamDto.getCosts()) {
            final CostAccount costAccount = this.findCostAccountById(costSchedule);
            final BigDecimal plannedCostParts = getParts(plannedWorkMonths, costSchedule.getPlannedCost());
            final BigDecimal actualCostParts = getParts(actualWorkMonths, costSchedule.getActualCost());
            mapCostToParts.put(costAccount, Pair.of(actualCostParts, plannedCostParts));
        }

        return mapCostToParts;
    }

    private Workpack findWorkpackById(final ScheduleParamDto scheduleParamDto) {
        return this.workpackService.findById(scheduleParamDto.getIdWorkpack());
    }

    private CostAccount findCostAccountById(final CostSchedule costSchedule) {
        return this.costAccountService.findById(costSchedule.getId());
    }

    public void delete(final Long idSchedule) {
        final Schedule schedule = this.findById(idSchedule);

        if (this.scheduleRepository.hasActiveBaseline(idSchedule)) {
            throw new NegocioException(SCHEDULE_HAS_ACTIVE_BASELINE);
        }

        this.stepRepository.deleteAll(schedule.getSteps());
        this.scheduleRepository.delete(schedule);
    }

    public Schedule findById(final Long id) {
        return this.scheduleRepository.findByIdSchedule(id)
                .orElseThrow(() -> new NegocioException(SCHEDULE_NOT_FOUND));
    }

}
