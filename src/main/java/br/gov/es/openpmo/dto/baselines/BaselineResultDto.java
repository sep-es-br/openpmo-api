package br.gov.es.openpmo.dto.baselines;

import java.time.LocalDateTime;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.model.baselines.Status;

@QueryResult
public class BaselineResultDto {

    private Long idWorkpack;
    private Long idBaseline;
    private LocalDateTime activationDate;
    private LocalDateTime proposalDate;
    private Status status;
    private String name;
    private String description;
    private String message;
    private boolean active;

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

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
