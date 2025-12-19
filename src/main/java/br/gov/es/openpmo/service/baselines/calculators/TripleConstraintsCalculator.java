package br.gov.es.openpmo.service.baselines.calculators;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PLAN_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.baselines.TripleConstraintDto;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineCostDetail;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineScheduleDetail;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineScopeDetail;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.CostDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScopeDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.StepCollectedData;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintBreakdown;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintOutput;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.UnitMeasureRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

@Component
public class TripleConstraintsCalculator implements ITripleConstraintsCalculator {
  private static final String DELIVERABLE = "Deliverable";

  private static final String MILESTONE = "Milestone";

  private static final String ORGANIZER = "Organizer";

  private final BaselineRepository repository;

  private final ApplicationCacheUtil cacheUtil;

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final OfficeRepository officeRepository;

  private final UnitMeasureRepository unitMeasureRepository;

  @Autowired
  public TripleConstraintsCalculator(
    final BaselineRepository repository,
    final ApplicationCacheUtil cacheUtil,
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final OfficeRepository officeRepository,
    final UnitMeasureRepository unitMeasureRepository
  ) {
    this.repository = repository;
    this.cacheUtil = cacheUtil;
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.officeRepository = officeRepository;
    this.unitMeasureRepository = unitMeasureRepository;
  }

  @Override
  public TripleConstraintOutput calculate(final Long idBaseline) {
    final Workpack master = this.findProjectMasterOfBaseline(idBaseline);

    boolean isCancelationBaseline = this.isCancelationBaseline(idBaseline);

    final Long idBaselineReference = this.findPreviousBaseline(idBaseline, master).map(Baseline::getId).orElse(null);
    final boolean hasPreviousBaseline = idBaselineReference != null;

    List<TripleConstraintDto> proposed = isCancelationBaseline ? getTripleConstraintDtoMaster(idBaseline) : getTripleConstraintDto(idBaseline);
    List<TripleConstraintDto> current = null;
    if (hasPreviousBaseline) {
      current = getTripleConstraintDto(idBaselineReference);
    }

    List<Long> ids = new ArrayList<>(0);
    List<EntityDto> unityMeasure = new ArrayList<>(0);
    if (CollectionUtils.isNotEmpty(proposed)) {
      ids.addAll(
        proposed
          .stream()
          .filter(p -> DELIVERABLE.equals(p.getType()))
          .map(TripleConstraintDto::getIdWorkpack)
          .collect(Collectors.toList())
      );
    }

    if (!ids.isEmpty()) {
      unityMeasure.addAll(repository.findUnitMeasureNameOfDeliverableWorkpack(ids));
    }

    Workpack project = this.baselineRepository.findWorkpackByBaselineId(idBaseline).orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
    final Long idProject = project.getId();
    Plan plan = this.workpackRepository.findPlanByWorkpackId(idProject);
    if (plan == null) throw new NegocioException(PLAN_NOT_FOUND);
    final Long idPlan = plan.getId();

    return this.buildCostDetail(unityMeasure, proposed, current, hasPreviousBaseline, idBaseline, idProject, idPlan);
  }

