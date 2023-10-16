package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.model.workpacks.Milestone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MilestoneDto {

  private Boolean completed;
  private LocalDate milestoneDate;
  private LocalDate snapshotDate;

  public static MilestoneDto of(Milestone milestone) {
    final MilestoneDto milestoneDto = new MilestoneDto();
    milestoneDto.setCompleted(milestoneDto.isCompleted());
    milestoneDto.setMilestoneDate(milestone.getMilestoneDate());
    milestoneDto.setSnapshotDate(milestone.getSnapshotDateActiveOrProposedBaseline());
    return milestoneDto;
  }

  public static List<MilestoneDto> of(List<Milestone> milestones) {
    if (milestones.isEmpty()) {
      return Collections.emptyList();
    }
    List<MilestoneDto> milestoneDtos = new ArrayList<>();
    for (Milestone milestone : milestones) {
      MilestoneDto milestoneDto = of(milestone);
      milestoneDtos.add(milestoneDto);
    }
    return milestoneDtos;
  }

  public LocalDate getMilestoneDate() {
    return milestoneDate;
  }

  public void setMilestoneDate(LocalDate milestoneDate) {
    this.milestoneDate = milestoneDate;
  }

  public LocalDate getSnapshotDate() {
    return snapshotDate;
  }

  public void setSnapshotDate(LocalDate snapshotDate) {
    this.snapshotDate = snapshotDate;
  }

  public Boolean isCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }
}
