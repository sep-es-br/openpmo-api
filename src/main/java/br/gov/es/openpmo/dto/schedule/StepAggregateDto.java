package br.gov.es.openpmo.dto.schedule;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class StepAggregateDto {

    private Integer stepDate; 
    private Long masterScheduleId;
    private Long masterStepId;
    private Long snapshotStepId;
    private Long snapshotScheduleId;
    private Float masterActualWork;
    private Float masterPlannedWork;
    private Float snapshotPlannedWork;

    private List<ConsumesCostDto> consumeCostMaster;
    private List<ConsumesCostDto> consumeCostBaseLine;

    public StepAggregateDto(Integer stepDate,
                        Long masterScheduleId,
                        Long masterStepId,
                        Long snapshotStepId,
                        Long snapshotScheduleId,
                        Float masterActualWork,
                        Float masterPlannedWork,
                        Float snapshotPlannedWork) {
    this.stepDate = stepDate;
    this.masterScheduleId = masterScheduleId;
    this.masterStepId = masterStepId;
    this.snapshotStepId = snapshotStepId;
    this.snapshotScheduleId = snapshotScheduleId;
    this.masterActualWork = masterActualWork;
    this.masterPlannedWork = masterPlannedWork;
    this.snapshotPlannedWork = snapshotPlannedWork;
    }




    public StepAggregateDto() {
    }

    

    public List<ConsumesCostDto> getConsumeCostMaster() {
        return consumeCostMaster;
    }


    public void setConsumeCostMaster(List<ConsumesCostDto> consumeCostMaster) {
        this.consumeCostMaster = consumeCostMaster;
    }


    public List<ConsumesCostDto> getConsumeCostBaseLine() {
        return consumeCostBaseLine;
    }




    public void setConsumeCostBaseLine(List<ConsumesCostDto> consumeCostBaseLine) {
        this.consumeCostBaseLine = consumeCostBaseLine;
    }




    public Integer getStepDate() {
        return stepDate;
    }

    public void setStepDate(Integer stepDate) {
        this.stepDate = stepDate;
    }

    public Long getMasterScheduleId() {
        return masterScheduleId;
    }

    public void setMasterScheduleId(Long masterScheduleId) {
        this.masterScheduleId = masterScheduleId;
    }

    public Long getMasterStepId() {
        return masterStepId;
    }

    public void setMasterStepId(Long masterStepId) {
        this.masterStepId = masterStepId;
    }

    public Long getSnapshotStepId() {
        return snapshotStepId;
    }

    public void setSnapshotStepId(Long snapshotStepId) {
        this.snapshotStepId = snapshotStepId;
    }

    public Long getSnapshotScheduleId() {
        return snapshotScheduleId;
    }

    public void setSnapshotScheduleId(Long snapshotScheduleId) {
        this.snapshotScheduleId = snapshotScheduleId;
    }

    public Float getMasterActualWork() {
        return masterActualWork;
    }

    public void setMasterActualWork(Float masterActualWork) {
        this.masterActualWork = masterActualWork;
    }

    public Float getMasterPlannedWork() {
        return masterPlannedWork;
    }

    public void setMasterPlannedWork(Float masterPlannedWork) {
        this.masterPlannedWork = masterPlannedWork;
    }

    public Float getSnapshotPlannedWork() {
        return snapshotPlannedWork;
    }

    public void setSnapshotPlannedWork(Float snapshotPlannedWork) {
        this.snapshotPlannedWork = snapshotPlannedWork;
    }
}