  private TripleConstraintOutput buildCostDetail(
    final List<EntityDto> unityMeasure,
    final List<TripleConstraintDto> proposedWorkpacksConstraint,
    final List<TripleConstraintDto> currentWorkpacksConstraint,
    final boolean hasPreviousBaseline,
    final Long idProposedBaseline,
    final Long idProject,
    final Long idPlan
  ) {
    BaselineCostDetail costDetail = getBaselineCostDetail(proposedWorkpacksConstraint, currentWorkpacksConstraint);
    BaselineScheduleDetail scheduleDetail = getBaselineScheduleDetail(proposedWorkpacksConstraint, currentWorkpacksConstraint);
    BaselineScopeDetail scopeDetail = getBaselineScopeDetail(unityMeasure, proposedWorkpacksConstraint, currentWorkpacksConstraint, hasPreviousBaseline);

    // Aqui monta uma lista de Ids de workpacks que não sofreram alterações
    List<Long> idsWorkpacksUnchanged = new ArrayList<Long>();
    if (currentWorkpacksConstraint != null && currentWorkpacksConstraint.size() > 0) {
      currentWorkpacksConstraint
        .stream()
        .forEach(item -> {
          TripleConstraintDto proposedEquivalent = proposedWorkpacksConstraint.stream().filter(el -> el.getIdWorkpack().equals(item.getIdWorkpack())).findFirst().orElse(null);

          if (
            proposedEquivalent != null &&
            (
              (item.getSumPlannedCost() != null && proposedEquivalent.getSumPlannedCost() != null)
              ? item.getSumPlannedCost().equals(proposedEquivalent.getSumPlannedCost())
              : true
            ) &&
            (
              (item.getSumPlannedWork() != null && proposedEquivalent.getSumPlannedWork() != null)
              ? item.getSumPlannedWork().equals(proposedEquivalent.getSumPlannedWork())
              : true
            ) &&
            (
              (item.getStart() != null && proposedEquivalent.getStart() != null)
              ? item.getStart().equals(proposedEquivalent.getStart())
              : true
            ) &&
            (
              (item.getEnd() != null && proposedEquivalent.getEnd() != null)
              ? item.getEnd().equals(proposedEquivalent.getEnd())
              : true
            ) &&
            (
              (item.getDate() != null && proposedEquivalent.getDate() != null)
              ? item.getDate().equals(proposedEquivalent.getDate())
              : true
            )
          ) {
            idsWorkpacksUnchanged.add(item.getIdWorkpack());
          }
        });
    }

    // Aqui percorre todos os workpacks associados à LB proposta, comparando os snapshots com seus masters, e definindo os excluídos e os à cancelar
    List<Long> idsWorkpacksDeleted = new ArrayList<Long>();
    List<Long> idsWorkpacksToBeCanceled = new ArrayList<Long>();
    List<Workpack> baselineSnapshots = this.baselineRepository.getBaselineWorkpacks(idProposedBaseline);

    baselineSnapshots.forEach(snapshot -> {
      Workpack master = snapshot.getWorkpackMaster();

      if (master.isDeleted() || master.isCanceled()) {
        idsWorkpacksDeleted.add(snapshot.getId());
      } else if (snapshot.isCanceled()) {
        idsWorkpacksToBeCanceled.add(snapshot.getId());
      }
    });

    WorkpackResultDto workpackDto = null;

    workpackDto = cacheUtil.getWorkpackBreakdownStructure(idProject, idPlan, true);

    List<TripleConstraintBreakdown> finalList = createTripleConstraintBreakdown(
      proposedWorkpacksConstraint,
      currentWorkpacksConstraint,
      costDetail,
      scheduleDetail,
      scopeDetail,
      workpackDto,
      idsWorkpacksUnchanged,
      idsWorkpacksDeleted,
      idsWorkpacksToBeCanceled
    );

    Office currentOffice = this.officeRepository.findOfficeByPlanId(idPlan).orElse(null);
    if (currentOffice == null) {
      throw new NegocioException(OFFICE_NOT_FOUND);
    }

    List<UnitMeasure> officeUnitMeasures = this.unitMeasureRepository.findByOffice(currentOffice.getId(), null, null);
    return new TripleConstraintOutput(costDetail, scheduleDetail, scopeDetail, finalList, officeUnitMeasures);
  }

  private BaselineCostDetail getBaselineCostDetail(final List<TripleConstraintDto> proposed, final List<TripleConstraintDto> current) {
    BaselineCostDetail detail = new BaselineCostDetail();
    if (CollectionUtils.isNotEmpty(proposed)) {
      proposed.stream().filter(pr -> DELIVERABLE.equals(pr.getType())).forEach(p -> {
        BigDecimal proposedCost = p.getSumPlannedCost();
        BigDecimal currentCost = null;
        if (CollectionUtils.isNotEmpty(current)) {
          TripleConstraintDto currentDto = current.stream().filter(c -> c.getIdWorkpack().equals(p.getIdWorkpack())).findFirst().orElse(null);
          if (currentDto != null) {
            currentCost = currentDto.getSumPlannedCost();
          }
        }
        detail.addDetail(new CostDetailItem(p.getIdWorkpack(), p.getFontIcon(), p.getName(), currentCost, proposedCost));
      });
    }
    return detail;
  }

