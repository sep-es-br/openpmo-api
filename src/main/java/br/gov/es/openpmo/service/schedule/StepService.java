package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.schedule.ConsumesDto;
import br.gov.es.openpmo.dto.schedule.ConsumesParamDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CONSUMES_COST_ACCOUNT_ALREADY_EXISTS;
import static br.gov.es.openpmo.utils.ApplicationMessage.CONSUMES_COST_ACCOUNT_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.STEP_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.STEP_PLANNED_WORK_CANNOT_BE_NULL_OR_ZERO;

@Service
public class StepService {

  private final StepRepository stepRepository;

  private final ScheduleRepository scheduleRepository;

  private final ConsumesRepository consumesRepository;

  private final CostAccountService costAccountService;
  private final RecalculateStepsAfterRemove recalculateStepsAfterRemove;

  private final ModelMapper modelMapper;

  @Autowired
  public StepService(
    final StepRepository stepRepository,
    final ScheduleRepository scheduleRepository,
    final ConsumesRepository consumesRepository,
    final CostAccountService costAccountService,
    final RecalculateStepsAfterRemove recalculateStepsAfterRemove,
    final ModelMapper modelMapper
  ) {
    this.stepRepository = stepRepository;
    this.scheduleRepository = scheduleRepository;
    this.consumesRepository = consumesRepository;
    this.costAccountService = costAccountService;
    this.recalculateStepsAfterRemove = recalculateStepsAfterRemove;
    this.modelMapper = modelMapper;
  }

