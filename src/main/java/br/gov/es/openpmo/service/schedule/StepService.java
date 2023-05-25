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
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
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

  @Transactional
  public void delete(final Long id) {
    final Step step = this.findById(id);
    final Schedule schedule = this.findScheduleById(step.getScheduleId());

    this.stepRepository.delete(step);

    final boolean isLastStep = schedule.getSteps().size() - 1 == 0;
    if (isLastStep) {
      final Long activeBaselineId = this.scheduleRepository.findActiveBaseline(schedule.getId());
      if (activeBaselineId != null) {
        throw new NegocioException(ApplicationMessage.SCHEDULE_HAS_ACTIVE_BASELINE);
      }
      this.scheduleRepository.delete(schedule);
      return;
    }

    final List<Step> steps = this.recalculateStepsAfterRemove.execute(step);
    this.stepRepository.saveAll(steps);
  }

  public Step findById(final Long id) {
    return this.stepRepository.findById(id)
      .orElseThrow(() -> new NegocioException(STEP_NOT_FOUND));
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

    for (final Consumes consumes : step.getConsumes()) {
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

    final boolean isEndStep = stepStoreParamDto.getEndStep();
    if (!isEndStep) {
      final List<Step> sortedSteps = scheduleSteps.stream()
        .sorted(Comparator.comparing(Step::getPeriodFromStart))
        .collect(Collectors.toList());

      long updatedPeriodFromStart = months;
      for (final Step step : sortedSteps) {
        step.setPeriodFromStart(updatedPeriodFromStart);
        updatedPeriodFromStart++;
      }

      this.stepRepository.saveAll(sortedSteps);
    }

    for (long month = 0; month < months; month++) {
      final Step step = this.mapsToStep(stepStoreParamDto);
      step.setSchedule(schedule);

      if (!consumes.isEmpty()) {
        this.addsConsumesToStep(consumes, step);
      }

      step.setPeriodFromStart(isEndStep ? scheduleSteps.size() : month);
      scheduleSteps.add(this.stepRepository.save(step));
    }

    this.addsMonthsToSchedule(stepStoreParamDto, schedule);
  }

  /**
   * Implementação movida para a classe {@link UpdateStep}, caso haja necessidade de novas alterações nesse método
   * utilizar implementação {@link UpdateStep} e alterar o {@link br.gov.es.openpmo.controller.schedule.StepController}
   */
  @Deprecated
  public Step update(final StepUpdateDto stepUpdateDto) {
    final Step step = this.getStepForUpdate(stepUpdateDto);
    final Step stepUpdate = this.findById(step.getId());

    stepUpdate.setActualWork(
      Optional.ofNullable(step.getActualWork())
        .orElse(BigDecimal.ZERO)
    );
    stepUpdate.setPlannedWork(
      Optional.ofNullable(step.getPlannedWork())
        .orElse(BigDecimal.ZERO)
    );

    if (Objects.nonNull(step.getPeriodFromStart())) {
      stepUpdate.setPeriodFromStart(step.getPeriodFromStart());
    }

    if (stepUpdate.getConsumes() != null && !(stepUpdate.getConsumes()).isEmpty()) {
      final Set<Consumes> consumesDelete = stepUpdate.getConsumes().stream()
        .filter(consumes ->
                  step.getConsumes() == null ||
                  step.getConsumes().stream().noneMatch(c -> Objects.nonNull(c.getId()) && c.getId().equals(consumes.getId()))
        ).collect(Collectors.toSet());

      if (!consumesDelete.isEmpty()) {
        this.consumesRepository.deleteAll(consumesDelete);
      }
    }

    if (!CollectionUtils.isEmpty(step.getConsumes())) {
      for (final Consumes consumes : step.getConsumes()) {
        if (consumes.getId() == null) {
          if (Objects.isNull(consumes.getIdCostAccount())) {
            throw new NegocioException(CONSUMES_COST_ACCOUNT_NOT_NULL);
          }

          if (costAccountConsumesAlreadyExists(consumes, stepUpdate.getConsumes())) {
            throw new NegocioException(CONSUMES_COST_ACCOUNT_ALREADY_EXISTS);
          }

          final CostAccount costAccount = this.costAccountService.findById(consumes.getIdCostAccount());
          if (step.getConsumes() == null) {
            step.setConsumes(new HashSet<>());
          }
          consumes.setCostAccount(costAccount);
          consumes.setStep(stepUpdate);
          stepUpdate.getConsumes().add(consumes);
          continue;
        }
        if (stepUpdate.getConsumes() != null && !(stepUpdate.getConsumes()).isEmpty()) {
          final Consumes consumesUpdate = stepUpdate.getConsumes().stream()
            .filter(c -> Objects.nonNull(c.getId()) && c.getId().equals(consumes.getId())).findFirst()
            .orElse(null);
          if (Objects.nonNull(consumesUpdate)) {
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

  private Schedule findScheduleById(final Long id) {
    return this.scheduleRepository.findByIdSchedule(id).orElseThrow(() -> new NegocioException(SCHEDULE_NOT_FOUND));
  }

  private Step getStepForUpdate(final StepUpdateDto stepUpdateDto) {
    final Step step = this.mapsToStep(stepUpdateDto);
    step.getConsumes().removeIf(consumes -> consumes.getId() == null);
    this.addsConsumesToStep(stepUpdateDto, step);
    return step;
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
    if (stepStoreParamDto.getEndStep()) {
      schedule.setEnd(stepStoreParamDto.getScheduleEnd());
    } else {
      schedule.setStart(stepStoreParamDto.getScheduleStart());
    }
    this.scheduleRepository.save(schedule);
  }

  private long getMonths(
    final StepStoreParamDto stepStoreParamDto,
    final Schedule schedule
  ) {
    if (stepStoreParamDto.getEndStep()) {
      final LocalDate scheduleEnd = stepStoreParamDto.getScheduleEnd();
      return intervalInMonths(schedule.getEnd(), scheduleEnd);
    }
    final LocalDate scheduleStart = stepStoreParamDto.getScheduleStart();
    return intervalInMonths(scheduleStart, schedule.getStart());
  }

  private Step mapsToStep(final StepStoreParamDto in) {
    final Step out = new Step();
    out.setActualWork(Optional.ofNullable(in.getActualWork()).orElse(BigDecimal.ZERO));
    out.setPlannedWork(Optional.ofNullable(in.getPlannedWork()).orElse(BigDecimal.ZERO));
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

  private static long intervalInMonths(
    final LocalDate start,
    final LocalDate end
  ) {
    return ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1));
  }

  private static boolean costAccountConsumesAlreadyExists(
    final Consumes consumes,
    final Collection<? extends Consumes> existingConsumes
  ) {
    if (CollectionUtils.isEmpty(existingConsumes)) {
      return false;
    }
    return existingConsumes.stream()
      .filter(c -> Objects.nonNull(c.getIdCostAccount()))
      .anyMatch(c -> c.getIdCostAccount().equals(consumes.getIdCostAccount()));
  }

}
