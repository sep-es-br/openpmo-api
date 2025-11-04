package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineDetailResponse;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineUpdateBreakdown;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.dto.baselines.UpdateObject;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;

@Service
public class GetBaselineService implements IGetBaselineService {
  private final BaselineRepository baselineRepository;

  private final IGetAllBaselineEvaluations getAllBaselineEvaluations;

  private final BaselineRepository repository;

  private final BaselineServiceUtil baselineServiceUtil;

  private final ApplicationCacheUtil cacheUtil;

  private final WorkpackRepository workpackRepository;

  private final WorkpackModelService workpackModelService;

  private final GetBaselineUpdatesService getBaselineUpdatesService;

  @Autowired
  public GetBaselineService(
    final BaselineRepository baselineRepository,
    final IGetAllBaselineEvaluations getAllBaselineEvaluations,
    final BaselineServiceUtil baselineServiceUtil,
    final BaselineRepository repository,
    final ApplicationCacheUtil cacheUtil,
    final WorkpackRepository workpackRepository,
    final WorkpackModelService workpackModelService,
    final GetBaselineUpdatesService getBaselineUpdatesService
  ) {
    this.baselineRepository = baselineRepository;
    this.getAllBaselineEvaluations = getAllBaselineEvaluations;
    this.baselineServiceUtil = baselineServiceUtil;
    this.repository = repository;
    this.cacheUtil = cacheUtil;
    this.workpackRepository = workpackRepository;
    this.workpackModelService = workpackModelService;
    this.getBaselineUpdatesService = getBaselineUpdatesService;
  }

  @Override
  public BaselineDetailResponse getById(final Long idBaseline, final Long idWorkpack) {
    BaselineDetailResponse result = null;

    final Baseline baseline = this.getBaselineById(idBaseline);

    final List<BaselineResultDto> bases = this.baselineRepository.findAllInWorkpackByIdWorkpack(baseline.getIdWorkpack());
    BaselineResultDto baseLineParam = bases.stream().filter(b -> b.getIdBaseline().equals(idBaseline)).findFirst().orElse(null);
    BaselineResultDto baselineCompare = null;

    if (baseLineParam != null) {
      switch (baseLineParam.getStatus()) {
        case PROPOSED:
        // Caso tenha selecionado uma LB que foi proposta
          baselineCompare = bases.stream().filter(BaselineResultDto::isActive).findFirst().orElse(null);
          // Irá comparar com a LB que estiver ativa, se existir
          break;
        case APPROVED:
        // Caso tenha selecionado uma LB que foi aprovada
          baselineCompare = bases.stream().filter(
              b -> b.getActivationDate() != null && b.getActivationDate().isBefore(
                  baseLineParam.getActivationDate()))
              .max(
                  Comparator.comparing(BaselineResultDto::getActivationDate))
              .orElse(null);
          // Irá comparar com uma LB que tenha sido aprovada anteriormente, e cuja data de ativação seja anterior à data de ativação da LB selecionada
          break;
        case REJECTED:
        // Caso tenha selecionado uma LB que foi rejeitada
          baselineCompare = bases.stream().filter(b -> b.getActivationDate() != null && b.getActivationDate().isBefore(
              baseLineParam.getProposalDate())).max(
                  Comparator.comparing(BaselineResultDto::getActivationDate))
              .orElse(null);
          // Irá comparar com uma LB que tenha sido aprovada anteriormente, e cuja data de ativação seja anterior à data de proposta da LB selecionada
          break;
        case DRAFT:
        default:
          break;
      }

      result = this.compareBaseline(baseline, baseLineParam, baselineCompare, idWorkpack);
    }
    return result;
  }

