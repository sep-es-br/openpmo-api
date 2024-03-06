package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.schedule.ConsumesParamDto;
import br.gov.es.openpmo.dto.schedule.StepUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
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

@Component
public class UpdateStep {

  private final StepRepository stepRepository;

  private final ConsumesRepository consumesRepository;

  private final ScheduleRepository scheduleRepository;

  private final CostAccountService costAccountService;

  private final UpdateStatusService updateStatusService;

  private final ModelMapper modelMapper;

  public UpdateStep(
    final StepRepository stepRepository,
    final ConsumesRepository consumesRepository,
    final ScheduleRepository scheduleRepository,
    final CostAccountService costAccountService,
    final UpdateStatusService updateStatusService,
    final ModelMapper modelMapper
  ) {
    this.stepRepository = stepRepository;
    this.consumesRepository = consumesRepository;
    this.scheduleRepository = scheduleRepository;
    this.costAccountService = costAccountService;
    this.updateStatusService = updateStatusService;
    this.modelMapper = modelMapper;
  }

  public Step execute(final StepUpdateDto stepUpdateDto, Boolean updateScheduleAndDeliverable) {
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
    final Step stepUpdated = this.stepRepository.save(stepUpdate);

    if (!updateScheduleAndDeliverable) {
      return stepUpdated;
    }
    final Schedule schedule = this.findScheduleById(stepUpdate.getSchedule().getId());
    Optional.of(stepUpdateDto).map(StepUpdateDto::getScheduleStart).ifPresent(schedule::setStart);
    Optional.of(stepUpdateDto).map(StepUpdateDto::getScheduleEnd).ifPresent(schedule::setEnd);

    final List<Deliverable> deliverables = this.updateStatusService.getDeliverablesByStepId(step.getId());
    this.updateStatusService.update(deliverables);

    return stepUpdated;
  }

  private Step findById(final Long id) {
    return this.stepRepository.findById(id).orElseThrow(() -> new NegocioException(STEP_NOT_FOUND));
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
