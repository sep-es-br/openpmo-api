package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.dashboards.v2.SimpleDashboard;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.MilestoneRepresentation;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.ScheduleMeasureUnit;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.ScheduleRepresentation;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackRepresentation;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.HasValue;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Portfolio;
import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.dashboards.v2.DashboardService;
import br.gov.es.openpmo.service.schedule.ScheduleService;
import br.gov.es.openpmo.service.workpack.MilestoneService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class GetWorkpackRepresentation {

  private final DashboardService dashboardService;

  private final MilestoneService milestoneService;

  private final ScheduleService scheduleService;

  private final ScheduleRepository scheduleRepository;

  private final WorkpackRepository workpackRepository;

  public GetWorkpackRepresentation(
    final DashboardService dashboardService,
    final MilestoneService milestoneService,
    final ScheduleService scheduleService,
    final ScheduleRepository scheduleRepository,
    final WorkpackRepository workpackRepository
  ) {
    this.dashboardService = dashboardService;
    this.milestoneService = milestoneService;
    this.scheduleService = scheduleService;
    this.scheduleRepository = scheduleRepository;
    this.workpackRepository = workpackRepository;
  }

  public WorkpackRepresentation execute(final Workpack workpack) {
    final WorkpackRepresentation workpackRepresentation = new WorkpackRepresentation();
    final Long workpackId = workpack.getId();
    workpackRepresentation.setIdWorkpack(workpackId);
    workpackRepresentation.setWorkpackType(workpack.getType());
    final WorkpackName workpackName = this.workpackRepository.findWorkpackNameAndFullname(workpackId)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NAME_NOT_FOUND));
    workpackRepresentation.setWorkpackName(workpackName.getName());
    if (this.hasDashboard(workpack)) {
      final SimpleDashboard dashboard = this.dashboardService.buildSimple(workpackId);
      workpackRepresentation.setDashboard(dashboard);
    }
    if (workpack instanceof Milestone) {
      final LocalDate milestoneDate = this.milestoneService.getMilestoneDate(workpackId);
      final LocalDate baselineDate = this.milestoneService.getBaselineDate(workpackId);
      final LocalDate expirationDate = this.milestoneService.getExpirationDate(workpackId);
      final MilestoneStatus status = this.milestoneService.getStatus(workpackId);
      final MilestoneRepresentation milestoneRepresentation = new MilestoneRepresentation();
      milestoneRepresentation.setMilestoneDate(milestoneDate);
      milestoneRepresentation.setBaselineDate(baselineDate);
      milestoneRepresentation.setExpirationDate(expirationDate);
      milestoneRepresentation.setMilestoneStatus(status);
      workpackRepresentation.setMilestone(milestoneRepresentation);
    }
    if (workpack instanceof Deliverable) {
      final Optional<Schedule> scheduleOptional = this.scheduleRepository.findScheduleByWorkpackId(workpackId);
      if (scheduleOptional.isPresent()) {
        final Schedule schedule = scheduleOptional.get();
        final ScheduleDto scheduleDto = this.scheduleService.mapsToScheduleDto(schedule);
        final ScheduleRepresentation scheduleRepresentation = new ScheduleRepresentation();
        scheduleRepresentation.setUnitMeasure(this.buildUnitMeasure(workpackId));
        scheduleRepresentation.setEnd(scheduleDto.getEnd());
        scheduleRepresentation.setStart(scheduleDto.getStart());
        scheduleRepresentation.setBaselineEnd(scheduleDto.getBaselineEnd());
        scheduleRepresentation.setBaselineStart(scheduleDto.getBaselineStart());
        scheduleRepresentation.setBaselinePlanned(scheduleDto.getBaselineCost());
        scheduleRepresentation.setBaselineCost(scheduleDto.getBaselineCost());
        scheduleRepresentation.setPlaned(scheduleDto.getPlaned());
        scheduleRepresentation.setActual(scheduleDto.getActual());
        scheduleRepresentation.setPlanedCost(scheduleDto.getPlanedCost());
        scheduleRepresentation.setActualCost(scheduleDto.getActualCost());
        workpackRepresentation.setSchedule(scheduleRepresentation);
      }
    }
    return workpackRepresentation;
  }

  private boolean hasDashboard(final Workpack workpack) {
    return workpack instanceof Portfolio ||
           workpack instanceof Program ||
           workpack instanceof Project ||
           workpack instanceof Organizer;
  }

  private ScheduleMeasureUnit buildUnitMeasure(final Long workpackId) {
    final Optional<Workpack> workpackUnitMeasure = this.workpackRepository.findWorkpackUnitMeasure(workpackId);
    return workpackUnitMeasure
      .map(Workpack::getProperties)
      .flatMap(properties -> properties.stream().findFirst())
      .map(HasValue::getValue)
      .map(unitMeasure -> ScheduleMeasureUnit.of((UnitMeasure) unitMeasure))
      .orElse(null);
  }

}