  private BaselineDetailResponse compareBaseline(
    Baseline baseline,
    BaselineResultDto baseLineParam,
    BaselineResultDto baselineCompare,
    Long idWorkpack
  ) {
    final List<BaselineWorkpackDto> workpacksBaseline = this.baselineRepository
        .findAllWorkpacBaselineById(baseLineParam.getIdBaseline());
    if (baselineCompare == null) {
      workpacksBaseline.forEach(w -> w.setClassification(BaselineStatus.NEW));
      return getBaselineDetailResponse(baseline, workpacksBaseline, idWorkpack);
    }
    addScheduleAndConsumes(workpacksBaseline);

    final List<BaselineWorkpackDto> workpackBaselineCompare = this.baselineRepository
        .findAllWorkpacBaselineById(baselineCompare.getIdBaseline());
    addScheduleAndConsumes(workpackBaselineCompare);

    List<BaselineWorkpackDto> result = baselineServiceUtil.compare(workpacksBaseline, workpackBaselineCompare);
    // result.removeIf(b -> b.getClassification() == null);
    result = result.stream().filter(el -> el.getClassification() != null).collect(Collectors.toList());
    return getBaselineDetailResponse(baseline, result, idWorkpack);
  }

  private void addScheduleAndConsumes(final List<BaselineWorkpackDto> workpacks) {
    Set<Long> deliverablesId = workpacks.stream().filter(d -> "Deliverable".equals(d.getType())).map(
        BaselineWorkpackDto::getId).collect(Collectors.toSet());
    List<BaselineScheduleStep> scheduleSteps = baselineRepository
        .findAllBaselineScheduleStepById(new ArrayList<>(deliverablesId));
    List<BaselineConsumesStep> stepConsumes = baselineRepository
        .findAllStepConsumesById(new ArrayList<>(deliverablesId));
    for (BaselineWorkpackDto workpack : workpacks) {
      workpack.setSchedule(
          scheduleSteps.stream().filter(s -> s.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
      workpack.setConsumes(
          stepConsumes.stream().filter(c -> c.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
    }
  }

  private BaselineDetailResponse getBaselineDetailResponse(Baseline baseline, List<BaselineWorkpackDto> workpacks, Long idWorkpack) {
    final BaselineDetailResponse response = BaselineDetailResponse.of(baseline);
    final List<EvaluationItem> items = this.getEvaluationItems(baseline.getId());
    response.setEvaluations(items);
    response.setUpdates(new ArrayList<>(0));
    
    if (Objects.isNull(idWorkpack)) {
      idWorkpack = this.baselineRepository.findProjectByBaselineId(baseline.getId()).getId();
    }

    Long idPlan = this.workpackRepository.findPlanByWorkpackId(idWorkpack).getId();
    WorkpackResultDto workpackDto = cacheUtil.getWorkpackBreakdownStructure(idWorkpack, idPlan, true);
    List<UpdateObject> updateList = this.assembleListOfUpdates(workpacks);
    List<BaselineUpdateBreakdown> updateBreakdown = this.getBaselineUpdatesService.createBaselineBreakdown(updateList, workpackDto);

    response.setUpdates(updateBreakdown);

    return response;
  }

  private Baseline getBaselineById(final Long idBaseline) {
    return this.repository.findBaselineDetailById(idBaseline)
        .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

  private List<EvaluationItem> getEvaluationItems(final Long idBaseline) {
    return this.getAllBaselineEvaluations.getEvaluations(idBaseline);
  }

  private List<UpdateObject> assembleListOfUpdates(List<BaselineWorkpackDto> workpacks) {
    final List<UpdateObject> resultList = new ArrayList<>(0);

    workpacks.forEach(w -> {
      UpdateObject newUR = new UpdateObject(
        w.getId(),
        w.getIdMaster(),
        w.getFontIcon(),
        w.getName(),
        w.getClassification(),
        true
      );
      newUR.setWorkpackType(w.getType());

      if (w.getType().equals("Deliverable")) {
        try {
          Optional<WorkpackModel> deliveryModel = this.workpackModelService.getWorkpackModelByWorkpackId(w.getId());
          newUR.setDeliveryModelHasActiveSchedule(
              deliveryModel.isPresent() && deliveryModel.get().getScheduleSessionActive());
        } catch (Exception e) {
          newUR.setDeliveryModelHasActiveSchedule(false);
        }

        if (w.getSchedule().size() == 0) {
          // Entrega não possui cronograma
          newUR.setClassification(BaselineStatus.NO_SCHEDULE);
        } else if (!this.workpackModelService.deliveryHasValidScope(w.getId())) {
          // Entrega não possui cronograma com escopo válido
          newUR.setClassification(BaselineStatus.UNDEFINED_SCOPE);
        }
      }

      resultList.add(newUR);
    });

    return resultList;
  }

}
