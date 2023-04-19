package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupStepDto {

  private Integer year;

  private List<StepDto> steps = new ArrayList<>(0);

  public Integer getYear() {
    return this.year;
  }

  public void setYear(final Integer year) {
    this.year = year;
  }

  public List<StepDto> getSteps() {
    return this.steps;
  }

  public void setSteps(final List<StepDto> steps) {
    this.steps = steps;
  }

  public BigDecimal getActualCost() {
    BigDecimal actualCost = BigDecimal.ZERO;
    if (this.steps == null || (this.steps).isEmpty()) {return BigDecimal.ZERO;}
    for (final StepDto step : this.steps) {
      if (step.getConsumes() != null && !(step.getConsumes()).isEmpty()) {
        for (final ConsumesDto consume : step.getConsumes()) {
          if (consume.getActualCost() != null) {
            actualCost = actualCost.add(consume.getActualCost());
          }
        }
      }
    }
    return actualCost;
  }

  public BigDecimal getActual() {
    BigDecimal actual = BigDecimal.ZERO;
    if (this.steps == null || (this.steps).isEmpty()) return actual;
    for (final StepDto step : this.steps) {
      if (step.getActualWork() == null) continue;
      actual = actual.add(step.getActualWork());
    }
    return actual;
  }

  public BigDecimal getPlaned() {
    return this.calculateUsingStep(StepDto::getPlannedWork);
  }

  public BigDecimal getPlanedCost() {
    return this.calculateUsingConsumes(ConsumesDto::getPlannedCost);
  }

  private BigDecimal calculateUsingConsumes(final Function<? super ConsumesDto, ? extends BigDecimal> dataToSum) {
    if (this.steps == null || this.steps.isEmpty()) {
      return BigDecimal.ZERO;
    }
    BigDecimal planedCost = BigDecimal.ZERO;
    for (final StepDto step : this.steps) {
      if (step.getConsumes() == null || (step.getConsumes()).isEmpty()) continue;
      for (final ConsumesDto consume : step.getConsumes()) {
        final BigDecimal data = dataToSum.apply(consume);
        if (data == null) continue;
        planedCost = planedCost.add(data);
      }
    }
    return planedCost;
  }

  private BigDecimal calculateUsingStep(final Function<? super StepDto, ? extends BigDecimal> dataToSum) {
    BigDecimal planed = BigDecimal.ZERO;
    if (this.steps != null && !(this.steps).isEmpty()) {
      for (final StepDto step : this.steps) {
        final BigDecimal data = dataToSum.apply(step);
        if (data != null) {
          planed = planed.add(data);
        }
      }
    }
    return planed;
  }

}
