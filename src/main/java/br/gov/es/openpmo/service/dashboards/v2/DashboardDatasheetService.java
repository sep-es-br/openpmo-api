package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpacksByModelResponse;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DashboardDatasheetService implements IDashboardDatasheetService {

  private final DashboardDatasheetRepository repository;

  @Autowired
  public DashboardDatasheetService(final DashboardDatasheetRepository repository) {
    this.repository = repository;
  }

  @Override
  public DatasheetResponse build(final DashboardParameters parameters) {
    final Long workpackId = parameters.getWorkpackId();
    final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();

    return new DatasheetResponse(
      this.getDatasheetTotalizers(workpackId),
      this.getDatasheetStakeholders(workpackId, uriComponentsBuilder)
    );
  }

  private DatasheetTotalizers getDatasheetTotalizers(final Long workpackId) {
    return new DatasheetTotalizers(this.getWorkpackByModel(workpackId));
  }

  private List<WorkpacksByModelResponse> getWorkpackByModel(final Long workpackId) {
    final List<WorkpackByModelQueryResult> queryResults = Optional.ofNullable(workpackId)
      .map(this.repository::workpackByModel)
      .orElse(Collections.emptyList());

    return queryResults.stream()
      .map(WorkpacksByModelResponse::from)
      .collect(Collectors.toList());
  }

  private Set<DatasheetStakeholderResponse> getDatasheetStakeholders(
    final Long workpackId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getStakeholders(workpackId).stream()
      .map(stakeholder -> stakeholder.mapToResponse(uriComponentsBuilder))
      .collect(Collectors.toSet());
  }

  private Set<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
    return this.repository.stakeholders(workpackId);
  }

}
