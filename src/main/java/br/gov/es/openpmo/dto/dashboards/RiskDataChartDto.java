package br.gov.es.openpmo.dto.dashboards;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class RiskDataChartDto {
    Long count;
    String importance;
    String status;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
