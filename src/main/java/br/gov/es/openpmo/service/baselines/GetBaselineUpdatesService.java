package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetBaselineUpdatesService implements IGetBaselineUpdatesService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final BaselineServiceUtil baselineServiceUtil;

  @Autowired
  public GetBaselineUpdatesService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final BaselineServiceUtil baselineServiceUtil
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.baselineServiceUtil = baselineServiceUtil;
  }

  @Override
  public List<UpdateResponse> getUpdates(final Long idWorkpack) {
    final Workpack workpack = this.findProjectWorkpackById(idWorkpack);
    final List<BaselineWorkpackDto> workpacskMaster = this.baselineRepository.findAllWorkpacMasterById(workpack.getId());
    final Baseline baseline = this.baselineRepository.findActiveBaseline(idWorkpack).orElse(null);
    if (baseline == null) {
      workpacskMaster.forEach(w -> w.setClassification(BaselineStatus.NEW));
      return getBaselineDetailResponse(workpacskMaster);
    }
    addScheduleAndConsumesMaster(workpacskMaster);

    final List<BaselineResultDto> bases = this.baselineRepository.findAllInWorkpackByIdWorkpack(idWorkpack);

    BaselineResultDto baseLineParam = bases.stream().filter(b -> b.getIdBaseline().equals(baseline.getId())).findFirst().orElse(null);

    List<UpdateResponse> list = new ArrayList<>(0);
    if (baseLineParam != null) {
      final List<BaselineWorkpackDto> workpackBaselineCompare = this.baselineRepository.findAllWorkpacBaselineById(baseLineParam.getIdBaseline());
      addScheduleAndConsumesSnapshot(workpackBaselineCompare);

      final List<BaselineWorkpackDto> result = this.baselineServiceUtil.compare(workpacskMaster, workpackBaselineCompare);
      result.removeIf(r -> r.getClassification() == null);
      list.addAll(getBaselineDetailResponse(result));
    }
    return list;
  }

  private void addScheduleAndConsumesSnapshot(final List<BaselineWorkpackDto> workpacks) {
    Set<Long> deliverablesId = workpacks.stream().filter(d -> "Deliverable".equals(d.getType())).map(
        BaselineWorkpackDto::getId).collect(Collectors.toSet());
    List<BaselineConsumesStep> stepConsumes = baselineRepository.findAllStepConsumesById(new ArrayList<>(deliverablesId));
    List<BaselineScheduleStep> scheduleSteps = baselineRepository.findAllBaselineScheduleStepById(new ArrayList<>(deliverablesId));
    for (BaselineWorkpackDto workpack : workpacks) {
      workpack.setConsumes(
          stepConsumes.stream().filter(c -> c.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
      workpack.setSchedule(
          scheduleSteps.stream().filter(s -> s.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
    }
  }

  private void addScheduleAndConsumesMaster(final List<BaselineWorkpackDto> workpacks) {
    Set<Long> deliverablesId = workpacks.stream().filter(d -> "Deliverable".equals(d.getType())).map(
        BaselineWorkpackDto::getId).collect(Collectors.toSet());
    List<BaselineConsumesStep> stepConsumes = baselineRepository.findAllStepConsumesMasterById(new ArrayList<>(deliverablesId));
    List<BaselineScheduleStep> scheduleSteps = baselineRepository.findAllScheduleStepMasterById(new ArrayList<>(deliverablesId));
    for (BaselineWorkpackDto workpack : workpacks) {
      workpack.setConsumes(
          stepConsumes.stream().filter(c -> c.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
      workpack.setSchedule(
          scheduleSteps.stream().filter(s -> s.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
    }
  }

  private List<UpdateResponse> getBaselineDetailResponse(List<BaselineWorkpackDto> workpacks) {
    final List<UpdateResponse> list = new ArrayList<>(0);
    workpacks.forEach(w -> list.add(new UpdateResponse(w.getId(), w.getFontIcon(), w.getName(), w.getClassification(), true)));
    return list;
  }

  private Workpack findProjectWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND))
      .ifIsNotProjectThrowsException();
  }

}
