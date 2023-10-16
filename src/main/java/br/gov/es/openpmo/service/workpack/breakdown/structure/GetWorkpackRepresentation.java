package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDto;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.ScheduleMeasureUnit;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackRepresentation;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.dashboards.Dashboard;
import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.UnitSelection;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.RiskRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
public class GetWorkpackRepresentation {

  private final BaselineRepository baselineRepository;

  private final RiskRepository riskRepository;

  public GetWorkpackRepresentation(
    final BaselineRepository baselineRepository,
    final RiskRepository riskRepository
  ) {
    this.baselineRepository = baselineRepository;
    this.riskRepository = riskRepository;
  }

  public WorkpackRepresentation execute(final Workpack workpack) {
    final WorkpackRepresentation workpackRepresentation = new WorkpackRepresentation();
    final Long workpackId = workpack.getId();
    workpackRepresentation.setIdWorkpack(workpackId);
    workpackRepresentation.setWorkpackType(workpack.getType());
    workpackRepresentation.setWorkpackName(workpack.getWorkpackName());
    if (this.hasDashboard(workpack)) {
      final Dashboard dashboard = workpack.getDashboard();
      if (dashboard != null) {
        final List<DashboardMonth> months = dashboard.getMonths();
        final LocalDate lastMonth = YearMonth.now().minusMonths(1).atDay(1);
        boolean seen = false;
        DashboardMonth best = null;
        Comparator<DashboardMonth> comparator = Comparator.comparing(DashboardMonth::getDate);
        for (DashboardMonth month : months) {
          if (!month.getDate().isBefore(lastMonth)) {
            if (!seen || comparator.compare(month, best) < 0) {
              seen = true;
              best = month;
            }
          }
        }
        if (seen) {
          Long baselineId = null;
          if (workpack instanceof Project) {
            baselineId = this.getBaselineId(workpack.getId());
          }
          final DashboardMonthDto monthDto = DashboardMonthDto.of(best, baselineId);
          workpackRepresentation.setDashboard(monthDto);
        }
      }
      final List<Risk> risks = this.riskRepository.findByWorkpackId(workpack.getId());
      final List<RiskDto> riskDtos = RiskDto.of(risks);
      workpackRepresentation.setRisks(riskDtos);
    }
    if (workpack instanceof Milestone) {
      workpackRepresentation.setMilestone(MilestoneDto.of((Milestone) workpack));
    }
    if (workpack instanceof Deliverable) {
      final ScheduleMeasureUnit unitMeasure = this.buildUnitMeasure((Deliverable) workpack);
      workpackRepresentation.setUnitMeasure(unitMeasure);
    }
    return workpackRepresentation;
  }

  private boolean hasDashboard(final Workpack workpack) {
    return workpack instanceof Portfolio ||
      workpack instanceof Program ||
      workpack instanceof Project ||
      workpack instanceof Organizer ||
      workpack instanceof Deliverable;
  }

  private ScheduleMeasureUnit buildUnitMeasure(final Deliverable workpack) {
    final Set<Property> properties = workpack.getProperties();
    for (Property property : properties) {
      if (property instanceof UnitSelection) {
        final UnitMeasure value = ((UnitSelection) property).getValue();
        return ScheduleMeasureUnit.of(value);
      }
    }
    return null;
  }

  private Long getBaselineId(final Long workpackId) {
    final List<Baseline> baselines = this.baselineRepository.findApprovedOrProposedBaselinesByAnyWorkpackId(workpackId);
    if (baselines.isEmpty()) {
      return null;
    }
    return baselines.get(0).getId();
  }

}
