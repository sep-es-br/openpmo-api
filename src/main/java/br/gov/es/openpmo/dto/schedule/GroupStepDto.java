package br.gov.es.openpmo.dto.schedule;

import java.util.ArrayList;
import java.util.List;

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

}
