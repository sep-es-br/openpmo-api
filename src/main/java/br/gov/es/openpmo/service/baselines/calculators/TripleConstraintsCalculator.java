package br.gov.es.openpmo.service.baselines.calculators;

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
import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintOutput;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;

@Component
public class TripleConstraintsCalculator implements ITripleConstraintsCalculator {

  private static final String DELIVERABLE = "Deliverable";
  private static final String MILESTONE = "Milestone";

  private final BaselineRepository repository;

  @Autowired
  public TripleConstraintsCalculator(
    final BaselineRepository repository
  ) {
    this.repository = repository;
  }

  @Override
  public TripleConstraintOutput calculate(final Long idBaseline) {
    final Workpack master = this.findProjectMasterOfBaseline(idBaseline);

    boolean isCancelationBaseline = this.isCancelationBaseline(idBaseline);

    final Long idBaselineReference = this.findPreviousBaseline(idBaseline, master)
                                         .map(Baseline::getId)
                                         .orElse(null);
    final boolean hasPreviousBaseline = idBaselineReference != null;

    List<TripleConstraintDto> proposed = isCancelationBaseline ? getTripleConstraintDtoMaster(idBaseline) : getTripleConstraintDto(idBaseline);
    List<TripleConstraintDto> current = null;
    if (hasPreviousBaseline) {
      current = getTripleConstraintDto(idBaselineReference);
    }
    List<Long> ids = new ArrayList<>(0);
    List<EntityDto> unityMeasure = new ArrayList<>(0);
    if (CollectionUtils.isNotEmpty(proposed)) {
      ids.addAll(proposed.stream().filter(p -> DELIVERABLE.equals(p.getType())).map(
          TripleConstraintDto::getIdWorkpack).collect(Collectors.toList()));
    }
    if (!ids.isEmpty()) {
      unityMeasure.addAll(repository.findUnitMeasureNameOfDeliverableWorkpack(ids));
    }

    return this.buildCostDetail(unityMeasure, proposed, current, hasPreviousBaseline);
  }

  private TripleConstraintOutput buildCostDetail(final List<EntityDto> unityMeasure
      , final List<TripleConstraintDto> proposed, final List<TripleConstraintDto> current, final boolean hasPreviousBaseline) {
    BaselineCostDetail costDetail = getBaselineCostDetail(proposed, current);
    BaselineScheduleDetail scheduleDetail = getBaselineScheduleDetail(proposed, current);
    BaselineScopeDetail scopeDetail = getBaselineScopeDetail(unityMeasure, proposed, current, hasPreviousBaseline);
    return new TripleConstraintOutput(costDetail, scheduleDetail, scopeDetail);
  }

  private BaselineScopeDetail getBaselineScopeDetail(final List<EntityDto> unitiesMeasure
      , final List<TripleConstraintDto> proposed, final  List<TripleConstraintDto> current, final boolean hasPreviousBaseline) {
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
        detail.addDetail(new ScopeDetailItem(p.getFontIcon(), p.getName(), unityName, data, hasPreviousBaseline));
      }

    });
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
        ScheduleDetailItem item = new ScheduleDetailItem(p.getFontIcon(), p.getName(), proposedIntervalDate, currentIntervalDate);
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
        ScheduleDetailItem item = new ScheduleDetailItem(p.getFontIcon(), p.getName(), proposedIntervalDate, currentIntervalDate);
        detail.addScheduleItem(item);

      }
    });
    return detail;
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
        detail.addDetail(new CostDetailItem(p.getFontIcon(), p.getName(), currentCost, proposedCost));
      });
    }
    return detail;
  }

  private List<TripleConstraintDto> getTripleConstraintDto(final Long idBaseline) {
    if (idBaseline == null) return new ArrayList<>(0);
    final List<TripleConstraintDto> list = repository.findAllTripleConstraintSnapshot(idBaseline);

    final List<TripleConstraintDto> listScheduleAndPlannedWork = repository.findAllTripleConstraintSnapshotScheduleAndPlannedWork(idBaseline);
    listScheduleAndPlannedWork.forEach(
        work -> list.stream().filter(tri -> tri.getIdWorkpack().equals(work.getIdWorkpack())).findFirst().ifPresent(
            t -> {
              t.setEnd(work.getEnd());
              t.setStart(work.getStart());
              t.setSumPlannedWork(work.getSumPlannedWork());
            }));

    final List<TripleConstraintDto> listCost = repository.findAllTripleConstraintSnapshotScheduleAndPlannedCost(idBaseline);
    listCost.forEach(
        cost -> list.stream().filter(tri -> tri.getIdWorkpack().equals(cost.getIdWorkpack())).findFirst().ifPresent(
            t -> t.setSumPlannedCost(cost.getSumPlannedCost())));
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

}
