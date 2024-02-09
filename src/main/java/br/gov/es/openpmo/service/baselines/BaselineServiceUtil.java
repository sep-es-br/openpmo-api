package br.gov.es.openpmo.service.baselines;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;

@Service
public class BaselineServiceUtil {

    private final WorkpackRepository workpackRepository;
    private final ScheduleRepository scheduleRepository;
    private final StepRepository stepRepository;
    private final CostAccountRepository costAccountRepository;
    private final ConsumesRepository consumesRepository;

    public BaselineServiceUtil(
        final WorkpackRepository workpackRepository,
        final ScheduleRepository scheduleRepository,
        final CostAccountRepository costAccountRepository,
        final StepRepository stepRepository,
        final ConsumesRepository consumesRepository
    ) {
        this.workpackRepository = workpackRepository;
        this.scheduleRepository = scheduleRepository;
        this.costAccountRepository = costAccountRepository;
        this.stepRepository = stepRepository;
        this.consumesRepository = consumesRepository;
    }

    private boolean isChanged(BaselineWorkpackDto principal, BaselineWorkpackDto compare) {
        return principal.isDateChanged(compare) || principal.isScheduleChanged(compare) || principal.isStepChanged(compare) || principal.isConsumesChanged(compare) ;
    }

    public List<BaselineWorkpackDto> compare(List<BaselineWorkpackDto> listParam, List<BaselineWorkpackDto> listCompare) {
        List<BaselineWorkpackDto> list = new ArrayList<>(listParam);
        for (BaselineWorkpackDto principal : list) {
            BaselineWorkpackDto compare = listCompare.stream().filter(w -> w.getIdMaster().equals(principal.getIdMaster())).findFirst().orElse(null);
            if (compare == null) {
                principal.setClassification(BaselineStatus.NEW);
                continue;
            }
            if (isChanged(principal, compare)) {
                principal.setClassification(BaselineStatus.CHANGED);
            }
        }
        final List<BaselineWorkpackDto> workpackBaselineDeleted = listCompare.stream().filter(
            w -> listParam.stream().noneMatch(p -> p.getIdMaster().equals(w.getIdMaster()))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(workpackBaselineDeleted)) {
            workpackBaselineDeleted.forEach(d -> d.setClassification(BaselineStatus.DELETED));
            list.addAll(workpackBaselineDeleted);
        }
        return list;
    }

    public void createSnapshot(Workpack workpack, Baseline baseline, BaselineScheduleSubmitDto schedule) {
        Workpack snapshot = getSnapshotOf(workpack);
        snapshot = workpackRepository.save(snapshot);
        workpackRepository.createSnapshotRelationshipWithMaster(workpack.getId(), snapshot.getId());
        workpackRepository.createSnapshotRelationshipWithBaseline(baseline.getId(), snapshot.getId());
        createSnapshotSchedule(snapshot, baseline, schedule);
    }

    private void createSnapshotSchedule(Workpack snapshot, Baseline baseline, BaselineScheduleSubmitDto schedule) {
        List<CostAccount> costAccounts = new ArrayList<>(0);
        if (snapshot instanceof Deliverable && schedule != null) {
            Schedule scheduleSnapshot = createScheduleSnapshot(snapshot, baseline, schedule);
            if (CollectionUtils.isNotEmpty(schedule.getSteps())) {
                for (BaselineStepSubmitDto stepDto : schedule.getSteps()) {
                    createStepSnapshot(stepDto, baseline, scheduleSnapshot, costAccounts);
                }
            }
        }
    }

    private void createStepSnapshot(BaselineStepSubmitDto stepDto, Baseline baseline, Schedule scheduleSnapshot, List<CostAccount> costAccounts) {
        Step snapshot = new Step();
        snapshot.setCategory(CategoryEnum.SNAPSHOT);
        snapshot.setPlannedWork(stepDto.getPlannedWork());
        snapshot.setActualWork(stepDto.getActualWork());
        snapshot = stepRepository.save(snapshot);
        stepRepository.createSnapshotRelationshipWithSchedule(scheduleSnapshot.getId(), snapshot.getId());
        stepRepository.createSnapshotRelationshipWithMaster(stepDto.getIdStep(), snapshot.getId());
        stepRepository.createSnapshotRelationshipWithBaseline(baseline.getId(), snapshot.getId());
        if (CollectionUtils.isNotEmpty(stepDto.getConsumes())) {
            for (BaselineConsumesStepSubmitDto consume : stepDto.getConsumes()) {
                createConsumesSnapshot(consume, baseline, snapshot, costAccounts);
            }
        }
    }

    private void createConsumesSnapshot(BaselineConsumesStepSubmitDto consumeDto, Baseline baseline, Step snapshot, List<CostAccount> costAccounts) {
        CostAccount costAccount = costAccounts.stream().filter(
            c -> c.getMaster() != null && c.getMaster().getMaster().getId().equals(consumeDto.getIdCostAccount()))
                                              .findFirst().orElse(null);
        if (costAccount == null) {
            costAccount = new CostAccount();
            costAccount.setCategory(CategoryEnum.SNAPSHOT);
            costAccount = costAccountRepository.save(costAccount);
            costAccountRepository.createSnapshotRelationshipWithMaster(consumeDto.getIdCostAccount(), costAccount.getId());
            costAccountRepository.createSnapshotRelationshipWithBaseline(baseline.getId(), costAccount.getId());
            costAccount = costAccountRepository.findById(costAccount.getId()).orElse(null);
            costAccounts.add(costAccount);
        }
        Consumes consumes = new Consumes();
        consumes.setActualCost(consumeDto.getActualCost());
        consumes.setPlannedCost(consumeDto.getPlannedCost());
        consumes.setStep(snapshot);
        consumes.setCostAccount(costAccount);
        consumesRepository.save(consumes);
    }

    private Schedule createScheduleSnapshot(Workpack workpack, Baseline baseline, BaselineScheduleSubmitDto schdule) {
        Schedule snapshot = new Schedule();
        snapshot.setWorkpack(workpack);
        snapshot.setEnd(schdule.getEnd());
        snapshot.setStart(schdule.getStart());
        snapshot.setCategory(CategoryEnum.SNAPSHOT);
        snapshot = scheduleRepository.save(snapshot);
        scheduleRepository.createSnapshotRelationshipWithMaster(schdule.getIdSchedule(), snapshot.getId());
        scheduleRepository.createSnapshotRelationshipWithBaseline(baseline.getId(), snapshot.getId());
        return snapshot;
    }

    public void createSnapshot(Workpack workpack, Workpack snapshotLastBaseline, Baseline baseline
        , BaselineScheduleSubmitDto scheduleLastBaselline) {
        Workpack snapshot = getSnapshotOf(snapshotLastBaseline);
        snapshot = workpackRepository.save(snapshot);
        workpackRepository.createSnapshotRelationshipWithMaster(workpack.getId(), snapshot.getId());
        workpackRepository.createSnapshotRelationshipWithBaseline(baseline.getId(), snapshot.getId());
        createSnapshotSchedule(snapshot, baseline, scheduleLastBaselline);
    }


    private Workpack getSnapshotOf(Workpack workpack) {
        Workpack snapshot = workpack instanceof Milestone ? new Milestone() : new Deliverable();
        snapshot.setDate(workpack.getDate());
        snapshot.setCategory(CategoryEnum.SNAPSHOT);
        snapshot.setFullName(workpack.getFullName());
        snapshot.setName(workpack.getName());
        return snapshot;
    }
}
