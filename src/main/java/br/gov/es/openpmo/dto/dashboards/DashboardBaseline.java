package br.gov.es.openpmo.dto.dashboards;

import java.time.LocalDateTime;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.model.baselines.Status;

@QueryResult
public class DashboardBaseline {

    private Long idWorkpack;
    private Long idBaseline;
    private boolean active;
    private LocalDateTime proposalDate;
    private Status status;

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdBaseline() {
        return idBaseline;
    }

    public void setIdBaseline(Long idBaseline) {
        this.idBaseline = idBaseline;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(LocalDateTime proposalDate) {
        this.proposalDate = proposalDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