  private static long intervalInMonths(
    final LocalDate start,
    final LocalDate end
  ) {
    return ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1));
  }

  private static boolean isStart(
    final Step step,
    final Schedule schedule
  ) {
    return isSameStartYear(step, schedule) && isSameStartMonth(step, schedule);
  }

  private static void setStart(
    final Schedule schedule,
    final Long periodFromStart
  ) {
    final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
    schedule.setStart(localDate);
  }

  private static void setEnd(
    final Schedule schedule,
    final Long periodFromStart
  ) {
    final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
    schedule.setEnd(localDate);
  }

  private static boolean isSameStartYear(
    final Step step,
    final Schedule schedule
  ) {
    return step.getPeriodFromStartDate().getYear() == schedule.getStart().getYear();
  }

  private static boolean isSameStartMonth(
    final Step step,
    final Schedule schedule
  ) {
    return step.getPeriodFromStartDate().getMonthValue() == schedule.getStart().getMonthValue();
  }

  private static void ifPlannedWorkIsZeroOrNullThrowException(final Comparable<? super BigDecimal> plannedWork) {
    if(Objects.isNull(plannedWork) || plannedWork.compareTo(BigDecimal.ZERO) == 0) {
      throw new NegocioException(STEP_PLANNED_WORK_CANNOT_BE_NULL_OR_ZERO);
    }
  }

  @Transactional
  public void delete(final Long id) {
    final Step step = this.findById(id);
    this.stepRepository.delete(step);
    final List<Step> steps = this.recalculateStepsAfterRemove.execute(step);
    this.stepRepository.saveAll(steps);
  }

  public Step findById(final Long id) {
    return this.stepRepository.findById(id).orElseThrow(() -> new NegocioException(STEP_NOT_FOUND));
  }

  public Schedule findScheduleById(final Long id) {
    return this.scheduleRepository.findByIdSchedule(id).orElseThrow(() -> new NegocioException(SCHEDULE_NOT_FOUND));
  }

  public StepDto mapToStepDto(final Step step) {
    final StepDto stepDto = new StepDto();

    stepDto.setId(step.getId());
    final Schedule schedule = step.getSchedule();
    stepDto.setIdSchedule(schedule.getId());
    stepDto.setScheduleStart(schedule.getStart());
    stepDto.setScheduleEnd(schedule.getEnd());
    stepDto.setActualWork(step.getActualWork());
    stepDto.setPlannedWork(step.getPlannedWork());
    stepDto.setPeriodFromStart(step.getPeriodFromStartDate());

    final Step snapshotStep = this.stepRepository.findSnapshotOfActiveBaseline(step.getId()).orElse(null);
    stepDto.setBaselinePlannedWork(Optional.ofNullable(snapshotStep).map(Step::getPlannedWork).orElse(null));

    stepDto.setConsumes(new HashSet<>());

    for(final Consumes consumes : step.getConsumes()) {
      final ConsumesDto consumesDto = new ConsumesDto();
      consumesDto.setId(consumes.getId());
      consumesDto.setActualCost(consumes.getActualCost());
      consumesDto.setPlannedCost(consumes.getPlannedCost());
      consumesDto.setCostAccount(EntityDto.of(consumes.getCostAccount()));

      final BigDecimal baselinePlannedCost = Optional.ofNullable(snapshotStep)
        .map(Step::getConsumes)
        .flatMap(consumesSnapshot -> consumesSnapshot.stream()
          .filter(snapshot -> snapshot.getIdCostAccountMaster().equals(consumes.getIdCostAccount()))
          .map(Consumes::getPlannedCost)
          .findFirst()
        )
        .orElse(null);

      consumesDto.setBaselinePlannedCost(baselinePlannedCost);

      stepDto.getConsumes().add(consumesDto);
    }

    return stepDto;
  }

  public void save(final StepStoreParamDto stepStoreParamDto) {
    final Schedule schedule = this.findScheduleById(stepStoreParamDto.getIdSchedule());
    final Set<Step> scheduleSteps = schedule.getSteps();
    final long months = this.getMonths(stepStoreParamDto, schedule);

    final Set<ConsumesParamDto> consumes = stepStoreParamDto.getConsumes();
    ifPlannedWorkIsZeroOrNullThrowException(stepStoreParamDto.getPlannedWork());
    final boolean isEndStep = stepStoreParamDto.getEndStep();
    if(!isEndStep) {
      final List<Step> sortedSteps = scheduleSteps.stream()
        .sorted(Comparator.comparing(Step::getPeriodFromStart))
        .collect(Collectors.toList());

      long updatedPeriodFromStart = months;
      for(final Step step : sortedSteps) {
        step.setPeriodFromStart(updatedPeriodFromStart);
        updatedPeriodFromStart++;
      }

      this.stepRepository.saveAll(sortedSteps);
    }

    for(long month = 0; month < months; month++) {
      final Step step = this.mapsToStep(stepStoreParamDto);
      step.setSchedule(schedule);

      if(!consumes.isEmpty()) {
        this.addsConsumesToStep(consumes, step);
      }

      step.setPeriodFromStart(isEndStep ? scheduleSteps.size() : month);
      scheduleSteps.add(this.stepRepository.save(step));
    }

    this.addsMonthsToSchedule(stepStoreParamDto, schedule);
  }

  private void addsConsumesToStep(
    final Iterable<? extends ConsumesParamDto> consumes,
    final Step step
  ) {
    step.setConsumes(new HashSet<>());
    consumes.forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
  }

  private void addsMonthsToSchedule(
    final StepStoreParamDto stepStoreParamDto,
    final Schedule schedule
  ) {
    if(stepStoreParamDto.getEndStep()) {
      schedule.setEnd(stepStoreParamDto.getScheduleEnd());
    }
    else {
      schedule.setStart(stepStoreParamDto.getScheduleStart());
    }
    this.scheduleRepository.save(schedule);
  }

  private long getMonths(
    final StepStoreParamDto stepStoreParamDto,
    final Schedule schedule
  ) {
    if(stepStoreParamDto.getEndStep()) {
      final LocalDate scheduleEnd = stepStoreParamDto.getScheduleEnd();
      return intervalInMonths(schedule.getEnd(), scheduleEnd);
    }
    final LocalDate scheduleStart = stepStoreParamDto.getScheduleStart();
    return intervalInMonths(scheduleStart, schedule.getStart());
  }

  public Step update(final StepUpdateDto stepUpdateDto) {
    final Step step = this.getStepForUpdate(stepUpdateDto);
    final Step stepUpdate = this.findById(step.getId());

    ifPlannedWorkIsZeroOrNullThrowException(step.getPlannedWork());

    stepUpdate.setActualWork(step.getActualWork());
    stepUpdate.setPlannedWork(step.getPlannedWork());

    if(Objects.nonNull(step.getPeriodFromStart())) {
      stepUpdate.setPeriodFromStart(step.getPeriodFromStart());
    }

    if(!(stepUpdate.getConsumes()).isEmpty()) {
      final Set<Consumes> consumesDelete = stepUpdate.getConsumes().stream()
        .filter(consumes -> step.getConsumes() == null ||
                            step.getConsumes().stream().noneMatch(c -> Objects.nonNull(c.getId()) && c.getId().equals(consumes.getId())))
        .collect(Collectors.toSet());

      if(!consumesDelete.isEmpty()) {
        this.consumesRepository.deleteAll(consumesDelete);
      }
    }

    if(!(step.getConsumes()).isEmpty()) {
      for(final Consumes consumes : step.getConsumes()) {
        if(consumes.getId() == null) {
          if(consumes.getCostAccount() == null || consumes.getCostAccount().getId() == null) {
            throw new NegocioException(CONSUMES_COST_ACCOUNT_NOT_NULL);
          }
          if(!(stepUpdate.getConsumes()).isEmpty() || stepUpdate.getConsumes().stream()
            .anyMatch(c -> Objects.nonNull(c.getCostAccount()) && Objects.nonNull(c.getCostAccount().getId())
                           && c.getCostAccount().getId().equals(consumes.getCostAccount().getId()))) {
            throw new NegocioException(CONSUMES_COST_ACCOUNT_ALREADY_EXISTS);
          }
          final CostAccount costAccount = this.costAccountService.findById(consumes.getCostAccount().getId());
          if(step.getConsumes() == null) {
            step.setConsumes(new HashSet<>());
          }
          consumes.setCostAccount(costAccount);
          consumes.setStep(stepUpdate);
          stepUpdate.getConsumes().add(consumes);
          continue;
        }
        if(!(stepUpdate.getConsumes()).isEmpty()) {
          final Consumes consumesUpdate = stepUpdate.getConsumes().stream()
            .filter(c -> Objects.nonNull(c.getId()) && c.getId().equals(consumes.getId())).findFirst()
            .orElse(null);
          if(Objects.nonNull(consumesUpdate)) {
            consumesUpdate.setActualCost(consumes.getActualCost());
            consumesUpdate.setPlannedCost(consumes.getPlannedCost());
          }
        }
      }
    }

    final Schedule schedule = this.findScheduleById(stepUpdate.getSchedule().getId());
    Optional.of(stepUpdateDto).map(StepUpdateDto::getScheduleStart).ifPresent(schedule::setStart);
    Optional.of(stepUpdateDto).map(StepUpdateDto::getScheduleEnd).ifPresent(schedule::setEnd);

    return this.stepRepository.save(stepUpdate);
  }

  private boolean isSameEndMonth(
    final Step stepUpdate,
    final Schedule schedule
  ) {
    return stepUpdate.getPeriodFromStartDate().getMonthValue() == schedule.getEnd().getMonthValue();
  }

  private boolean isSameEndYear(
    final Step stepUpdate,
    final Schedule schedule
  ) {
    return stepUpdate.getPeriodFromStartDate().getYear() == schedule.getEnd().getYear();
  }

  public Step getStepForUpdate(final StepUpdateDto stepUpdateDto) {
    final Step step = this.mapsToStep(stepUpdateDto);
    step.getConsumes().removeIf(consumes -> consumes.getId() == null);
    this.addsConsumesToStep(stepUpdateDto, step);
    return step;
  }

  private Step mapsToStep(final StepStoreParamDto in) {
    final Step out = new Step();
    out.setActualWork(in.getActualWork());
    out.setPlannedWork(in.getPlannedWork());
    return out;
  }

  private Step mapsToStep(final StepUpdateDto in) {
    return this.modelMapper.map(in, Step.class);
  }

  private void addsConsumesToStep(
    final StepUpdateDto stepUpdateDto,
    final Step step
  ) {
    stepUpdateDto.getConsumes().stream()
      .filter(consumesParamDto -> consumesParamDto.getId() == null)
      .forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
  }

  private void addsConsumesToSteps(
    final Step step,
    final ConsumesParamDto consumesParamDto
  ) {
    final CostAccount costAccount = this.costAccountService.findById(consumesParamDto.getIdCostAccount());

    final Consumes consumes = new Consumes(
      null,
      consumesParamDto.getActualCost(),
      consumesParamDto.getPlannedCost(),
      costAccount,
      step
    );

    step.getConsumes().add(consumes);
  }

}