  private BaselineScheduleDetail getBaselineScheduleDetail(List<TripleConstraintDto> proposed, List<TripleConstraintDto> current) {
    BaselineScheduleDetail detail = new BaselineScheduleDetail();
    proposed.forEach(p -> {
      if (DELIVERABLE.equals(p.getType())) {
        ScheduleInterval proposedIntervalDate = new ScheduleInterval(p.getStart(), p.getEnd());
        ScheduleInterval currentIntervalDate = null;

        if (CollectionUtils.isNotEmpty(current)) {
          TripleConstraintDto currentDto = current.stream().filter(c -> c.getIdWorkpack().equals(p.getIdWorkpack())).findFirst().orElse(null);
          if (currentDto != null) {
            currentIntervalDate = new ScheduleInterval(currentDto.getStart(), currentDto.getEnd());
          }
        }
        ScheduleDetailItem item = new ScheduleDetailItem(p.getIdWorkpack(), p.getFontIcon(), p.getName(), proposedIntervalDate, currentIntervalDate);
        detail.addScheduleItem(item);
      }

      if (MILESTONE.equals(p.getType())) {
        ScheduleInterval proposedIntervalDate = new ScheduleInterval(p.getDate().toLocalDate(), p.getDate().toLocalDate());
        ScheduleInterval currentIntervalDate = null;

        if (CollectionUtils.isNotEmpty(current)) {
          TripleConstraintDto currentDto = current.stream().filter(c -> c.getIdWorkpack().equals(p.getIdWorkpack())).findFirst().orElse(null);
          if (currentDto != null) {
            currentIntervalDate = new ScheduleInterval(currentDto.getDate().toLocalDate(), currentDto.getDate().toLocalDate());
          }
        }
        ScheduleDetailItem item = new ScheduleDetailItem(p.getIdWorkpack(), p.getFontIcon(), p.getName(), proposedIntervalDate, currentIntervalDate);
        detail.addScheduleItem(item);
      }
    });

    return detail;
  }

  private BaselineScopeDetail getBaselineScopeDetail(
    final List<EntityDto> unitiesMeasure,
    final List<TripleConstraintDto> proposed,
    final List<TripleConstraintDto> current,
    final boolean hasPreviousBaseline
  ) {
    BaselineScopeDetail detail = new BaselineScopeDetail();
    proposed.forEach(p -> {
      StepCollectedData data = new StepCollectedData();

      if (DELIVERABLE.equals(p.getType())) {
        EntityDto unitMeasure = unitiesMeasure.stream().filter(u -> u.getId().equals(p.getIdWorkpack())).findFirst().orElse(null);
        String unityName = unitMeasure != null ? unitMeasure.getName() : "";
        data.cost.addProposedValue(p.getSumPlannedCost());
        data.work.addProposedValue(p.getSumPlannedWork());
        if (CollectionUtils.isNotEmpty(current)) {
          TripleConstraintDto currentDto = current.stream().filter(c -> c.getIdWorkpack().equals(p.getIdWorkpack())).findFirst().orElse(null);
          if (currentDto != null) {
            data.cost.addCurrentValue(currentDto.getSumPlannedCost());
            data.work.addCurrentValue(currentDto.getSumPlannedWork());
          }
        }

        detail.addDetail(new ScopeDetailItem(p.getIdWorkpack(), p.getFontIcon(), p.getName(), unityName, data, hasPreviousBaseline));
      }
    });

    return detail;
  }

  private List<TripleConstraintDto> getTripleConstraintDto(final Long idBaseline) {
    if (idBaseline == null) return new ArrayList<>(0);
    final List<TripleConstraintDto> list = repository.findAllTripleConstraintSnapshot(idBaseline);

    final List<TripleConstraintDto> listScheduleAndPlannedWork = repository.findAllTripleConstraintSnapshotScheduleAndPlannedWork(idBaseline);
    listScheduleAndPlannedWork.forEach(work -> list
      .stream()
      .filter(tri -> tri.getIdWorkpack().equals(work.getIdWorkpack()))
      .findFirst()
      .ifPresent(
        t -> {
          t.setStart(work.getStart());
          t.setEnd(work.getEnd());
          t.setSumPlannedWork(work.getSumPlannedWork());
        }
      )
    );

    final List<TripleConstraintDto> listCost = repository.findAllTripleConstraintSnapshotScheduleAndPlannedCost(idBaseline);
    listCost.forEach(cost -> list
      .stream()
      .filter(tri -> tri.getIdWorkpack().equals(cost.getIdWorkpack()))
      .findFirst()
      .ifPresent(
        t -> t.setSumPlannedCost(cost.getSumPlannedCost())
      )
    );

    return list;
  }

  private List<TripleConstraintDto> getTripleConstraintDtoMaster(final Long idBaseline) {
    if (idBaseline == null) return new ArrayList<>(0);
    final List<TripleConstraintDto> list = repository.findAllTripleConstraint(idBaseline);

    final List<TripleConstraintDto> listScheduleAndPlannedWork = repository.findAllTripleConstraintScheduleAndPlannedWork(idBaseline);
    listScheduleAndPlannedWork.forEach(
        work -> list.stream().filter(tri -> tri.getIdWorkpack().equals(work.getIdWorkpack())).findFirst().ifPresent(
            t -> {
              t.setEnd(work.getEnd());
              t.setStart(work.getStart());
              t.setSumPlannedWork(work.getSumPlannedWork());
            }));

    final List<TripleConstraintDto> listCost = repository.findAllTripleConstraintScheduleAndPlannedCost(idBaseline);
    listCost.forEach(
        cost -> list.stream().filter(tri -> tri.getIdWorkpack().equals(cost.getIdWorkpack())).findFirst().ifPresent(
            t -> t.setSumPlannedCost(cost.getSumPlannedCost())));
    return list;
  }

