package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import br.gov.es.openpmo.enumerator.MilestoneStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class MilestoneRepresentation {

  private MilestoneStatus milestoneStatus;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate milestoneDate;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate expirationDate;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate baselineDate;

  public MilestoneStatus getMilestoneStatus() {
    return milestoneStatus;
  }

  public void setMilestoneStatus(MilestoneStatus milestoneStatus) {
    this.milestoneStatus = milestoneStatus;
  }

  public LocalDate getMilestoneDate() {
    return milestoneDate;
  }

  public void setMilestoneDate(LocalDate milestoneDate) {
    this.milestoneDate = milestoneDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public LocalDate getBaselineDate() {
    return baselineDate;
  }

  public void setBaselineDate(LocalDate baselineDate) {
    this.baselineDate = baselineDate;
  }

}
