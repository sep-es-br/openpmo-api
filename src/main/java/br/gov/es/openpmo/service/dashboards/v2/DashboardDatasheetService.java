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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
    final Long workpackModelId = getWorkpackModelId(parameters);
    final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();

    return new DatasheetResponse(
      this.getDatasheetTotalizers(workpackId, workpackModelId),
      this.getDatasheetStakeholders(workpackId, uriComponentsBuilder)
    );
  }

  private static Long getWorkpackModelId(DashboardParameters parameters) {
    if (Boolean.TRUE.equals(parameters.getLinked())) {
      return parameters.getWorkpackModelLinkedId();
    }
    return parameters.getWorkpackModelId();
  }

  private DatasheetTotalizers getDatasheetTotalizers(final Long workpackId, Long workpackModelId) {
    return new DatasheetTotalizers(this.getWorkpackByModel(workpackId, workpackModelId));
  }

  private List<WorkpacksByModelResponse> getWorkpackByModel(final Long workpackId, Long workpackModelId) {
    if (workpackId == null) {
      return Collections.emptyList();
    }
    List<WorkpackByModelQueryResult> queryResults = this.repository.workpackByModel(workpackId, workpackModelId);
    List<WorkpacksByModelResponse> result = new ArrayList<>();
    for (WorkpackByModelQueryResult queryResult : queryResults) {
      WorkpacksByModelResponse from = WorkpacksByModelResponse.from(queryResult);
      result.add(from);
    }
    return result;
  }

  private Set<DatasheetStakeholderResponse> getDatasheetStakeholders(
    final Long workpackId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getStakeholders(workpackId).stream()
      .map(stakeholder -> stakeholder.mapToResponse(uriComponentsBuilder))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
    return new LinkedHashSet<>(this.repository.stakeholders(workpackId));
  }

}
