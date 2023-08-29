package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.time.LocalDate;

public class MilestoneDetailParentDto extends WorkpackDetailParentDto {

  private MilestoneStatus milestoneStatus;

  private LocalDate milestoneDate;

  public static MilestoneDetailParentDto of(final Workpack workpack) {
    return (MilestoneDetailParentDto) WorkpackDetailParentDto.of(
      workpack,
      MilestoneDetailParentDto::new
    );
  }

  public MilestoneStatus getMilestoneStatus() {
    return this.milestoneStatus;
  }

  public void setMilestoneStatus(final MilestoneStatus milestoneStatus) {
    this.milestoneStatus = milestoneStatus;
  }

  public LocalDate getMilestoneDate() {
    return this.milestoneDate;
  }

  public void setMilestoneDate(final LocalDate milestoneDate) {
    this.milestoneDate = milestoneDate;
  }

}
