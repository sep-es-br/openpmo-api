package br.gov.es.openpmo.dto.schedule;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.es.openpmo.model.schedule.Schedule;

@QueryResult
public class ScheduleDto {

  private Long id;
  private LocalDate end;
  private LocalDate start;
  private LocalDate baselineEnd;
  private LocalDate baselineStart;
  private BigDecimal baselinePlaned = BigDecimal.ZERO;
  private BigDecimal baselineCost = BigDecimal.ZERO;
  private List<GroupStepDto> groupStep;
  private Long idWorkpack;

  @JsonIgnore
  private Long idSnapshot;

  public ScheduleDto() {
  }

  public ScheduleDto(Schedule schedule) {
    this.id = schedule.getId();
    this.end = schedule.getEnd();
    this.start = schedule.getStart();
    this.idWorkpack = schedule.getIdWorkpack();
  }


  public LocalDate getBaselineEnd() {
    return this.baselineEnd;
  }

  public void setBaselineEnd(final LocalDate baselineEnd) {
    this.baselineEnd = baselineEnd;
  }

  public LocalDate getBaselineStart() {
    return this.baselineStart;
  }

  public void setBaselineStart(final LocalDate baselineStart) {
    this.baselineStart = baselineStart;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getEnd() {
    return this.end;
  }

  public void setEnd(final LocalDate end) {
    this.end = end;
  }

  public List<GroupStepDto> getGroupStep() {
    return this.groupStep;
  }

  public void setGroupStep(final List<GroupStepDto> groupStep) {
    this.groupStep = groupStep;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  private BigDecimal calculatePlannedWork(final Function<? super StepDto, ? extends BigDecimal> dataToSum) {
    BigDecimal planed = BigDecimal.ZERO;
    if(this.groupStep != null && !this.groupStep.isEmpty()) {
      for(final GroupStepDto group : this.groupStep) {
        if(group.getSteps() != null && !(group.getSteps()).isEmpty()) {
          for(final StepDto step : group.getSteps()) {
            final BigDecimal data = dataToSum.apply(step);
            if(data != null) {
              planed = planed.add(data);
            }
          }
        }
      }
    }
    return planed;
  }

  private BigDecimal calculatePlannedCost(final Function<? super ConsumesDto, ? extends BigDecimal> dataToSum) {
    BigDecimal planedCost = BigDecimal.ZERO;
    if(this.groupStep != null && !this.groupStep.isEmpty()) {
      for(final GroupStepDto group : this.groupStep) {
        if(group.getSteps() != null && !(group.getSteps()).isEmpty()) {
          for(final StepDto step : group.getSteps()) {
            if(step.getConsumes() != null && !(step.getConsumes()).isEmpty()) {
              for(final ConsumesDto consume : step.getConsumes()) {
                final BigDecimal data = dataToSum.apply(consume);
                if(data != null) {
                  planedCost = planedCost.add(data);
                }
              }
            }
          }
        }
      }
    }
    return planedCost;
  }

  public BigDecimal getPlaned() {
    return this.calculatePlannedWork(StepDto::getPlannedWork);
  }

  public BigDecimal getActual() {
    BigDecimal actual = BigDecimal.ZERO;
    if(this.groupStep != null && !this.groupStep.isEmpty()) {
      for(final GroupStepDto group : this.groupStep) {
        if(group.getSteps() != null && !(group.getSteps()).isEmpty()) {
          for(final StepDto step : group.getSteps()) {
            if(step.getActualWork() != null) {
              actual = actual.add(step.getActualWork());
            }
          }
        }
      }
    }
    return actual;
  }

  public BigDecimal getPlanedCost() {
    return this.calculatePlannedCost(ConsumesDto::getPlannedCost);
  }

  public BigDecimal getActualCost() {
    BigDecimal planedCost = BigDecimal.ZERO;
    if(this.groupStep != null && !this.groupStep.isEmpty()) {
      for(final GroupStepDto group : this.groupStep) {
        if(group.getSteps() != null && !(group.getSteps()).isEmpty()) {
          for(final StepDto step : group.getSteps()) {
            if(step.getConsumes() != null && !(step.getConsumes()).isEmpty()) {
              for(final ConsumesDto consume : step.getConsumes()) {
                if(consume.getActualCost() != null) {
                  planedCost = planedCost.add(consume.getActualCost());
                }
              }
            }
          }
        }
      }
    }
    return planedCost;
  }

  public BigDecimal getBaselinePlaned() {
    return this.baselinePlaned;
  }

  public void setBaselinePlaned(final BigDecimal baselinePlaned) {
    this.baselinePlaned = baselinePlaned;
  }

  public BigDecimal getBaselineCost() {
    return this.baselineCost;
  }

  public void setBaselineCost(final BigDecimal baselineCost) {
    this.baselineCost = baselineCost;
  }

  public Long getIdSnapshot() {
    return idSnapshot;
  }

  public void setIdSnapshot(Long idSnapshot) {
    this.idSnapshot = idSnapshot;
  }
}
