package br.gov.es.openpmo.service.baselines;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleSubmitDto;
import br.gov.es.openpmo.dto.baselines.BaselineStepSubmitDto;
import br.gov.es.openpmo.dto.baselines.SubmitBaselineRequest;
import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class SubmitBaselineService implements ISubmitBaselineService {

  private final JournalCreator journalCreator;

  private final BaselineRepository baselineRepository;
  private final IAsyncDashboardService dashboardService;

  private final WorkpackRepository workpackRepository;
  private final BaselineServiceUtil baselineServiceUtil;

  @Autowired
  public SubmitBaselineService(
    final JournalCreator journalCreator,
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final BaselineServiceUtil baselineServiceUtil,
    final IAsyncDashboardService dashboardService
  ) {
    this.journalCreator = journalCreator;
    this.baselineRepository = baselineRepository;
    this.dashboardService = dashboardService;
    this.workpackRepository = workpackRepository;
    this.baselineServiceUtil = baselineServiceUtil;
  }

  private void changeStatusToProposed(final Baseline baseline) {
    baseline.setStatus(Status.PROPOSED);
    baseline.setProposalDate(LocalDateTime.now());
    this.baselineRepository.save(baseline, 0);
  }

  @Override
  public void submit(
      final Long idBaseline,
      final SubmitBaselineRequest request,
      final Long idPerson
  ) {
    final Baseline baseline = this.getBaselineById(idBaseline);

    Long baselineReference = getBaselineIdReference(baseline);

    if (CollectionUtils.isNotEmpty(request.getUpdates())) {
      List<Workpack> workpacks = this.getWorkpack(request);
      List<Workpack> snapshotReferences = this.getSnapshotReferences(baselineReference, request);
      List<BaselineScheduleSubmitDto> schdules = getSchedule(request);
      List<BaselineScheduleSubmitDto> schdulesReferences = this.getSnapshotScheduleReferences(baselineReference, request);

      for (UpdateRequest update : request.getUpdates()) {
        if (Boolean.TRUE.equals(update.getIncluded())) {
          processIncluded(update, baseline, workpacks, schdules);
          continue;
        }
        processNotIncluded(update, baseline, workpacks, snapshotReferences, schdulesReferences);
      }

      if (baselineReference != null) {
        addWorkpackWithoutChanges(request, baseline);
      }
    }
    this.changeStatusToProposed(baseline);
    this.journalCreator.baseline(baseline, idPerson);

//    this.dashboardService.calculate(baseline.getBaselinedBy().getWorkpack().getId(), true);
  }

  private void addWorkpackWithoutChanges(final SubmitBaselineRequest request, final Baseline baseline) {
    Set<Long> ids = workpackRepository.findAllDeliverableAndMilestoneByProject(baseline.getBaselinedBy().getWorkpack().getId());
    ids.removeIf(id -> request.getUpdates().stream().map(UpdateRequest::getIdWorkpack).anyMatch(r -> r.equals(id)));
    if (CollectionUtils.isNotEmpty(ids)) {
      List<Workpack> workpackWithoutChanges = workpackRepository.findAllWithDeletedByIdThin(new ArrayList<>(ids));
      List<BaselineScheduleSubmitDto> schduleWithoutChanges = this.baselineRepository.findAllByWorkpackId(new ArrayList<>(ids));
      addStepToSchedule(schduleWithoutChanges, ids);
      for (Workpack workpackWithoutChange : workpackWithoutChanges) {
        BaselineScheduleSubmitDto baseSchedule = schduleWithoutChanges.stream().filter(
            s -> s.getIdWorkpack().equals(workpackWithoutChange.getId())).findFirst().orElse(null);
        baselineServiceUtil.createSnapshot(workpackWithoutChange, baseline, baseSchedule);
      }
    }
  }

  private List<Workpack> getWorkpack(final SubmitBaselineRequest request) {
    if (CollectionUtils.isNotEmpty(request.getUpdates())) {
      Set<Long> ids = request.getUpdates().stream().map(UpdateRequest::getIdWorkpack).collect(Collectors.toSet());
      if (!ids.isEmpty()) {
        return workpackRepository.findAllWithDeletedByIdThin(new ArrayList<>(ids));
      }
    }
    return new ArrayList<>(0);
  }

  private List<Workpack> getSnapshotReferences(Long baselineReference, final SubmitBaselineRequest request) {
    if (baselineReference != null && CollectionUtils.isNotEmpty(request.getUpdates())) {
      Set<Long> ids = this.getIdsRequestSnapshot(request);
      if (!ids.isEmpty()) {
        return workpackRepository.findAllSnapshotWithDeletedByIdThin(new ArrayList<>(ids), baselineReference);
      }
    }
    return new ArrayList<>(0);
  }

  private List<BaselineScheduleSubmitDto> getSchedule(final SubmitBaselineRequest request) {
    if (CollectionUtils.isEmpty(request.getUpdates())) {
      return new ArrayList<>(0);
    }
    Set<Long> ids = getIdsRequestMaster(request);
    if (CollectionUtils.isNotEmpty(ids)) {
      List<BaselineScheduleSubmitDto> schdules = this.baselineRepository.findAllByWorkpackId(new ArrayList<>(ids));
      addStepToSchedule(schdules, ids);
      return schdules;
    }

    return new ArrayList<>(0);
  }

  private void addStepToSchedule(List<BaselineScheduleSubmitDto> schdules, Set<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) return;
    final List<BaselineStepSubmitDto> steps = baselineRepository.findAllStepByScheduleIds(new ArrayList<>(ids));
    this.addConsumesToStep(steps, ids);
    schdules.forEach(s -> s.getSteps().addAll(
        steps.stream().filter(st -> st.getIdSchedule().equals(s.getIdSchedule())).collect(Collectors.toList())));
  }

  private void addConsumesToStep(List<BaselineStepSubmitDto> steps, Set<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) return;
    final List<BaselineConsumesStepSubmitDto> consumes = baselineRepository.findAllByScheduleId(new ArrayList<>(ids));
    steps.forEach(s -> s.getConsumes().addAll(
        consumes.stream().filter(c -> c.getIdStep().equals(s.getIdStep())).collect(Collectors.toList())));
  }

  private List<BaselineScheduleSubmitDto> getSnapshotScheduleReferences(Long baselineReference, final SubmitBaselineRequest request) {
    if (baselineReference == null || CollectionUtils.isEmpty(request.getUpdates())) {
      return new ArrayList<>(0);
    }
    Set<Long> ids = this.getIdsRequestSnapshot(request);
    if (!ids.isEmpty()) {
      List<BaselineScheduleSubmitDto> schdules = this.baselineRepository.findAllSnapshotByWorkpackId(new ArrayList<>(ids), baselineReference);
      this.addStepToScheduleSnapshot(schdules, ids, baselineReference);
      return schdules;
    }
    return new ArrayList<>(0);
  }

  private void addStepToScheduleSnapshot(List<BaselineScheduleSubmitDto> schdules, Set<Long> ids, Long baselineReference) {
    if (CollectionUtils.isEmpty(ids)) return;
    List<BaselineStepSubmitDto> steps = baselineRepository.findAllStepSnapshotByScheduleIds(new ArrayList<>(ids), baselineReference);
    this.addConsumesToStepSnapshot(steps, ids, baselineReference);
    schdules.forEach(s -> s.getSteps().addAll(
        steps.stream().filter(st -> st.getIdSchedule().equals(s.getIdSchedule())).collect(Collectors.toList())));
  }

  private void addConsumesToStepSnapshot(List<BaselineStepSubmitDto> steps, Set<Long> ids, Long baselineReference) {
    if (CollectionUtils.isEmpty(ids)) return;
    final List<BaselineConsumesStepSubmitDto> consumes = baselineRepository.findAllSnapshotByScheduleId(new ArrayList<>(ids), baselineReference);
    steps.forEach(s -> s.getConsumes().addAll(
        consumes.stream().filter(c -> c.getIdStep().equals(s.getIdStep())).collect(Collectors.toList())));
  }

  private Set<Long> getIdsRequestSnapshot(final SubmitBaselineRequest request) {
    return request.getUpdates().stream()
                  .filter(r -> Boolean.FALSE.equals(r.getIncluded())
                      && (BaselineStatus.CHANGED.equals(r.getClassification())
                      || BaselineStatus.DELETED.equals(r.getClassification()))
                  ).map(UpdateRequest::getIdWorkpack)
                  .collect(Collectors.toSet());
  }

  private Set<Long> getIdsRequestMaster(final SubmitBaselineRequest request) {
    return request.getUpdates().stream()
                  .filter(r -> Boolean.TRUE.equals(r.getIncluded())
                      && (BaselineStatus.NEW.equals(r.getClassification())
                          || BaselineStatus.CHANGED.equals(r.getClassification())
                          )
                  ).map(UpdateRequest::getIdWorkpack)
                  .collect(Collectors.toSet());
  }


  private Long getBaselineIdReference(final Baseline baseline) {
    final List<BaselineResultDto> bases = this.baselineRepository.findAllInWorkpackByIdWorkpack(baseline.getIdWorkpack());
    if (bases.size() > 1) {
      return bases.stream().filter(
          BaselineResultDto::isActive).map(BaselineResultDto::getIdBaseline).findFirst().orElse(null);
    }
    return null;
  }

  private void processIncluded(final UpdateRequest update, final Baseline baseline, final List<Workpack> workpacks
      , List<BaselineScheduleSubmitDto> schdules) {
    Workpack workpack = workpacks.stream()
                                 .filter(w -> w.getId().equals(update.getIdWorkpack()))
                                 .findFirst().orElse(null);
    BaselineScheduleSubmitDto schedule = schdules.stream()
                                                 .filter(s -> s.getIdWorkpack().equals(update.getIdWorkpack()))
                                                 .findFirst().orElse(null);
    if (workpack != null) {
      if (BaselineStatus.NEW.equals(update.getClassification())
          || BaselineStatus.CHANGED.equals(update.getClassification())){
        baselineServiceUtil.createSnapshot(workpack, baseline, schedule);
      }
    }
  }

  private void processNotIncluded(UpdateRequest update, final Baseline baseline, final List<Workpack> workpacks
      , final List<Workpack> snapshotReferences, final List<BaselineScheduleSubmitDto> schdulesReferences) {
    Workpack master = workpacks.stream()
                               .filter(w -> w.getId().equals(update.getIdWorkpack()))
                               .findFirst().orElse(null);
    Workpack snapshot = snapshotReferences.stream()
                                          .filter(w -> w.getWorkpackMasterId().equals(update.getIdWorkpack()))
                                          .findFirst().orElse(null);
    BaselineScheduleSubmitDto scheduleSnapshot = schdulesReferences.stream()
                                                 .filter(s -> s.getIdWorkpack().equals(update.getIdWorkpack()))
                                                 .findFirst().orElse(null);
    if (BaselineStatus.CHANGED.equals(update.getClassification())
        || BaselineStatus.DELETED.equals(update.getClassification())) {
      if (master != null && snapshot != null) {
        baselineServiceUtil.createSnapshot(master, snapshot, baseline, scheduleSnapshot);
      }
    }
  }

  private Baseline getBaselineById(final Long idBaseline) {
    return this.baselineRepository.findBaselineDetailById(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.BASELINE_NOT_FOUND))
      .ifIsNotDraftThrowsException();
  }

}