  private boolean isCancelationBaseline(final Long idBaseline) {
    return this.repository.isCancelBaseline(idBaseline);
  }

  private Optional<Baseline> findPreviousBaseline(
    final Long idBaseline,
    final Workpack master
  ) {
    return this.repository.findPreviousBaseline(idBaseline, master.getId());
  }

  private Workpack findProjectMasterOfBaseline(final Long idBaseline) {
    return this.repository.findWorkpackByBaselineIdThin(idBaseline)
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  public List<TripleConstraintBreakdown> createTripleConstraintBreakdown(
    List<TripleConstraintDto> proposedLBWorkpacks,
    List<TripleConstraintDto> currentLBWorkpacks,
    BaselineCostDetail costDetail,
    BaselineScheduleDetail scheduleDetail,
    BaselineScopeDetail scopeDetail,
    WorkpackResultDto workpackDto,
    List<Long> idsWorkpacksUnchanged,
    List<Long> idsWorkpacksDeleted,
    List<Long> idsWorkpacksToBeCanceled
  ) {
    List<CostDetailItem> costItems = costDetail.getCostDetails();
    List<ScheduleDetailItem> scheduleItems = scheduleDetail.getScheduleDetails();
    List<ScopeDetailItem> scopeItems = scopeDetail.getScopeDetails();
    List<TripleConstraintBreakdown> listEtapas = new ArrayList<>(0);

    for (WorkpackResultDto etapa : workpackDto.getChildren()) {
      TripleConstraintBreakdown etapaBreakdown = new TripleConstraintBreakdown(
        etapa.getId(),
        etapa.getIdPlan(),
        etapa.getName(),
        etapa.getFullName(),
        etapa.getFontIcon(),
        etapa.getType(),
        etapa.getModelName(),
        etapa.getModelNameInPlural()
      );

      for (WorkpackResultDto child : etapa.getChildren()) {
        if (child.getType().equals(ORGANIZER)) {
          TripleConstraintBreakdown subEtapaBreakdown = new TripleConstraintBreakdown(
            child.getId(),
            child.getIdPlan(),
            child.getName(),
            child.getFullName(),
            child.getFontIcon(),
            child.getType(),
            child.getModelName(),
            child.getModelNameInPlural()
          );

          for (WorkpackResultDto deliveryOrMilestone : child.getChildren()) {
            if (
              !idsWorkpacksDeleted.contains(deliveryOrMilestone.getId()) &&
              (              
                proposedLBWorkpacks.stream().filter(w -> w.getIdWorkpack().equals(deliveryOrMilestone.getId())).findFirst().isPresent() ||
                currentLBWorkpacks.stream().filter(w -> w.getIdWorkpack().equals(deliveryOrMilestone.getId())).findFirst().isPresent() ||
                idsWorkpacksUnchanged.stream().filter(id -> id.equals(deliveryOrMilestone.getId())).findFirst().isPresent() ||
                idsWorkpacksDeleted.stream().filter(id -> id.equals(deliveryOrMilestone.getId())).findFirst().isPresent() ||
                idsWorkpacksToBeCanceled.stream().filter(id -> id.equals(deliveryOrMilestone.getId())).findFirst().isPresent()
              )
            ) {
              CostDetailItem costItem = costItems
                .stream()
                .filter(item -> item.getIdWorkpack().equals(deliveryOrMilestone.getId()))
                .findFirst()
                .orElse(null);
  
              ScheduleDetailItem scheduleItem = scheduleItems
                .stream()
                .filter(item -> item.getIdWorkpack().equals(deliveryOrMilestone.getId()))
                .findFirst()
                .orElse(null);
  
              ScopeDetailItem scopeItem = scopeItems
                .stream()
                .filter(item -> item.getIdWorkpack().equals(deliveryOrMilestone.getId()))
                .findFirst()
                .orElse(null);
              
              TripleConstraintBreakdown deliveryOrMilestoneBreakdown = new TripleConstraintBreakdown(
                deliveryOrMilestone.getId(),
                deliveryOrMilestone.getIdPlan(),
                deliveryOrMilestone.getName(),
                deliveryOrMilestone.getFullName(),
                deliveryOrMilestone.getFontIcon(),
                deliveryOrMilestone.getType(),
                deliveryOrMilestone.getModelName(),
                deliveryOrMilestone.getModelNameInPlural()
              );
  
              if (costItem != null || scheduleItem != null || scopeItem != null) {  
                if (deliveryOrMilestone.getType().equals(MILESTONE)) {
                  deliveryOrMilestoneBreakdown.setScheduleDetails(scheduleItem);
                } else if (deliveryOrMilestone.getType().equals(DELIVERABLE)) {
                  deliveryOrMilestoneBreakdown.setCostDetails(costItem);
                  deliveryOrMilestoneBreakdown.setScheduleDetails(scheduleItem);
                  deliveryOrMilestoneBreakdown.setScopeDetails(scopeItem);
                }
              }
              
              if (idsWorkpacksUnchanged.contains(deliveryOrMilestone.getId())) {
                deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.UNCHANGED);
              }
              if (idsWorkpacksToBeCanceled.contains(deliveryOrMilestone.getId())) {
                deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.TO_CANCEL);;
              }
              // if (idsWorkpacksDeleted.contains(deliveryOrMilestone.getId())) {
              //   deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.DELETED);
              // }
              subEtapaBreakdown.addChild(deliveryOrMilestoneBreakdown);
            }
          };

          if (subEtapaBreakdown.getChildren().size() > 0) {
            etapaBreakdown.addChild(subEtapaBreakdown);
          }
        } else if (
          (
            (child.getType().equals(DELIVERABLE)) ||
            (child.getType().equals(MILESTONE))
          ) &&
          !idsWorkpacksDeleted.contains(child.getId()) &&
          (
            proposedLBWorkpacks.stream().filter(w -> w.getIdWorkpack().equals(child.getId())).findFirst().isPresent() ||
            currentLBWorkpacks.stream().filter(w -> w.getIdWorkpack().equals(child.getId())).findFirst().isPresent() ||
            idsWorkpacksUnchanged.stream().filter(id -> id.equals(child.getId())).findFirst().isPresent() ||
            idsWorkpacksDeleted.stream().filter(id -> id.equals(child.getId())).findFirst().isPresent() ||
            idsWorkpacksToBeCanceled.stream().filter(id -> id.equals(child.getId())).findFirst().isPresent()
          )
        ) {
          CostDetailItem costItem = costItems
            .stream()
            .filter(item -> item.getIdWorkpack().equals(child.getId()))
            .findFirst()
            .orElse(null);

          ScheduleDetailItem scheduleItem = scheduleItems
            .stream()
            .filter(item -> item.getIdWorkpack().equals(child.getId()))
            .findFirst()
            .orElse(null);

          ScopeDetailItem scopeItem = scopeItems
            .stream()
            .filter(item -> item.getIdWorkpack().equals(child.getId()))
            .findFirst()
            .orElse(null);

          TripleConstraintBreakdown deliveryOrMilestoneBreakdown = new TripleConstraintBreakdown(
            child.getId(),
            child.getIdPlan(),
            child.getName(),
            child.getFullName(),
            child.getFontIcon(),
            child.getType(),
            child.getModelName(),
            child.getModelNameInPlural()
          );

          if (costItem != null || scheduleItem != null || scopeItem != null) {
            if (child.getType().equals(MILESTONE)) {
              deliveryOrMilestoneBreakdown.setScheduleDetails(scheduleItem);
            } else if (child.getType().equals(DELIVERABLE)) {
              deliveryOrMilestoneBreakdown.setCostDetails(costItem);
              deliveryOrMilestoneBreakdown.setScheduleDetails(scheduleItem);
              deliveryOrMilestoneBreakdown.setScopeDetails(scopeItem);
            }
          }

          if (idsWorkpacksUnchanged.contains(child.getId())) {
            deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.UNCHANGED);
          }
          if (idsWorkpacksToBeCanceled.contains(child.getId())) {
              deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.TO_CANCEL);;
            }
          // if (idsWorkpacksDeleted.contains(child.getId())) {
          //   deliveryOrMilestoneBreakdown.setWorkpackStatus(BaselineStatus.DELETED);
          // }
          etapaBreakdown.addChild(deliveryOrMilestoneBreakdown);
        }
      }

      if (etapaBreakdown.getChildren().size() > 0) {
        listEtapas.add(etapaBreakdown);
      }
    }

    return listEtapas;
  };
}
