package br.gov.es.openpmo.dto.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Optional;

public class Interval {

    @JsonFormat(pattern = "MM-yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "MM-yyyy")
    private LocalDate endDate;

    public Interval(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Interval(DateIntervalQuery intervalQuery) {
        Optional.ofNullable(intervalQuery)
                .filter(DateIntervalQuery::isValid)
                .map(DateIntervalQuery::getInitialDate)
                .ifPresent(this::setStartDate);

        Optional.ofNullable(intervalQuery)
                .filter(DateIntervalQuery::isValid)
                .map(DateIntervalQuery::getEndDate)
                .ifPresent(this::setEndDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
