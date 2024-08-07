package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.schedule.ConsumesDto;
import br.gov.es.openpmo.dto.schedule.DistributionStrategy;
import br.gov.es.openpmo.dto.schedule.GroupStepDto;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.MapPair;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_ALREADY_EXISTS;
import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_HAS_ACTIVE_BASELINE;
import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_START_DATE_AFTER_DATE_ERROR;

@Service
public class ScheduleService {

  private final StepRepository stepRepository;

  private final ScheduleRepository scheduleRepository;

  private final WorkpackService workpackService;

  private final ModelMapper modelMapper;

  private final CostAccountRepository costAccountRepository;

  private final UpdateStatusService updateStatusService;

  private final GetCostAccountGroupedByDistributedParts getCostAccountGroupedByDistributedParts;

  private final GetUnitMeasureScaleByWorkpack getUnitMeasureScaleByWorkpack;

  private final BaselineRepository baselineRepository;

  @Autowired
  public ScheduleService(
    final StepRepository stepRepository,
    final ScheduleRepository scheduleRepository,
    final WorkpackService workpackService,
    final ModelMapper modelMapper,
    final CostAccountRepository costAccountRepository,
    final UpdateStatusService updateStatusService,
    final GetCostAccountGroupedByDistributedParts getCostAccountGroupedByDistributedParts,
    final GetUnitMeasureScaleByWorkpack getUnitMeasureScaleByWorkpack,
    final BaselineRepository baselineRepository
  ) {
    this.stepRepository = stepRepository;
    this.scheduleRepository = scheduleRepository;
    this.workpackService = workpackService;
    this.modelMapper = modelMapper;
    this.costAccountRepository = costAccountRepository;
    this.updateStatusService = updateStatusService;
    this.getCostAccountGroupedByDistributedParts = getCostAccountGroupedByDistributedParts;
    this.getUnitMeasureScaleByWorkpack = getUnitMeasureScaleByWorkpack;
    this.baselineRepository = baselineRepository;
  }

  private static void setScheduleDtoWorkpack(
    final Schedule schedule,
    final ScheduleDto scheduleDto
  ) {
    final Workpack workpack = schedule.getWorkpack();
    scheduleDto.setIdWorkpack(getWorkpackId(workpack));
  }

  private static Long getWorkpackId(final Workpack workpack) {
    return workpack.getId();
  }

  private static void addsStepsToMap(
    final Iterable<? extends Step> steps,
    final Map<? super Integer, List<Step>> mapsYearToStep
  ) {
    for (final Step step : steps) {
      mapsYearToStep.computeIfAbsent(
        step.getYear(),
        ifIsMissingStepList -> new ArrayList<>()
      );
      mapsYearToStep.get(step.getYear()).add(step);
    }
  }

  private static void sortGroupsByYear(final List<? extends GroupStepDto> groups) {
    groups.sort(Comparator.comparing(GroupStepDto::getYear));
  }

  private static void sortStepsByPeriodFromStart(final List<? extends StepDto> steps) {
    steps.sort(Comparator.comparing(StepDto::getPeriodFromStart));
  }

  private static Optional<BigDecimal> getBaselinePlannedCost(
    final ConsumesDto consumes,
    final Iterable<? extends Consumes> consumesSnapshot
  ) {
    for (final Consumes snapshot : consumesSnapshot) {
      if (snapshot.getIdCostAccountMaster().equals(consumes.getIdCostAccount())) {
        final BigDecimal plannedCost = (snapshot.getPlannedCost() == null) ? BigDecimal.valueOf(0) : snapshot.getPlannedCost();
        return Optional.of(plannedCost);
      }
    }
    return Optional.empty();
  }

  private static void ifEndDateIsBeforeStartDateThrowsException(final ScheduleParamDto scheduleParamDto) {
    final LocalDate start = getStart(scheduleParamDto);
    final LocalDate end = getEnd(scheduleParamDto);

    if (end.isBefore(start)) {
      throw new NegocioException(SCHEDULE_START_DATE_AFTER_DATE_ERROR);
    }
  }

