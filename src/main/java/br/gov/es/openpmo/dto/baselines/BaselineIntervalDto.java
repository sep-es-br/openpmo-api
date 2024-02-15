package br.gov.es.openpmo.dto.baselines;

import java.time.LocalDate;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineIntervalDto {
    private Long idBaseline;
    private LocalDate minStart;
    private LocalDate maxEnd;

    public Long getIdBaseline() {
        return idBaseline;
    }

    public void setIdBaseline(Long idBaseline) {
        this.idBaseline = idBaseline;
    }

    public LocalDate getMinStart() {
        return minStart;
    }

    public void setMinStart(LocalDate minStart) {
        this.minStart = minStart;
    }

    public LocalDate getMaxEnd() {
        return maxEnd;
    }

    public void setMaxEnd(LocalDate maxEnd) {
        this.maxEnd = maxEnd;
    }
}
