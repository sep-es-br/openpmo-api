package br.gov.es.openpmo.dto.schedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.model.schedule.Step;

@QueryResult
public class ScheduleWithStepsDto {
    
    private Long idSchedule;
    private LocalDate scheduleStart;
    private LocalDate scheduleEnd;
    private List<Step> steps;
    
    public ScheduleWithStepsDto() {}

    public Long getIdSchedule() {
        return idSchedule;
    }
    
    public void setIdSchedule(Long idSchedule) {
        this.idSchedule = idSchedule;
    }

    public LocalDate getScheduleStart() {
        return scheduleStart;
    }
    public void setScheduleStart(LocalDate scheduleStart) {
        this.scheduleStart = scheduleStart;
    }
    public LocalDate getScheduleEnd() {
        return scheduleEnd;
    }
    public void setScheduleEnd(LocalDate scheduleEnd) {
        this.scheduleEnd = scheduleEnd;
    }
    public List<Step> getSteps() {
        return steps;
    }
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
