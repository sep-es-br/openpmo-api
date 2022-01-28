package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetDatasheetDashboardData implements IGetDatasheetDashboardData {

  private final DashboardDatasheetRepository repository;

  @Autowired
  public GetDatasheetDashboardData(final DashboardDatasheetRepository repository) {
    this.repository = repository;
  }

  @Override
  public DatasheetResponse get(final DashboardDataParameters parameters) {
    final Long workpackId = parameters.getIdWorkpack();
    final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();

    final DatasheetTotalizers totalizers = this.getDatasheetTotalizers(workpackId);
    final List<DatasheetStakeholderResponse> stakeholders = this.getDatasheetStakeholders(
        workpackId,
        uriComponentsBuilder
    );

    return new DatasheetResponse(
        totalizers,
        stakeholders
    );
  }

  private DatasheetTotalizers getDatasheetTotalizers(final Long workpackId) {
    final Long projectsQuantity = this.getProjectsQuantity(workpackId);
    final Long deliverablesQuantity = this.getDeliverablesQuantity(workpackId);
    final Long milestoneQuantity = this.getMilestoneQuantity(workpackId);

    return new DatasheetTotalizers(
        projectsQuantity,
        deliverablesQuantity,
        milestoneQuantity
    );
  }

  private List<DatasheetStakeholderResponse> getDatasheetStakeholders(
      final Long workpackId,
      final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getStakeholders(workpackId).stream()
        .map(stakeholder -> stakeholder.mapToResponse(uriComponentsBuilder))
        .collect(Collectors.toList());
  }

  private Long getProjectsQuantity(final Long workpackId) {
    return this.repository.quantityOfProjects(workpackId);
  }

  private Long getDeliverablesQuantity(final Long workpackId) {
    return this.repository.quantityOfDeliverables(workpackId);
  }

  private Long getMilestoneQuantity(final Long workpackId) {
    return this.repository.quantityOfMilestones(workpackId);
  }

  private List<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
    return this.repository.stakeholders(workpackId);
  }

}
