package br.gov.es.openpmo.dto.baselines;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineScheduleSubmitDto {

    private Long idWorkpack;
    private Long idSchedule;
    private LocalDate end;
    private LocalDate start;
    private List<BaselineStepSubmitDto> steps = new ArrayList<>(0);

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(Long idSchedule) {
        this.idSchedule = idSchedule;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public List<BaselineStepSubmitDto> getSteps() {
        return steps;
    }

    public void setSteps(List<BaselineStepSubmitDto> steps) {
        this.steps = steps;
    }
}