  private static void setScheduleStartDate(
    final ScheduleParamDto scheduleParamDto,
    final Schedule schedule
  ) {
    schedule.setStart(getStart(scheduleParamDto));
  }

  private static void setScheduleEndDate(
    final ScheduleParamDto scheduleParamDto,
    final Schedule schedule
  ) {
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

    return ChronoUnit.MONTHS.between(
      first,
      second
    ) + 1L;
  }

  private static long getActualWorkMonths(final ScheduleParamDto scheduleParamDto) {
    final LocalDate start = getStart(scheduleParamDto).withDayOfMonth(1);
    final LocalDate end = getEnd(scheduleParamDto).withDayOfMonth(1);
    final LocalDate now = LocalDate.now();

    if (now.isBefore(start)) {
      return 0L;
    }

    if (now.isAfter(end)) {
      return ChronoUnit.MONTHS.between(
        start,
        end
      ) + 1L;
    }

    return ChronoUnit.MONTHS.between(
      start,
      now
    ) + 1L;
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

  private static void addsStepToSteps(
    final Map<Long, ? extends BigDecimal> plannedParts,
    final Map<Long, ? extends BigDecimal> actualParts,
    final Map<CostAccount, MapPair<Long, BigDecimal>> costsMap,
    final Collection<? super Step> steps,
    final long actualWorkMonths,
    final long currentMonth,
    final ScheduleParamDto scheduleParamDto
  ) {
    final Step step = new Step();

    setPlannedWork(
      plannedParts,
      currentMonth,
      scheduleParamDto,
      step
    );
    addsConsumesForEachStep(
      currentMonth,
      actualWorkMonths,
      costsMap,
      step
    );

    step.setPeriodFromStart(currentMonth - 1);
    step.setActualWork(BigDecimal.ZERO);

    if (currentMonth < actualWorkMonths && actualWorkMonths > 0) {
      setActualWork(
        currentMonth,
        scheduleParamDto,
        actualParts,
        step
      );
    }

    steps.add(step);
  }

  private static void setPlannedWork(
    final Map<Long, ? extends BigDecimal> plannedParts,
    final long currentMonth,
    final ScheduleParamDto scheduleParamDto,
    final Step step
  ) {
    final BigDecimal plannedWork = scheduleParamDto.getPlannedWork();
    step.setPlannedWork(BigDecimal.ZERO);
    if (Objects.nonNull(plannedWork)) {
      step.setPlannedWork(plannedParts.get(currentMonth));
    }
  }

  private static void addsConsumesForEachStep(
    final long currentMonth,
    final long actualWorkMonths,
    final Map<CostAccount, MapPair<Long, BigDecimal>> mapCostToParts,
    final Step step
  ) {
    if (mapCostToParts.isEmpty()) {
      return;
    }

    step.setConsumes(new HashSet<>());

    for (final Map.Entry<CostAccount, MapPair<Long, BigDecimal>> entry : mapCostToParts.entrySet()) {
      final CostAccount costAccount = entry.getKey();
      final MapPair<Long, BigDecimal> pair = entry.getValue();
      addsConsumesInStep(
        step,
        currentMonth,
        costAccount,
        pair,
        currentMonth < actualWorkMonths
      );
    }
  }

  private static void setActualWork(
    final long currentMonth,
    final ScheduleParamDto scheduleParamDto,
    final Map<Long, ? extends BigDecimal> actualParts,
    final Step step
  ) {
    final BigDecimal actualWork = scheduleParamDto.getActualWork();
    step.setActualWork(BigDecimal.ZERO);

    if (Objects.nonNull(actualWork)) {
      step.setActualWork(actualParts.get(currentMonth));
    }
  }

  private static void addsConsumesInStep(
    final Step step,
    final long currentMonth,
    final CostAccount costAccount,
    final MapPair<Long, ? extends BigDecimal> pair,
    final boolean includeActualCost
  ) {
    BigDecimal actualCost = BigDecimal.ZERO;

    if (includeActualCost) {
      actualCost = pair.getFirst().get(currentMonth);
    }

    final Consumes consumes = new Consumes(
      null,
      actualCost,
      pair.getSecond().get(currentMonth),
      costAccount,
      step
    );

    step.getConsumes().add(consumes);
  }

  public List<ScheduleDto> findAll(final Long idWorkpack) {
    final List<Schedule> schedules = this.scheduleRepository.findAllByWorkpack(idWorkpack);
    final Set<Long> ids = schedules.stream().map(Schedule::getId).collect(Collectors.toSet());
    final List<ScheduleDto> snapshots = this.scheduleRepository.findSnapshotByMasterIds(new ArrayList<>(ids));
    final Set<Long> idsSnapshots = snapshots.stream().map(ScheduleDto::getIdSnapshot).collect(Collectors.toSet());
    final List<StepDto> stepSnapshot = this.stepRepository.findAllStepsnapshotByScheduleSnapshotIds(new ArrayList<>(idsSnapshots));
    final Set<Long> idsStepSnapshot = stepSnapshot.stream().map(StepDto::getId).collect(Collectors.toSet());
    final List<ConsumesDto> consumesSnapshot = costAccountRepository.findAllConsumesByStepIds(new ArrayList<>(idsStepSnapshot));
    final List<EntityDto> constAccounts = costAccountRepository.findCostAccountByScheduleIds(new ArrayList<>(ids));
    final List<ScheduleDto> list = new ArrayList<>();

    for (final Schedule schedule : schedules) {
      ScheduleDto snapShot = snapshots.stream().filter(s -> schedule.getId().equals(s.getId())).findFirst().orElse(null);
      ScheduleDto scheduleDto = new ScheduleDto(schedule);
      List<StepDto> listStepSnapshot = new ArrayList<>(0);
      if (snapShot != null) {
        scheduleDto.setBaselineStart(snapShot.getBaselineStart());
        scheduleDto.setBaselineEnd(snapShot.getBaselineEnd());
        listStepSnapshot.addAll(
            stepSnapshot.stream().filter(s -> snapShot.getIdSnapshot().equals(s.getIdSchedule())).collect(
                Collectors.toList()));
      }
      scheduleDto.setGroupStep(this.getGroupStep(schedule, listStepSnapshot, consumesSnapshot, constAccounts));
      setValuesBaseline(scheduleDto, listStepSnapshot, consumesSnapshot);
      list.add(scheduleDto);
    }

    return list;
  }

  private void setValuesBaseline(final ScheduleDto scheduleDto, final List<StepDto> listStepSnapshot
      , final List<ConsumesDto> consumesSnapshot) {
    final BigDecimal plannedWork = listStepSnapshot.stream().map(StepDto::getPlannedWork).filter(
        Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    final BigDecimal plannedCost = consumesSnapshot.stream().map(ConsumesDto::getPlannedCost).filter(
        Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    scheduleDto.setBaselinePlaned(plannedWork);
    scheduleDto.setBaselineCost(plannedCost);
  }



  private List<GroupStepDto> getGroupStep(Schedule schedule, List<StepDto> listStepSnapshot
      , final List<ConsumesDto> listConsumesSnapshot, final List<EntityDto> constAccounts) {
    List<GroupStepDto> groupList = new ArrayList<>(0);
    Map<Integer, List<StepDto>> mapGroup = this.getMapGroups(schedule, listStepSnapshot, listConsumesSnapshot, constAccounts);
    if (!mapGroup.isEmpty()) {
      mapGroup.forEach((year, steps) -> {
        GroupStepDto group = new GroupStepDto(year, steps);
        groupList.add(group);
      });
    }
    groupList.forEach(g -> g.getSteps().sort(Comparator.comparing(StepDto::getPeriodFromStart)));
    return groupList.stream().sorted(Comparator.comparing(GroupStepDto::getYear)).collect(Collectors.toList());
  }

  private Map<Integer, List<StepDto>> getMapGroups(Schedule schedule, List<StepDto> listStepSnapshot
      , final List<ConsumesDto> listConsumesSnapshot, final List<EntityDto> constAccounts) {
    Map<Integer, List<StepDto>> mapGroup = new HashMap<>(0);
    if (CollectionUtils.isNotEmpty(schedule.getSteps())) {
      schedule.getSteps().forEach(s -> {
        mapGroup.computeIfAbsent(s.getYear(),  k -> new ArrayList<>());

        StepDto snapshot = listStepSnapshot.stream().filter(
            st -> st.getStepMasterId() != null && st.getStepMasterId().equals(s.getId())).findFirst().orElse(null);

        List<ConsumesDto> consumes = listConsumesSnapshot.stream().filter(
            c -> snapshot != null && c.getStepSnapshotId() != null && c.getStepSnapshotId().equals(
                snapshot.getId())).collect(Collectors.toList());

        mapGroup.get(s.getYear()).add(this.getStepDto(s, snapshot, consumes, constAccounts));
      });
    }
    return mapGroup;
  }

  private StepDto getStepDto(final Step step, StepDto snapshot
      , final List<ConsumesDto> listConsumesSnapshot, final List<EntityDto> constAccounts) {
    StepDto stepDto = new StepDto(step);
    stepDto.setConsumes(this.getConsumes(step, listConsumesSnapshot, constAccounts));
    if (snapshot != null) {
      stepDto.setBaselinePlannedWork(snapshot.getPlannedWork());
      if (snapshot.getPeriodFromStartNumber() != null) {
        stepDto.setBaselinePeriodFromStart(snapshot.getScheduleStart().plusMonths(snapshot.getPeriodFromStartNumber()));
      }
    }
    return stepDto;
  }

  private Set<ConsumesDto> getConsumes(final Step step
      , List<ConsumesDto> listConsumesSnapshot, final List<EntityDto> constAccounts) {
    Set<ConsumesDto> consumes = new LinkedHashSet<>(0);
    if (CollectionUtils.isNotEmpty(step.getConsumes())) {
      step.getConsumes().forEach(c -> {
        ConsumesDto dto = new ConsumesDto(c);

        constAccounts.stream().filter(e -> e.getId().equals(c.getCostAccount().getId())).findFirst().ifPresent(
            entityDto -> dto.getCostAccount().setName(entityDto.getName()));

        listConsumesSnapshot.stream().filter(
            s -> s.getCostAccountMasterId() != null && s.getCostAccountMasterId().equals(
                c.getIdCostAccount())).findFirst().ifPresent(c2 -> dto.setBaselinePlannedCost(c2.getPlannedCost()));

        consumes.add(dto);
      });
    }
    return consumes;
  }

  public ScheduleDto mapsToScheduleDto(final Schedule schedule) {
    final ScheduleDto scheduleDto = this.mapsToScheduleDtoUsingModelMapper(schedule);

    if (Objects.nonNull(schedule.getSteps())) {
      this.setScheduleDtoGroupStep(
        schedule,
        scheduleDto
      );
    }

    setScheduleDtoWorkpack(
      schedule,
      scheduleDto
    );

    return scheduleDto;
  }

  private <T> ScheduleDto mapsToScheduleDtoUsingModelMapper(final T source) {
    return this.modelMapper.map(
      source,
      ScheduleDto.class
    );
  }

  private void setScheduleDtoGroupStep(
    final Schedule schedule,
    final ScheduleDto scheduleDto
  ) {
    final List<GroupStepDto> groupStep = this.getGroupStep(schedule.getSteps());
    scheduleDto.setGroupStep(groupStep);
  }

  private List<GroupStepDto> getGroupStep(final Iterable<? extends Step> steps) {
    final List<GroupStepDto> groups = new ArrayList<>();
    if (Objects.nonNull(steps)) {
      this.addsStepsToGroupSteps(
        steps,
        groups
      );
    }
    return groups;
  }

  private void addsStepsToGroupSteps(
    final Iterable<? extends Step> steps,
    final List<GroupStepDto> groups
  ) {
    final Map<Integer, List<Step>> mapYearToSteps = new HashMap<>();
    addsStepsToMap(
      steps,
      mapYearToSteps
    );
    this.addsStepsFromMapToGroupSteps(
      mapYearToSteps,
      groups
    );
    sortGroupsByYear(groups);
  }

  private void addsStepsFromMapToGroupSteps(
    final Map<Integer, ? extends List<Step>> map,
    final Collection<? super GroupStepDto> groups
  ) {
    for (final Map.Entry<Integer, ? extends List<Step>> entry : map.entrySet()) {
      final Integer year = entry.getKey();
      final List<Step> stepList = entry.getValue();
      this.addsStepToGroupSteps(
        groups,
        year,
        stepList
      );
    }
  }

  private void addsStepToGroupSteps(
    final Collection<? super GroupStepDto> groups,
    final Integer year,
    final Iterable<? extends Step> stepList
  ) {
    final GroupStepDto group = new GroupStepDto();
    final List<StepDto> groupSteps = group.getSteps();

    this.addsStepsToGroupSteps(
      stepList,
      groupSteps
    );
    sortStepsByPeriodFromStart(groupSteps);

    group.setYear(year);
    groups.add(group);
  }

  private void addsStepsToGroupSteps(
    final Iterable<? extends Step> stepList,
    final Collection<? super StepDto> groupSteps
  ) {
    for (final Step step : stepList) {
      this.addsStepToGroupSteps(
        groupSteps,
        step
      );
    }
  }

  private void addsStepToGroupSteps(
    final Collection<? super StepDto> groupSteps,
    final Step step
  ) {
    final StepDto dto = this.mapsToStepDto(step);


    groupSteps.add(dto);
  }

  private StepDto mapsToStepDto(final Step step) {
    final StepDto stepDto = new StepDto();

    stepDto.setId(step.getId());
    final Schedule schedule = step.getSchedule();
    stepDto.setIdSchedule(schedule.getId());
    stepDto.setScheduleStart(schedule.getStart());
    stepDto.setScheduleEnd(schedule.getEnd());
    stepDto.setActualWork(step.getActualWork());
    stepDto.setPlannedWork(step.getPlannedWork());
    stepDto.setPeriodFromStart(step.getPeriodFromStartDate());
    stepDto.setConsumes(new HashSet<>());

    final Set<Consumes> consumesRelationship = Optional.ofNullable(step.getConsumes())
      .orElseGet(HashSet::new);

    for (final Consumes relation : consumesRelationship) {
      final ConsumesDto consumesDto = new ConsumesDto();
      consumesDto.setId(relation.getId());
      consumesDto.setActualCost(relation.getActualCost());
      consumesDto.setPlannedCost(relation.getPlannedCost());
      final Long id = relation.getCostAccount().getId();
      final EntityDto costAccount = new EntityDto(
        id,
        this.costAccountRepository.findCostAccountNameById(id)
      );
      consumesDto.setCostAccount(costAccount);
      stepDto.getConsumes().add(consumesDto);
    }

    final Set<ConsumesDto> sortedConsumes = stepDto.getConsumes().stream()
      .sorted(Comparator.comparing(dto -> dto.getCostAccount().getName()))
      .collect(Collectors.toCollection(LinkedHashSet::new));
    stepDto.setConsumes(sortedConsumes);
    return stepDto;
  }

  @Transactional
  public Schedule save(final ScheduleParamDto scheduleParamDto) {
    this.validatesScheduleParamDto(scheduleParamDto);
    final Schedule schedule = this.mapToSchedule(scheduleParamDto);
    final Schedule savedSchedule = this.scheduleRepository.save(schedule);
    final List<Deliverable> deliverables = this.updateStatusService.getDeliverablesByScheduleId(savedSchedule.getId());
    this.updateStatusService.update(deliverables);
    return savedSchedule;
  }

  private void validatesScheduleParamDto(final ScheduleParamDto scheduleParamDto) {
    this.ifScheduleAlreadyExistsThrowsException(scheduleParamDto);
    ifEndDateIsBeforeStartDateThrowsException(scheduleParamDto);
  }

  private Schedule mapToSchedule(final ScheduleParamDto scheduleParamDto) {
    final Schedule schedule = new Schedule();

    setScheduleStartDate(
      scheduleParamDto,
      schedule
    );
    setScheduleEndDate(
      scheduleParamDto,
      schedule
    );

    this.setScheduleSteps(
      scheduleParamDto,
      schedule
    );
    this.setScheduleWorkpack(
      scheduleParamDto,
      schedule
    );

    return schedule;
  }

  private void ifScheduleAlreadyExistsThrowsException(final ScheduleParamDto scheduleParamDto) {
    if (!this.scheduleRepository.findAllByWorkpack(scheduleParamDto.getIdWorkpack()).isEmpty()) {
      throw scheduleAlreadyExistsException();
    }
  }

  private void setScheduleSteps(
    final ScheduleParamDto scheduleParamDto,
    final Schedule schedule
  ) {
    schedule.setSteps(this.getSteps(scheduleParamDto));
  }

  private void setScheduleWorkpack(
    final ScheduleParamDto scheduleParamDto,
    final Schedule schedule
  ) {
    schedule.setWorkpack(this.getWorkpack(scheduleParamDto));
  }

  private Set<Step> getSteps(final ScheduleParamDto scheduleParamDto) {
    final Set<Step> steps = new HashSet<>();

    final long plannedWorkMonths = getPlannedWorkMonths(scheduleParamDto);
    final long actualWorkMonths = getActualWorkMonths(scheduleParamDto);

    final Map<CostAccount, MapPair<Long, BigDecimal>> costsMap = this.getCostAccountGroupedByDistributedParts.execute(
      plannedWorkMonths,
      actualWorkMonths,
      scheduleParamDto.getDistribution(),
      scheduleParamDto.getCosts()
    );

    final int scale = this.getScale(scheduleParamDto);
    final Map<Long, BigDecimal> plannedParts = getParts(
      plannedWorkMonths,
      scheduleParamDto.getPlannedWork(),
      scale,
      scheduleParamDto.getDistribution()
    );
    final Map<Long, BigDecimal> actualParts = getParts(
      actualWorkMonths,
      scheduleParamDto.getActualWork(),
      scale,
      scheduleParamDto.getDistribution()
    );
    for (long currentMonth = 1; currentMonth <= plannedWorkMonths; currentMonth++) {
      addsStepToSteps(
        plannedParts,
        actualParts,
        costsMap,
        steps,
        actualWorkMonths,
        currentMonth,
        scheduleParamDto
      );
    }

    return steps;
  }

  private int getScale(final ScheduleParamDto scheduleParamDto) {
    return this.getUnitMeasureScaleByWorkpack.execute(scheduleParamDto.getIdWorkpack());
  }

  private Workpack getWorkpack(final ScheduleParamDto scheduleParamDto) {
    return this.findWorkpackById(scheduleParamDto);
  }

  private Workpack findWorkpackById(final ScheduleParamDto scheduleParamDto) {
    return this.workpackService.findById(scheduleParamDto.getIdWorkpack());
  }

  public void delete(final Long idSchedule) {
    final Schedule schedule = this.findById(idSchedule);
    final List<Deliverable> deliverables = this.updateStatusService.getDeliverablesByScheduleId(idSchedule);

    if (this.scheduleRepository.canBeRemoved(idSchedule)) {
      throw new NegocioException(SCHEDULE_HAS_ACTIVE_BASELINE);
    }

    this.stepRepository.deleteAll(schedule.getSteps());
    this.scheduleRepository.delete(schedule);
    this.updateStatusService.update(deliverables);
  }

  public Schedule findById(final Long id) {
    return this.scheduleRepository.findByIdSchedule(id)
      .orElseThrow(() -> new NegocioException(SCHEDULE_NOT_FOUND));
  }

  public Boolean getCurrentBaseline(final Long workpackId) {
    return this.baselineRepository.workpackHasSnapshotOrProjectWithBaseline(workpackId);
  }

}
