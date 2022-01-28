package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.schedule.ConsumesParamDto;
import br.gov.es.openpmo.dto.schedule.StepDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
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
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StepService {

  private final StepRepository stepRepository;
  private final ScheduleRepository scheduleRepository;
  private final ConsumesRepository consumesRepository;

  private final ModelMapper modelMapper;
  private final CostAccountService costAccountService;

  @Autowired
  public StepService(
    final StepRepository stepRepository,
    final ScheduleRepository scheduleRepository,
    final ConsumesRepository consumesRepository,
    final ModelMapper modelMapper,
    final CostAccountService costAccountService
  ) {
    this.stepRepository = stepRepository;
    this.scheduleRepository = scheduleRepository;
    this.consumesRepository = consumesRepository;
    this.modelMapper = modelMapper;
    this.costAccountService = costAccountService;
  }

  private static long intervalInMonths(final Temporal start, final Temporal end) {
    return ChronoUnit.MONTHS.between(start, end) + 1L;
  }

  private static <T> boolean isEmpty(final Collection<T> consumes) {
    return consumes.isEmpty();
  }

  private static Set<ConsumesParamDto> getConsumes(final StepStoreParamDto stepStoreParamDto) {
    return stepStoreParamDto.getConsumes();
  }

  private static Long getIdSchedule(final StepStoreParamDto stepStoreParamDto) {
    return stepStoreParamDto.getIdSchedule();
  }

  private static void setScheduleToStep(final Schedule schedule, final Step step) {
    step.setSchedule(schedule);
  }

  private static void setPeriodFromStart(final StepStoreParamDto stepStoreParamDto, final int month, final Step step) {
    step.setPeriodFromStart(isEndStep(stepStoreParamDto)
                              ? getPeriodFromStart(stepStoreParamDto).minusMonths(month)
                              : getPeriodFromStart(stepStoreParamDto).plusMonths(month));
  }

  private static boolean isEndStep(final StepStoreParamDto stepStoreParamDto) {
    return is(stepStoreParamDto.getEndStep());
  }

  private static LocalDate getPeriodFromStart(final StepStoreParamDto stepStoreParamDto) {
    return stepStoreParamDto.getPeriodFromStart();
  }

  private static boolean is(final Boolean endStep) {
    return Boolean.TRUE.equals(endStep);
  }

  public void delete(final Long id) {
    final Step step = this.findById(id);
    final Long idSchedule = getIdSchedule(getSchedule(step));
    final Schedule schedule = this.findScheduleById(idSchedule);

    final List<Step> steps = getScheduleSteps(schedule).stream().sorted(Comparator.comparing(Step::getPeriodFromStart)).collect(
      Collectors.toList());

    this.delete(step);

    final boolean start = isStart(step, schedule);

    if(steps.size() > 1) {
      if(start) {
        steps.remove(0);
        setStart(schedule, getPeriodFromStart(steps.get(0)));
      }
      else {
        Collections.reverse(steps);
        steps.remove(0);
        setEnd(schedule, getPeriodFromStart(steps.get(0)));
      }
    }
    else {
      if(start) {
        setStart(schedule, getPeriodFromStart(step).plusMonths(1L));
      }
      else {
        setEnd(schedule, getPeriodFromStart(step).minusMonths(1L));
      }
    }

    this.saveSchedule(schedule);
  }

  public Step findById(final Long id) {
    return this.stepRepository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_NOT_FOUND));
  }

  private static Long getIdSchedule(final Schedule schedule) {
    return schedule.getId();
  }

  private static Schedule getSchedule(final Step step) {
    return step.getSchedule();
  }

  public Schedule findScheduleById(final Long id) {
    return this.scheduleRepository.findByIdSchedule(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.SCHEDULE_NOT_FOUND));
  }

  private static Set<Step> getScheduleSteps(final Schedule schedule) {
    return schedule.getSteps();
  }

  private void delete(final Step step) {
    this.stepRepository.delete(step);
  }

  private static boolean isStart(final Step step, final Schedule schedule) {
    return isSameStartYear(step, schedule) && isSameStartMonth(step, schedule);
  }

  private static void setStart(final Schedule schedule, final LocalDate periodFromStart) {
    schedule.setStart(periodFromStart);
  }

  private static LocalDate getPeriodFromStart(final Step step) {
    return step.getPeriodFromStart();
  }

  private static void setEnd(final Schedule schedule, final LocalDate periodFromStart) {
    schedule.setEnd(periodFromStart);
  }

  private void saveSchedule(final Schedule schedule) {
    this.scheduleRepository.save(schedule);
  }

  private static boolean isSameStartYear(final Step step, final Schedule schedule) {
    return getPeriodFromStart(step).getYear() == getStart(schedule).getYear();
  }

  private static boolean isSameStartMonth(final Step step, final Schedule schedule) {
    return getPeriodFromStart(step).getMonthValue() == getStart(schedule).getMonthValue();
  }

  private static LocalDate getStart(final Schedule schedule) {
    return schedule.getStart();
  }

  public StepDto mapToStepDto(final Step step) {
    final StepDto stepDto = this.mapsToStepDto(step);
    setSchedule(step, stepDto);
    return stepDto;
  }

  private <T> StepDto mapsToStepDto(final T source) {
    return this.modelMapper.map(source, StepDto.class);
  }

  private static void setSchedule(final Step step, final StepDto stepDto) {
    final Schedule schedule = getSchedule(step);
    setIdSchedule(stepDto, getIdSchedule(schedule));
  }

  private static void setIdSchedule(final StepDto stepDto, final Long idSchedule) {
    stepDto.setIdSchedule(idSchedule);
  }

  public void deleteAll(final Iterable<? extends Step> steps) {
    this.stepRepository.deleteAll(steps);
  }

  public void save(final StepStoreParamDto stepStoreParamDto) {
    final long months = this.getMonths(stepStoreParamDto);

    final Schedule schedule = this.findScheduleById(stepStoreParamDto);

    for(int month = 0; month < months; month++) {
      final Step step = this.mapsToStep(stepStoreParamDto);

      setScheduleToStep(schedule, step);
      setPeriodFromStart(stepStoreParamDto, month, step);

      if(!isEmpty(getConsumes(stepStoreParamDto))) {
        this.addsConsumesToStep(getConsumes(stepStoreParamDto), step);
      }

      getScheduleSteps(schedule).add(this.save(step));
    }

    this.addsMonthsToSchedule(stepStoreParamDto, schedule);
  }

  private Schedule findScheduleById(final StepStoreParamDto stepStoreParamDto) {
    return this.findScheduleById(getIdSchedule(stepStoreParamDto));
  }

  private void addsConsumesToStep(final Iterable<? extends ConsumesParamDto> consumes, final Step step) {
    step.setConsumes(new HashSet<>());
    consumes.forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
  }

  private void addsMonthsToSchedule(final StepStoreParamDto stepStoreParamDto, final Schedule schedule) {
    if(isEndStep(stepStoreParamDto)) {
      setEnd(schedule, getPeriodFromStart(stepStoreParamDto));
    }
    else {
      setStart(schedule, getPeriodFromStart(stepStoreParamDto));
    }

    this.saveSchedule(schedule);
  }

  private long getMonths(final StepStoreParamDto stepStoreParamDto) {
    final Schedule schedule = this.findScheduleById(stepStoreParamDto);

    if(isEndStep(stepStoreParamDto)) {
      return intervalInMonths(getEnd(schedule), getPeriodFromStart(stepStoreParamDto));
    }

    return intervalInMonths(getPeriodFromStart(stepStoreParamDto), getStart(schedule));
  }

  public Step update(final StepParamDto stepParamDto) {
    final Step step = this.getStepForUpdate(stepParamDto);
    final Step stepUpdate = this.findById(step.getId());

    stepUpdate.setActualWork(step.getActualWork());
    stepUpdate.setPlannedWork(step.getPlannedWork());

    if(isNonNun(getPeriodFromStart(step))) {
      stepUpdate.setPeriodFromStart(getPeriodFromStart(step));
    }

    if(!CollectionUtils.isEmpty(getConsumes(stepUpdate))) {
      final Set<Consumes> consumesDelete = getConsumes(stepUpdate).stream()
        .filter(consumes -> getConsumes(step) == null || getConsumes(step).stream().noneMatch(c -> isNonNun(c.getId()) && c.getId().equals(
          consumes.getId())))
        .collect(Collectors.toSet());
      if(!consumesDelete.isEmpty()) {
        this.deleteAllConsumes(consumesDelete);
      }
    }

    if(!CollectionUtils.isEmpty(getConsumes(step))) {
      for(final Consumes consumes : getConsumes(step)) {
        if(consumes.getId() == null) {
          if(consumes.getCostAccount() == null || consumes.getCostAccount().getId() == null) {
            throw new NegocioException(ApplicationMessage.CONSUMES_COST_ACCOUNT_NOT_NULL);
          }
          if(!CollectionUtils.isEmpty(getConsumes(stepUpdate)) || getConsumes(stepUpdate).stream()
            .anyMatch(c -> isNonNun(c.getCostAccount()) && isNonNun(c.getCostAccount().getId())
                           && c.getCostAccount().getId().equals(consumes.getCostAccount().getId()))) {
            throw new NegocioException(ApplicationMessage.CONSUMES_COST_ACCOUNT_ALREADY_EXISTS);
          }
          final CostAccount costAccount = this.findCostAccountById(consumes.getCostAccount().getId());
          if(getConsumes(step) == null) {
            step.setConsumes(new HashSet<>());
          }
          consumes.setCostAccount(costAccount);
          consumes.setStep(stepUpdate);
          getConsumes(stepUpdate).add(consumes);
          continue;
        }
        if(!CollectionUtils.isEmpty(getConsumes(stepUpdate))) {
          final Consumes consumesUpdate = getConsumes(stepUpdate).stream()
            .filter(c -> isNonNun(c.getId()) && c.getId().equals(consumes.getId())).findFirst()
            .orElse(null);
          if(isNonNun(consumesUpdate)) {
            consumesUpdate.setActualCost(consumes.getActualCost());
            consumesUpdate.setPlannedCost(consumes.getPlannedCost());
          }
        }
      }
    }

    if(isNonNun(getPeriodFromStart(stepUpdate))) {
      final Schedule schedule = this.findScheduleById(stepUpdate.getSchedule().getId());

      if(isSameStartYear(stepUpdate, schedule) && isSameStartMonth(stepUpdate, schedule) && !getPeriodFromStart(stepUpdate).equals(
        getStart(schedule))) {
        setScheduleStart(schedule, stepUpdate);
        this.saveSchedule(schedule);
      }

      if(getPeriodFromStart(stepUpdate).getYear() == getEnd(schedule).getYear() && getPeriodFromStart(stepUpdate).getMonthValue() == getEnd(
        schedule).getMonthValue() && !getPeriodFromStart(stepUpdate).equals(getEnd(schedule))) {
        setScheduleEnd(schedule, stepUpdate);
        this.saveSchedule(schedule);
      }
    }

    return this.save(stepUpdate);
  }

  public Step getStepForUpdate(final StepParamDto stepParamDto) {
    final Step step = this.mapsToStep(stepParamDto);

    removesAllConsumesWhichIdIsNull(getConsumes(step));
    this.addsConsumesToStep(stepParamDto, step);

    return step;
  }

  private static <T> boolean isNonNun(final T obj) {
    return Objects.nonNull(obj);
  }

  private static Set<Consumes> getConsumes(final Step step) {
    return step.getConsumes();
  }

  private void deleteAllConsumes(final Iterable<? extends Consumes> consumesDelete) {
    this.consumesRepository.deleteAll(consumesDelete);
  }

  private CostAccount findCostAccountById(final Long idCostAccount) {
    return this.costAccountService.findById(idCostAccount);
  }

  private static void setScheduleStart(final Schedule schedule, final Step stepUpdate) {
    schedule.setStart(getPeriodFromStart(stepUpdate));
  }

  private static LocalDate getEnd(final Schedule schedule) {
    return schedule.getEnd();
  }

  private static void setScheduleEnd(final Schedule schedule, final Step stepUpdate) {
    schedule.setEnd(getPeriodFromStart(stepUpdate));
  }

  public Step save(final Step step) {
    return this.stepRepository.save(step);
  }

  private <T> Step mapsToStep(final T source) {
    return this.modelMapper.map(source, Step.class);
  }

  private static void removesAllConsumesWhichIdIsNull(final Collection<? extends Consumes> consumesSet) {
    consumesSet.removeIf(consumes -> consumes.getId() == null);
  }

  private void addsConsumesToStep(final StepParamDto stepParamDto, final Step step) {
    stepParamDto.getConsumes()
      .stream()
      .filter(StepService::isIdNull)
      .forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
  }

  private static boolean isIdNull(final ConsumesParamDto consumesParamDto) {
    return consumesParamDto.getId() == null;
  }

  private void addsConsumesToSteps(final Step step, final ConsumesParamDto consumesParamDto) {
    final CostAccount costAccount = this.findCostAccountById(consumesParamDto);

    final Consumes consumes = new Consumes(
      null,
      getActualCost(consumesParamDto),
      getPlannedCost(consumesParamDto),
      costAccount,
      step
    );

    getConsumes(step).add(consumes);
  }

  private CostAccount findCostAccountById(final ConsumesParamDto consumesParamDto) {
    return this.findCostAccountById(consumesParamDto.getIdCostAccount());
  }

  private static BigDecimal getActualCost(final ConsumesParamDto consumesParamDto) {
    return consumesParamDto.getActualCost();
  }

  private static BigDecimal getPlannedCost(final ConsumesParamDto consumesParamDto) {
    return consumesParamDto.getPlannedCost();
  }

}
