package br.gov.es.openpmo.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.gov.es.openpmo.dto.schedule.CostSchedule;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.dto.schedule.StepParamDto;
import br.gov.es.openpmo.dto.schedule.StepStoreParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Schedule;
import br.gov.es.openpmo.model.Step;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final StepRepository stepRepository;
    private final CostAccountService costAccountService;
    private final WorkpackService workpackService;
    private final ConsumesRepository consumesRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository, StepRepository stepRepository,
            CostAccountService costAccountService, WorkpackService workpackService,
            ConsumesRepository consumesRepository,
                           ModelMapper modelMapper) {
        this.scheduleRepository = scheduleRepository;
        this.stepRepository = stepRepository;
        this.costAccountService = costAccountService;
        this.workpackService = workpackService;
        this.consumesRepository = consumesRepository;
        this.modelMapper = modelMapper;
    }

    public List<Schedule> findAll(Long idWorkpack) {
        return new ArrayList<>(scheduleRepository.findAllByWorkpack(idWorkpack));
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Step save(Step step) {
        return stepRepository.save(step);
    }

    public Step update(Step step) {
        Step stepUpdate = stepRepository.findById(step.getId())
                .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_NOT_FOUND));
        stepUpdate.setActualWork(step.getActualWork());
        stepUpdate.setPlannedWork(step.getPlannedWork());
        if (!CollectionUtils.isEmpty(stepUpdate.getConsumes())) {
            Set<Consumes> consumesDelete = stepUpdate.getConsumes().stream()
                    .filter(consumes -> step.getConsumes() == null || step.getConsumes().stream()
                            .noneMatch(c -> c.getId() != null && c.getId().equals(consumes.getId())))
                    .collect(Collectors.toSet());
            if (!consumesDelete.isEmpty()) {
                consumesRepository.deleteAll(consumesDelete);
            }
        }
        if (!CollectionUtils.isEmpty(step.getConsumes())) {
            for (Consumes consumes : step.getConsumes()) {
                if (consumes.getId() == null) {
                    if (consumes.getCostAccount() == null || consumes.getCostAccount().getId() == null) {
                        throw new NegocioException(ApplicationMessage.CONSUMES_COSTACCOUNT_NOT_NULL);
                    }
                    if (!CollectionUtils.isEmpty(stepUpdate.getConsumes()) || stepUpdate.getConsumes().stream()
                            .anyMatch(c -> c.getCostAccount() != null && c.getCostAccount().getId() != null
                                    && c.getCostAccount().getId().equals(consumes.getCostAccount().getId()))) {
                        throw new NegocioException(ApplicationMessage.CONSUMES_COSTACCOUNT_ALREADY_EXISTS);
                    }
                    CostAccount costAccount = costAccountService.findById(consumes.getCostAccount().getId());
                    if (step.getConsumes() == null) {
                        step.setConsumes(new HashSet<>());
                    }
                    consumes.setCostAccount(costAccount);
                    consumes.setStep(stepUpdate);
                    stepUpdate.getConsumes().add(consumes);
                    continue;
                }
                if (!CollectionUtils.isEmpty(stepUpdate.getConsumes())) {
                    Consumes consumesUpdate = stepUpdate.getConsumes().stream()
                            .filter(c -> c.getId() != null && c.getId().equals(consumes.getId())).findFirst()
                            .orElse(null);
                    if (consumesUpdate != null) {
                        consumesUpdate.setActualCost(consumes.getActualCost());
                        consumesUpdate.setPlannedCost(consumes.getPlannedCost());
                    }

                }
            }

        }
        return stepRepository.save(stepUpdate);
    }

    public Schedule findById(Long id) {
        return scheduleRepository.findByIdSchedule(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.SCHEDULE_NOT_FOUND));
    }

    public Step findStepById(Long id) {
        return stepRepository.findById(id)
                                 .orElseThrow(() -> new NegocioException(ApplicationMessage.STEP_NOT_FOUND));
    }

    public void delete(Schedule schedule) {
        if (!CollectionUtils.isEmpty(schedule.getSteps())) {
            stepRepository.deleteAll(schedule.getSteps());
        }
        scheduleRepository.delete(schedule);
    }

    public void delete(Step step) {
        Schedule schedule = findById(step.getSchedule().getId());
        LocalDate periodFromStart = step.getPeriodFromStart();
        stepRepository.delete(step);
        if (periodFromStart.equals(schedule.getStart())) {
            schedule.setStart(periodFromStart.plusMonths(1));
        } else if (periodFromStart.equals(schedule.getEnd())) {
            schedule.setEnd(periodFromStart.minusMonths(1));
        }
        scheduleRepository.save(schedule);
    }

    public Schedule getSchedule(ScheduleParamDto scheduleParamDto) {
        if (scheduleParamDto.getEnd().isBefore(scheduleParamDto.getStart())) {
            throw new NegocioException(ApplicationMessage.SCHEDULE_START_DATE_AFTER_DATE_ERROR);
        }
        if (!scheduleRepository.findAllByWorkpack(scheduleParamDto.getIdWorkpack()).isEmpty()) {
            throw new NegocioException(ApplicationMessage.SCHEDULE_ALREADY_EXISTS);
        }
        Schedule schedule = new Schedule();
        schedule.setStart(scheduleParamDto.getStart());
        schedule.setEnd(scheduleParamDto.getEnd());
        schedule.setSteps(getSteps(scheduleParamDto));
        schedule.setWorkpack(workpackService.findById(scheduleParamDto.getIdWorkpack()));

        return schedule;
    }

    private Set<Step> getSteps(ScheduleParamDto scheduleParamDto) {
        Set<Step> steps = new HashSet<>();
        long months = ChronoUnit.MONTHS.between(scheduleParamDto.getStart(), scheduleParamDto.getEnd()) + 1;
        BigDecimal parts = BigDecimal.ZERO;
        if (scheduleParamDto.getPlannedWork() != null) {
            parts = scheduleParamDto.getPlannedWork().divide(new BigDecimal(months),
                    new MathContext(4, RoundingMode.HALF_EVEN));
        }
        Map<CostAccount, BigDecimal> costs = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleParamDto.getCosts())) {
            for (CostSchedule costSchedule : scheduleParamDto.getCosts()) {
                CostAccount costAccount = costAccountService.findById(costSchedule.getId());
                BigDecimal part = costSchedule.getPlannedCost().divide(new BigDecimal(months),
                        new MathContext(4, RoundingMode.HALF_EVEN));
                costs.put(costAccount, part);
            }
        }
        for (int i = 1; i <= months; i++) {
            Step step = new Step();
            step.setPeriodFromStart(
                    i == 1 ? scheduleParamDto.getStart() : scheduleParamDto.getStart().plusMonths(i - 1L));
            step.setActualWork(BigDecimal.ZERO);
            step.setPlannedWork(parts);
            if (!costs.isEmpty()) {
                step.setConsumes(new HashSet<>());
                costs.keySet().forEach(costAccount -> {
                    Consumes consumes = new Consumes(null, BigDecimal.ZERO, costs.get(costAccount), costAccount, step);
                    step.getConsumes().add(consumes);
                });
            }
            steps.add(step);
        }
        return steps;
    }

    public Step getStep(StepStoreParamDto stepStoreParamDto) {
        Step step = modelMapper.map(stepStoreParamDto, Step.class);
        Schedule schedule = findById(stepStoreParamDto.getIdSchedule());
        step.setSchedule(schedule);
        step.setPeriodFromStart(getPeriodFromStart(schedule, stepStoreParamDto.getEndStep()));
        if (!CollectionUtils.isEmpty(stepStoreParamDto.getConsumes())) {
            step.setConsumes(new HashSet<>());
            stepStoreParamDto.getConsumes().forEach(c -> {
                CostAccount costAccount = costAccountService.findById(c.getIdCostAccount());
                Consumes consumes = new Consumes(null, c.getActualCost(), c.getPlannedCost(), costAccount, step);
                step.getConsumes().add(consumes);
            });
        }
        return step;
    }

    private LocalDate getPeriodFromStart(Schedule schedule, boolean endStep) {
        if (endStep) {
            Step last = schedule.getSteps().stream().max(Comparator.comparing(Step::getPeriodFromStart)).orElse(null);
            if (last != null && last.getPeriodFromStart() != null) {
                return last.getPeriodFromStart().plusMonths(1);
            }
        }
        Step first = schedule.getSteps().stream().min(Comparator.comparing(Step::getPeriodFromStart)).orElse(null);
        if (first != null && first.getPeriodFromStart() != null) {
            return first.getPeriodFromStart().minusMonths(1);
        }
        return null;
    }

    public Step getStep(StepParamDto stepParamDto) {
        Step step = modelMapper.map(stepParamDto, Step.class);
        if (CollectionUtils.isEmpty(stepParamDto.getConsumes())) {
            step.getConsumes().removeIf(c -> c.getId() == null);
            stepParamDto.getConsumes().stream().filter(c -> c.getId() == null).forEach(c -> {
                CostAccount costAccount = costAccountService.findById(c.getIdCostAccount());
                Consumes consumes = new Consumes(null, c.getActualCost(), c.getPlannedCost(), costAccount, step);
                step.getConsumes().add(consumes);
            });
        }
        return step;
    }

    public void addMonthToSchedule(Step step) {
        Schedule schedule = findById(step.getSchedule().getId());
        LocalDate periodFromStart = step.getPeriodFromStart();
        if (periodFromStart.equals(schedule.getStart())) {
            schedule.setStart(periodFromStart.plusMonths(1));
        } else if (periodFromStart.equals(schedule.getEnd())) {
            schedule.setEnd(periodFromStart.minusMonths(1));
        }
        scheduleRepository.save(schedule);
    }
}
