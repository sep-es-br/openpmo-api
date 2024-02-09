package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineDetailResponse;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class GetBaselineService implements IGetBaselineService {

  private final BaselineRepository baselineRepository;

  private final IGetAllBaselineEvaluations getAllBaselineEvaluations;

  private final BaselineRepository repository;
  private final BaselineServiceUtil baselineServiceUtil;

  @Autowired
  public GetBaselineService(
    final BaselineRepository baselineRepository,
    final IGetAllBaselineEvaluations getAllBaselineEvaluations,
    final BaselineServiceUtil baselineServiceUtil,
    final BaselineRepository repository
  ) {
    this.baselineRepository = baselineRepository;
    this.getAllBaselineEvaluations = getAllBaselineEvaluations;
    this.baselineServiceUtil = baselineServiceUtil;
    this.repository = repository;
  }

  @Override
  public BaselineDetailResponse getById(final Long idBaseline) {
    BaselineDetailResponse result = null;

    final Baseline baseline = this.getBaselineById(idBaseline);

    final List<BaselineResultDto> bases = this.baselineRepository.findAllInWorkpackByIdWorkpack(baseline.getIdWorkpack());
    BaselineResultDto baseLineParam = bases.stream().filter(b -> b.getIdBaseline().equals(idBaseline)).findFirst().orElse(null);
    BaselineResultDto baselineCompare = null;
    if (baseLineParam != null) {
      switch (baseLineParam.getStatus()) {
        case PROPOSED:
          baselineCompare = bases.stream().filter(BaselineResultDto::isActive).findFirst().orElse(null);
          break;
        case APPROVED:
          baselineCompare = bases.stream().filter(
              b -> b.getActivationDate() != null && b.getActivationDate().isBefore(
                  baseLineParam.getActivationDate())).max(
              Comparator.comparing(BaselineResultDto::getActivationDate)).orElse(null);
          break;
        case REJECTED:
          baselineCompare = bases.stream().filter(b -> b.getActivationDate() != null && b.getActivationDate().isBefore(
              baseLineParam.getProposalDate())).max(
              Comparator.comparing(BaselineResultDto::getActivationDate)).orElse(null);
          break;
      }
      result = this.compareBaseline(baseline, baseLineParam, baselineCompare);
    }
    return result;
  }

  private BaselineDetailResponse compareBaseline(Baseline baseline, BaselineResultDto baseLineParam, BaselineResultDto baselineCompare) {
    final List<BaselineWorkpackDto> workpacksBaseline = this.baselineRepository.findAllWorkpacBaselineById(baseLineParam.getIdBaseline());
    if (baselineCompare == null) {
      workpacksBaseline.forEach(w -> w.setClassification(BaselineStatus.NEW));
      return getBaselineDetailResponse(baseline, workpacksBaseline);
    }
    addScheduleAndConsumes(workpacksBaseline);

    final List<BaselineWorkpackDto> workpackBaselineCompare = this.baselineRepository.findAllWorkpacBaselineById(baselineCompare.getIdBaseline());
    addScheduleAndConsumes(workpackBaselineCompare);

    final List<BaselineWorkpackDto> result = baselineServiceUtil.compare(workpacksBaseline, workpackBaselineCompare);
    result.removeIf(b -> b.getClassification() == null);
    return getBaselineDetailResponse(baseline, result);
  }


  private void addScheduleAndConsumes(final List<BaselineWorkpackDto> workpacks) {
    Set<Long> deliverablesId = workpacks.stream().filter(d -> "Deliverable".equals(d.getType())).map(
        BaselineWorkpackDto::getId).collect(Collectors.toSet());
    List<BaselineScheduleStep> scheduleSteps = baselineRepository.findAllBaselineScheduleStepById(new ArrayList<>(deliverablesId));
    List<BaselineConsumesStep> stepConsumes = baselineRepository.findAllStepConsumesById(new ArrayList<>(deliverablesId));
    for (BaselineWorkpackDto workpack : workpacks) {
      workpack.setSchedule(
          scheduleSteps.stream().filter(s -> s.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
      workpack.setConsumes(
          stepConsumes.stream().filter(c -> c.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
    }
  }

  private BaselineDetailResponse getBaselineDetailResponse(Baseline baseline,List<BaselineWorkpackDto> workpacks) {
    final BaselineDetailResponse response = BaselineDetailResponse.of(baseline);
    final List<EvaluationItem> items = this.getEvaluationItems(baseline.getId());
    response.setEvaluations(items);
    response.setUpdates(new ArrayList<>(0));
    workpacks.forEach(w -> {
      Long id = BaselineStatus.DELETED.equals(w.getClassification()) ? w.getIdMaster() : w.getId();
      response.getUpdates().add(new UpdateResponse(id, w.getFontIcon(), w.getName(), w.getClassification(), true));
    });
    return response;
  }

  private Baseline getBaselineById(final Long idBaseline) {
    return this.repository.findBaselineDetailById(idBaseline)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private List<EvaluationItem> getEvaluationItems(final Long idBaseline) {
    return this.getAllBaselineEvaluations.getEvaluations(idBaseline);
  }

}
