package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.schedule.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StepService {

    private final StepRepository stepRepository;

    private final ScheduleRepository scheduleRepository;

    private final ConsumesRepository consumesRepository;

    private final CostAccountService costAccountService;

    private final ModelMapper modelMapper;

    @Autowired
    public StepService(
            final StepRepository stepRepository,
            final ScheduleRepository scheduleRepository,
            final ConsumesRepository consumesRepository,
            final CostAccountService costAccountService,
            final ModelMapper modelMapper
    ) {
        this.stepRepository = stepRepository;
        this.scheduleRepository = scheduleRepository;
        this.consumesRepository = consumesRepository;
        this.costAccountService = costAccountService;
        this.modelMapper = modelMapper;
    }

    private static long intervalInMonths(final LocalDate start, final LocalDate end) {
        return ChronoUnit.MONTHS.between(start.withDayOfMonth(1), end.withDayOfMonth(1));
    }

    private static boolean isStart(final Step step, final Schedule schedule) {
        return isSameStartYear(step, schedule) && isSameStartMonth(step, schedule);
    }

    private static void setStart(final Schedule schedule, final Long periodFromStart) {
        final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
        schedule.setStart(localDate);
    }

    private static void setEnd(final Schedule schedule, final Long periodFromStart) {
        final LocalDate localDate = schedule.getStart().plusMonths(periodFromStart);
        schedule.setEnd(localDate);
    }

    private static boolean isSameStartYear(final Step step, final Schedule schedule) {
        return step.getPeriodFromStartDate().getYear() == schedule.getStart().getYear();
    }

    private static boolean isSameStartMonth(final Step step, final Schedule schedule) {
        return step.getPeriodFromStartDate().getMonthValue() == schedule.getStart().getMonthValue();
    }

    public void delete(final Long id) {
        final Step step = this.findById(id);
        final Long idSchedule = step.getSchedule().getId();
        final Schedule schedule = this.findScheduleById(idSchedule);

        final List<Step> steps = schedule.getSteps().stream()
                .sorted(Comparator.comparing(Step::getPeriodFromStart))
                .collect(Collectors.toList());

        this.stepRepository.delete(step);

        final boolean start = isStart(step, schedule);

        if (steps.size() > 1) {
            if (start) {
                steps.remove(0);
                setStart(schedule, steps.get(0).getPeriodFromStart());
            } else {
                Collections.reverse(steps);
                steps.remove(0);
                setEnd(schedule, steps.get(0).getPeriodFromStart());
            }
        } else {
            if (start) {
                setStart(schedule, step.getPeriodFromStart() + 1);
            } else {
                setEnd(schedule, step.getPeriodFromStart() - 1);
            }
        }

        this.scheduleRepository.save(schedule);
    }

    public Step findById(final Long id) {
        return this.stepRepository.findById(id).orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_NOT_FOUND));
    }

    public Schedule findScheduleById(final Long id) {
        return this.scheduleRepository.findByIdSchedule(id).orElseThrow(() -> new NegocioException(ApplicationMessage.SCHEDULE_NOT_FOUND));
    }

    public StepDto mapToStepDto(final Step step) {
        final StepDto stepDto = new StepDto();

        stepDto.setId(step.getId());
        Schedule schedule = step.getSchedule();
        stepDto.setIdSchedule(schedule.getId());
        stepDto.setScheduleStart(schedule.getStart());
        stepDto.setScheduleEnd(schedule.getEnd());
        stepDto.setActualWork(step.getActualWork());
        stepDto.setPlannedWork(step.getPlannedWork());
        stepDto.setPeriodFromStart(step.getPeriodFromStartDate());

        final Step snapshotStep = this.stepRepository.findSnapshotOfActiveBaseline(step.getId()).orElse(null);
        stepDto.setBaselinePlannedWork(Optional.ofNullable(snapshotStep).map(Step::getPlannedWork).orElse(null));

        stepDto.setConsumes(new HashSet<>());

        for (Consumes consumes : step.getConsumes()) {
            ConsumesDto consumesDto = new ConsumesDto();
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

        boolean isEndStep = stepStoreParamDto.getEndStep();
        if (!isEndStep) {
            final List<Step> sortedSteps = scheduleSteps.stream()
                    .sorted(Comparator.comparing(Step::getPeriodFromStart))
                    .collect(Collectors.toList());

            sortedSteps.forEach(step -> step.setPeriodFromStart(step.getPeriodFromStart() + months));
            this.stepRepository.saveAll(sortedSteps);
        }

        for (long month = 0; month < months; month++) {
            final Step step = this.mapsToStep(stepStoreParamDto);
            step.setSchedule(schedule);

            if (!stepStoreParamDto.getConsumes().isEmpty()) {
                this.addsConsumesToStep(stepStoreParamDto.getConsumes(), step);
            }

            step.setPeriodFromStart(isEndStep ? scheduleSteps.size() : month);
            scheduleSteps.add(this.stepRepository.save(step));
        }

        this.addsMonthsToSchedule(stepStoreParamDto, schedule);
    }

    private void addsConsumesToStep(final Iterable<? extends ConsumesParamDto> consumes, final Step step) {
        step.setConsumes(new HashSet<>());
        consumes.forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
    }

    private void addsMonthsToSchedule(final StepStoreParamDto stepStoreParamDto, final Schedule schedule) {
        if (stepStoreParamDto.getEndStep()) {
            schedule.setEnd(stepStoreParamDto.getScheduleEnd());
        } else {
            schedule.setStart(stepStoreParamDto.getScheduleStart());
        }
        this.scheduleRepository.save(schedule);
    }

    private long getMonths(final StepStoreParamDto stepStoreParamDto, final Schedule schedule) {
        if (stepStoreParamDto.getEndStep()) {
            LocalDate scheduleEnd = stepStoreParamDto.getScheduleEnd();
            return intervalInMonths(schedule.getEnd(), scheduleEnd);
        }
        LocalDate scheduleStart = stepStoreParamDto.getScheduleStart();
        return intervalInMonths(scheduleStart, schedule.getStart());
    }

    public Step update(final StepUpdateDto stepUpdateDto) {
        final Step step = this.getStepForUpdate(stepUpdateDto);
        final Step stepUpdate = this.findById(step.getId());

        stepUpdate.setActualWork(step.getActualWork());
        stepUpdate.setPlannedWork(step.getPlannedWork());

        if (Objects.nonNull(step.getPeriodFromStart())) {
            stepUpdate.setPeriodFromStart(step.getPeriodFromStart());
        }

        if (!CollectionUtils.isEmpty(stepUpdate.getConsumes())) {
            final Set<Consumes> consumesDelete = stepUpdate.getConsumes().stream()
                    .filter(consumes -> step.getConsumes() == null ||
                            step.getConsumes().stream().noneMatch(c -> Objects.nonNull(c.getId()) && c.getId().equals(consumes.getId())))
                    .collect(Collectors.toSet());

            if (!consumesDelete.isEmpty()) {
                this.consumesRepository.deleteAll(consumesDelete);
            }
        }

        if (!CollectionUtils.isEmpty(step.getConsumes())) {
            for (final Consumes consumes : step.getConsumes()) {
                if (consumes.getId() == null) {
                    if (consumes.getCostAccount() == null || consumes.getCostAccount().getId() == null) {
                        throw new NegocioException(ApplicationMessage.CONSUMES_COST_ACCOUNT_NOT_NULL);
                    }
                    if (!CollectionUtils.isEmpty(stepUpdate.getConsumes()) || stepUpdate.getConsumes().stream()
                            .anyMatch(c -> Objects.nonNull(c.getCostAccount()) && Objects.nonNull(c.getCostAccount().getId())
                                    && c.getCostAccount().getId().equals(consumes.getCostAccount().getId()))) {
                        throw new NegocioException(ApplicationMessage.CONSUMES_COST_ACCOUNT_ALREADY_EXISTS);
                    }
                    final CostAccount costAccount = this.costAccountService.findById(consumes.getCostAccount().getId());
                    if (step.getConsumes() == null) {
                        step.setConsumes(new HashSet<>());
                    }
                    consumes.setCostAccount(costAccount);
                    consumes.setStep(stepUpdate);
                    stepUpdate.getConsumes().add(consumes);
                    continue;
                }
                if (!CollectionUtils.isEmpty(stepUpdate.getConsumes())) {
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

    private boolean isSameEndMonth(Step stepUpdate, Schedule schedule) {
        return stepUpdate.getPeriodFromStartDate().getMonthValue() == schedule.getEnd().getMonthValue();
    }

    private boolean isSameEndYear(Step stepUpdate, Schedule schedule) {
        return stepUpdate.getPeriodFromStartDate().getYear() == schedule.getEnd().getYear();
    }

    public Step getStepForUpdate(final StepUpdateDto stepUpdateDto) {
        final Step step = this.mapsToStep(stepUpdateDto);
        step.getConsumes().removeIf(consumes -> consumes.getId() == null);
        this.addsConsumesToStep(stepUpdateDto, step);
        return step;
    }

    private Step mapsToStep(final StepStoreParamDto in) {
        Step out = new Step();
        out.setActualWork(in.getActualWork());
        out.setPlannedWork(in.getPlannedWork());
        return out;
    }

    private Step mapsToStep(final StepUpdateDto in) {
        return this.modelMapper.map(in, Step.class);
    }

    private void addsConsumesToStep(final StepUpdateDto stepUpdateDto, final Step step) {
        stepUpdateDto.getConsumes().stream()
                .filter(consumesParamDto -> consumesParamDto.getId() == null)
                .forEach(consumesParamDto -> this.addsConsumesToSteps(step, consumesParamDto));
    }

    private void addsConsumesToSteps(final Step step, final ConsumesParamDto consumesParamDto) {
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
