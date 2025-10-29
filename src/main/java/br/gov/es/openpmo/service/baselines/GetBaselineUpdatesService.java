package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineUpdateBreakdown;
import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineWorkpackDto;
import br.gov.es.openpmo.dto.baselines.UpdateObject;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetBaselineUpdatesService implements IGetBaselineUpdatesService {
  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final BaselineServiceUtil baselineServiceUtil;

  private final WorkpackModelService workpackModelService;

  private final ApplicationCacheUtil cacheUtil;

  @Autowired
  public GetBaselineUpdatesService(
      final BaselineRepository baselineRepository,
      final WorkpackRepository workpackRepository,
      final BaselineServiceUtil baselineServiceUtil,
      final WorkpackModelService workpackModelService,
      final ApplicationCacheUtil cacheUtil) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.baselineServiceUtil = baselineServiceUtil;
    this.workpackModelService = workpackModelService;
    this.cacheUtil = cacheUtil;
  }

  @Override
  public List<BaselineUpdateBreakdown> getUpdates(final Long idWorkpack, final Long idPlan) {
    final Workpack workpack = this.findProjectWorkpackById(idWorkpack);
    final List<BaselineWorkpackDto> workpacksMaster = this.baselineRepository
        .findAllWorkpacksMasterById(workpack.getId());
    final Baseline baseline = this.baselineRepository.findActiveBaseline(idWorkpack).orElse(null);
    addScheduleAndConsumesMaster(workpacksMaster);

    WorkpackResultDto workpackDto = cacheUtil.getWorkpackBreakdownStructure(idWorkpack, idPlan, true);

    if (baseline == null) {
      workpacksMaster.forEach(w -> w.setClassification(BaselineStatus.NEW));
      List<UpdateObject> updatesList = getBaselineDetailResponse(workpacksMaster);
      return createBaselineBreakdown(updatesList, workpackDto);
      // return new UpdateResponse(updates, workpackDto);
    }

    final List<BaselineResultDto> bases = this.baselineRepository.findAllInWorkpackByIdWorkpack(idWorkpack);

    BaselineResultDto baseLineParam = bases.stream().filter(b -> b.getIdBaseline().equals(baseline.getId())).findFirst()
        .orElse(null);

    List<UpdateObject> updatesList = new ArrayList<>(0);

    if (baseLineParam != null) {
      final List<BaselineWorkpackDto> workpackBaselineCompare = this.baselineRepository
          .findAllWorkpacBaselineById(baseLineParam.getIdBaseline());
      addScheduleAndConsumesSnapshot(workpackBaselineCompare);

      final List<BaselineWorkpackDto> result = this.baselineServiceUtil.compare(workpacksMaster,
          workpackBaselineCompare);
      result.removeIf(r -> r.getClassification() == null);
      updatesList.addAll(getBaselineDetailResponse(result));
    }

    // return new UpdateResponse(list, workpackDto);
    return createBaselineBreakdown(updatesList, workpackDto);
  }

  private void addScheduleAndConsumesSnapshot(final List<BaselineWorkpackDto> workpacks) {
    Set<Long> deliverablesId = workpacks.stream().filter(d -> "Deliverable".equals(d.getType())).map(
        BaselineWorkpackDto::getId).collect(Collectors.toSet());
    List<BaselineConsumesStep> stepConsumes = baselineRepository
        .findAllStepConsumesById(new ArrayList<>(deliverablesId));
    List<BaselineScheduleStep> scheduleSteps = baselineRepository
        .findAllBaselineScheduleStepById(new ArrayList<>(deliverablesId));

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
    List<BaselineConsumesStep> stepConsumes = baselineRepository
        .findAllStepConsumesMasterById(new ArrayList<>(deliverablesId));
    List<BaselineScheduleStep> scheduleSteps = baselineRepository
        .findAllScheduleStepMasterById(new ArrayList<>(deliverablesId));
    for (BaselineWorkpackDto workpack : workpacks) {
      workpack.setConsumes(
          stepConsumes.stream().filter(c -> c.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
      workpack.setSchedule(
          scheduleSteps.stream().filter(s -> s.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList()));
    }
  }

  private List<UpdateObject> getBaselineDetailResponse(List<BaselineWorkpackDto> workpacks) {
    final List<UpdateObject> list = new ArrayList<>(0);
    workpacks.forEach(w -> {
      UpdateObject newUR = new UpdateObject(w.getId(), w.getFontIcon(), w.getName(),
          w.getClassification(), true);
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

      list.add(newUR);
    });

    return list;
  }

  private Workpack findProjectWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND))
        .ifIsNotProjectThrowsException();
  }

  private List<BaselineUpdateBreakdown> createBaselineBreakdown(
    List<UpdateObject> updates,
    WorkpackResultDto workpackDto
  ) {
    List<BaselineUpdateBreakdown> listEtapas = new ArrayList<>(0);

    for (WorkpackResultDto etapa : workpackDto.getChildren()) {
      BaselineUpdateBreakdown etapaBreakdown = new BaselineUpdateBreakdown(
        etapa.getId(),
        etapa.getIdWorkpackModel(),
        etapa.getIdPlan(),
        etapa.getName(),
        etapa.getFullName(),
        etapa.getFontIcon(),
        etapa.getModelName(),
        etapa.getModelNameInPlural(),
        etapa.getType()
      );

      for (WorkpackResultDto child : etapa.getChildren()) {
        if (child.getType().equals("Organizer") && child.getModelName().equals("Subetapa")) {
          BaselineUpdateBreakdown subEtapaBreakdown = new BaselineUpdateBreakdown(
            child.getId(),
            child.getIdWorkpackModel(),
            child.getIdPlan(),
            child.getName(),
            child.getFullName(),
            child.getFontIcon(),
            child.getModelName(),
            child.getModelNameInPlural(),
            child.getType()
          );

          for (WorkpackResultDto entrega : child.getChildren()) {          
            UpdateObject entregaObject = updates
              .stream()
              .filter(item -> item.getIdWorkpack().equals(entrega.getId()))
              .findFirst()
              .orElse(null);

            if (entregaObject != null) {
              BaselineUpdateBreakdown entregaBreakdown = new BaselineUpdateBreakdown(
                entrega.getId(),
                entrega.getIdWorkpackModel(),
                entrega.getIdPlan(),
                entrega.getName(),
                entrega.getFullName(),
                entrega.getFontIcon(),
                entrega.getModelName(),
                entrega.getModelNameInPlural(),
                entrega.getType(),
                entregaObject.getClassification()
              );

              subEtapaBreakdown.addChild(entregaBreakdown);
            }
          };

          if (subEtapaBreakdown.getChildren().size() > 0) {
            etapaBreakdown.addChild(subEtapaBreakdown);
          }
        } else if (
          (child.getType().equals("Deliverable") && child.getModelName().equals("Entrega")) ||
          (child.getType().equals("Milestone") && child.getModelName().equals("Marco crítico"))
        ) {
          UpdateObject deliveryOrMilestoneObject = updates
            .stream()
            .filter(item -> item.getIdWorkpack().equals(child.getId()))
            .findFirst()
            .orElse(null);

          if (deliveryOrMilestoneObject != null) {
            BaselineUpdateBreakdown entregaBreakdown = new BaselineUpdateBreakdown(
              child.getId(),
              child.getIdWorkpackModel(),
              child.getIdPlan(),
              child.getName(),
              child.getFullName(),
              child.getFontIcon(),
              child.getModelName(),
              child.getModelNameInPlural(),
              child.getType(),
              deliveryOrMilestoneObject.getClassification()
            );

            etapaBreakdown.addChild(entregaBreakdown);
          }
        }
      }

      if (etapaBreakdown.getChildren().size() > 0) {
        listEtapas.add(etapaBreakdown);
      }
    }

    return listEtapas;
  };
}
