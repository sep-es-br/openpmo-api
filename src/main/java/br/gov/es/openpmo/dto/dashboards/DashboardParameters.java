package br.gov.es.openpmo.dto.dashboards;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;

public class DashboardParameters {

    private final Boolean showHeader;

    private final Long workpackId;

    private final Long baselineId;

    private final YearMonth yearMonth;

    private final UriComponentsBuilder uriComponentsBuilder;

    public DashboardParameters(
            final Boolean showHeader,
            final Long workpackId,
            final Long baselineId,
            final YearMonth date,
            final UriComponentsBuilder uriComponentsBuilder
    ) {
        this.showHeader = showHeader;
        this.workpackId = workpackId;
        this.baselineId = baselineId;
        this.yearMonth = date;
        this.uriComponentsBuilder = uriComponentsBuilder;
    }

    public Long getWorkpackId() {
        return this.workpackId;
    }

    public Long getBaselineId() {
        return this.baselineId;
    }

    public YearMonth getYearMonth() {
        return this.yearMonth;
    }

    public UriComponentsBuilder getUriComponentsBuilder() {
        return this.uriComponentsBuilder;
    }

    public Boolean getShowHeader() {
        return this.showHeader;
    }

}
