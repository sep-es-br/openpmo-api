package br.gov.es.openpmo.dto.dashboards;

import org.springframework.data.neo4j.annotation.QueryResult;


@QueryResult
public class MilestoneDateDto {

    private Long idWorkpack;
    private Boolean completed;
    private String milestoneDate;

    private String snapshotDate;

    public MilestoneDateDto(Boolean completed, String milestoneDate, String snapshotDate) {
        this.completed = completed;
        this.milestoneDate = milestoneDate;
        this.snapshotDate = snapshotDate;
    }

    public MilestoneDateDto() {
    }

    public MilestoneDateDto(Long idWorkpack, Boolean completed, String milestoneDate, String snapshotDate) {
        this.idWorkpack = idWorkpack;
        this.completed = completed;
        this.milestoneDate = milestoneDate;
        this.snapshotDate = snapshotDate;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(String milestoneDate) {
        this.milestoneDate = milestoneDate;
    }

    public String getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(String snapshotDate) {
        this.snapshotDate = snapshotDate;
    }
}
