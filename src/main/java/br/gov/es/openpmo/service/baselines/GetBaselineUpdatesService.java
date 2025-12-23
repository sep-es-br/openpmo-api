package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineConsumesStep;
import br.gov.es.openpmo.dto.baselines.BaselineResultDto;
import br.gov.es.openpmo.dto.baselines.BaselineScheduleStep;
import br.gov.es.openpmo.dto.baselines.BaselineUpdateBreakdown;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    WorkpackResultDto workpackDto = cacheUtil.getFullWorkpackBreakdownStructure(idWorkpack, idPlan, true);

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
      // result.removeIf(r -> r.getClassification() == null);
      updatesList.addAll(getBaselineDetailResponse(result));

      // O código abaixo foi feito por conta da task #484, onde deveria-se verificar se haviam Entregas inclusas em LBs passadas que não
      // possuíam Cronograma ou Escopo na época que a LB foi submetida. A ideia era listar essas entregas nas Atualizações ao se criar
      // uma LB nova. Porém, foi identificado que isso já ocorre, visto que essas entregas aparecem com alerta de "Sem cronograma" ou
      // "Sem escopo". Portanto, fica o código aí para a posterioridade.

      // final List<BaselineResultDto> remainingBaselines = bases.stream().filter(b -> b.getIdBaseline() != baseline.getId()).collect(Collectors.toList());
      // for (BaselineResultDto oldBaseline : remainingBaselines) {
      //   List<BaselineWorkpackDto> oldBaselineWorkpacks = this.baselineRepository.findAllWorkpacBaselineById(oldBaseline.getIdBaseline());
      //   oldBaselineWorkpacks.removeIf(
      //     w -> updatesList.stream().anyMatch(
      //       p -> (
      //         p.getIdWorkpack().equals(w.getId()) ||
      //         p.getIdMaster().equals(w.getId()) ||
      //         p.getIdWorkpack().equals(w.getIdMaster()) ||
      //         p.getIdMaster().equals(w.getIdMaster())
      //       )
      //     )
      //   );

      //   addScheduleAndConsumesSnapshot(oldBaselineWorkpacks);

      //   final List<UpdateObject> oldProblematicDeliveries = new ArrayList<>();
      //   for (BaselineWorkpackDto oldWorkpack : oldBaselineWorkpacks) {
      //     if (oldWorkpack.getType().equals("Deliverable")) {
      //       UpdateObject newUR = new UpdateObject(
      //         oldWorkpack.getId(),
      //         oldWorkpack.getIdMaster(),
      //         oldWorkpack.getFontIcon(),
      //         oldWorkpack.getName(),
      //         oldWorkpack.getClassification(),
      //         true
      //       );
      //       newUR.setWorkpackType(oldWorkpack.getType());
  
      //       try {
      //         Optional<WorkpackModel> deliveryModel = this.workpackModelService.getWorkpackModelByWorkpackId(oldWorkpack.getId());
      //         newUR.setDeliveryModelHasActiveSchedule(deliveryModel.isPresent() && deliveryModel.get().getScheduleSessionActive());
      //       } catch (Exception e) {
      //         newUR.setDeliveryModelHasActiveSchedule(false);
      //       }
  
      //       if (oldWorkpack.getSchedule().size() == 0) {
      //         // Entrega não possui cronograma
      //         newUR.setClassification(BaselineStatus.NO_SCHEDULE);
      //       } else if (!this.workpackModelService.deliveryHasValidScope(oldWorkpack.getId())) {
      //         // Entrega não possui cronograma com escopo válido
      //         newUR.setClassification(BaselineStatus.UNDEFINED_SCOPE);
      //       }

      //       if (
      //         newUR.getClassification() != null &&
      //         (
      //           newUR.getClassification().equals(BaselineStatus.NO_SCHEDULE) ||
      //           newUR.getClassification().equals(BaselineStatus.UNDEFINED_SCOPE)
      //         )
      //       ) {
      //         newUR.setIsFromAnOldBaseline(true);
      //         oldProblematicDeliveries.add(newUR);
      //       }
      //     }
      //   }

      //   if (oldProblematicDeliveries.size() > 0) {
      //     updatesList.addAll(oldProblematicDeliveries);
      //   }
      // }
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

      list.add(newUR);
    });

    return list;
  }

  private Workpack findProjectWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findWithPropertiesAndModelAndChildrenById(idWorkpack)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND))
        .ifIsNotProjectThrowsException();
  }

  public List<BaselineUpdateBreakdown> createBaselineBreakdown(
      List<UpdateObject> updates,
      WorkpackResultDto workpackDto
  ) {
      List<BaselineUpdateBreakdown> listEtapas = new ArrayList<>();

      // Processa cada filho do root (cada "etapa")
      for (WorkpackResultDto etapaNode : workpackDto.getChildren()) {
          List<BaselineUpdateBreakdown> resultado = processNodeRecursive(etapaNode, updates);
          // processNodeRecursive retorna lista: para um nó composto retorna 1 elemento (o nó com filhos),
          // para Deliverable/Milestone retorna 0..N elementos (um por UpdateObject).
          listEtapas.addAll(resultado);
      }

      return listEtapas;
  }

  /**
   * Processa um nó (etapa / organizer / deliverable / milestone) recursivamente.
   * Retorna uma lista de BaselineUpdateBreakdown:
   *  - Se o nó é Deliverable/Milestone: retorna um elemento por UpdateObject relevante (pode ser vazio).
   *  - Se o nó é outro tipo (ex: Organizer / Etapa): tenta agregar resultados dos filhos.
   *    Se houver filhos válidos, retorna uma lista com 1 elemento: o breakdown do nó contendo os filhos.
   *    Se não houver filhos válidos, retorna lista vazia.
   */
  private List<BaselineUpdateBreakdown> processNodeRecursive(
      WorkpackResultDto node,
      List<UpdateObject> updates
  ) {
      // Null-safe check do tipo
      String type = node.getType();

      // Caso Deliverable ou Milestone: criar um Breakdown por UpdateObject relevante
      if ("Deliverable".equals(type) || "Milestone".equals(type)) {
          List<UpdateObject> relevant = updates.stream()
              .filter(u -> Objects.equals(u.getIdWorkpack(), node.getId()) ||
                          Objects.equals(u.getIdMaster(), node.getId()))
              .collect(Collectors.toList()); 

          List<BaselineUpdateBreakdown> results = new ArrayList<>();
          for (UpdateObject u : relevant) {
              BaselineUpdateBreakdown bd = new BaselineUpdateBreakdown(
                  node.getId(),
                  node.getIdWorkpackModel(),
                  node.getIdPlan(),
                  node.getName(),
                  node.getFullName(),
                  node.getFontIcon(),
                  node.getModelName(),
                  node.getModelNameInPlural(),
                  node.getType(),
                  u.getClassification()
              );
              if ("Deliverable".equals(type)) {
                  bd.setDeliveryModelHasActiveSchedule(u.getDeliveryModelHasActiveSchedule());
                  if (u.getIsFromAnOldBaseline()) bd.setIsFromAnOldBaseline(true);
              }
              results.add(bd);
          }
          return results; // pode ser vazio
      }

      // Para outros tipos (Organizer, Etapa, etc.) -> montar um nó agregador
      BaselineUpdateBreakdown aggregator = new BaselineUpdateBreakdown(
          node.getId(),
          node.getIdWorkpackModel(),
          node.getIdPlan(),
          node.getName(),
          node.getFullName(),
          node.getFontIcon(),
          node.getModelName(),
          node.getModelNameInPlural(),
          node.getType()
      );

      // Percorre filhos recursivamente e agrega os breakdowns retornados
      if (node.getChildren() != null) {
          for (WorkpackResultDto child : node.getChildren()) {
              List<BaselineUpdateBreakdown> childResults = processNodeRecursive(child, updates);
              for (BaselineUpdateBreakdown cr : childResults) {
                  aggregator.addChild(cr); // addChild deve anexar o breakdown filho corretamente
              }
          }
      }

      // só retorna o aggregator se tiver filhos válidos
      if (aggregator.getChildren() != null && aggregator.getChildren().size() > 0) {
          List<BaselineUpdateBreakdown> out = new ArrayList<>(1);
          out.add(aggregator);
          return out;
      } else {
          return new ArrayList<>(); // vazio: nada relevante sob esse nó
      }
  }
}
