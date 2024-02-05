package br.gov.es.openpmo.dto.dashboards;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.model.dashboards.Dashboard;

@QueryResult
public class DashboardWorkpackDto {
    private Long idWorkpack;
    private Dashboard dashboard;

    public DashboardWorkpackDto(Long idWorkpack, Dashboard dashboard) {
        this.idWorkpack = idWorkpack;
        this.dashboard = dashboard;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }
}
