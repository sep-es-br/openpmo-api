package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardMilestoneService implements IDashboardMilestoneService {

  private final DashboardMilestoneRepository repository;

  @Autowired
  public DashboardMilestoneService(final DashboardMilestoneRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<MilestoneDto> build(final DashboardParameters parameters) {
    final Long idBaseline = parameters.getBaselineId();
    final Long idWorkpack = parameters.getWorkpackId();

    final Long planId = idWorkpack == null ? parameters.getPlanId() : null;
    final Long baselineId = idWorkpack == null ? null : idBaseline;
    final List<MilestoneDateDto> milestones = this.repository.findForDashboard(
      planId,
      idWorkpack,
      baselineId
    );
    return MilestoneDto.setMilestonesOfMiletonesDate(milestones);
  }


}
