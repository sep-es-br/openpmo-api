package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class MilestoneDetailDto extends WorkpackDetailDto {

  private MilestoneStatus milestoneStatus;

  private LocalDate milestoneDate;

  private LocalDate baselineDate;

  private Long delayInDays;

  @JsonFormat(pattern = "dd/MM/yyyy")
  private LocalDate expirationDate;

  private boolean isWithinAWeek;

  public static MilestoneDetailDto of(final Workpack workpack) {
    return (MilestoneDetailDto) WorkpackDetailDto.of(
      workpack,
      MilestoneDetailDto::new
    );
  }

  public MilestoneStatus getMilestoneStatus() {
    return this.milestoneStatus;
  }

  public void setMilestoneStatus(final MilestoneStatus milestoneStatus) {
    this.milestoneStatus = milestoneStatus;
  }

  public LocalDate getExpirationDate() {
    return this.expirationDate;
  }

  public void setExpirationDate(final LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public boolean isWithinAWeek() {
    return this.isWithinAWeek;
  }

  public void setWithinAWeek(final boolean withinAWeek) {
    this.isWithinAWeek = withinAWeek;
  }

  public LocalDate getMilestoneDate() {
    return this.milestoneDate;
  }

  public void setMilestoneDate(final LocalDate milestoneDate) {
    this.milestoneDate = milestoneDate;
  }

  public LocalDate getBaselineDate() {
    return baselineDate;
  }

  public void setBaselineDate(LocalDate baselineDate) {
    this.baselineDate = baselineDate;
  }

  public Long getDelayInDays() {
    return delayInDays;
  }

  public void setDelayInDays(Long delayInDays) {
    this.delayInDays = delayInDays;
  }

}
