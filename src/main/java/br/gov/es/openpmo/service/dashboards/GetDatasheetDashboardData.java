package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.*;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GetDatasheetDashboardData implements IGetDatasheetDashboardData {

    private final DashboardDatasheetRepository repository;

    @Autowired
    public GetDatasheetDashboardData(final DashboardDatasheetRepository repository) {
        this.repository = repository;
    }

    @Override
    public DatasheetResponse get(final DashboardParameters parameters) {
        final Long workpackId = parameters.getWorkpackId();
        final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();

        return new DatasheetResponse(
                this.getDatasheetTotalizers(workpackId),
                this.getDatasheetStakeholders(workpackId, uriComponentsBuilder));
    }

    private DatasheetTotalizers getDatasheetTotalizers(final Long workpackId) {
        return new DatasheetTotalizers(getChildrenByType(workpackId));
    }

    private List<ChildrenByTypeResponse> getChildrenByType(Long workpackId) {
        final List<ChildrenByTypeQueryResult> queryResults = Optional.ofNullable(workpackId)
                .map(this.repository::childrenByType)
                .orElse(Collections.emptyList());

        return queryResults.stream()
                .map(ChildrenByTypeResponse::from)
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

    private List<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
        return new ArrayList<>(this.repository.stakeholders(workpackId));
    }

}
