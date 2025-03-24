package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailParentDto;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MilestoneService {

  private final MilestoneRepository milestoneRepository;

  @Autowired
  public MilestoneService(
    final MilestoneRepository milestoneRepository
  ) {
    this.milestoneRepository = milestoneRepository;
  }

  public void addStatus(
    final Workpack workpack,
    final MilestoneDetailDto milestoneDetailDto
  ) {
    final Optional<LocalDateTime> baselineDateMilestone = this.milestoneRepository.fetchMilestoneBaselineDate(workpack.getId());
    final LocalDate snapshotDate = baselineDateMilestone.map(LocalDateTime::toLocalDate).orElse(null);
    final LocalDate milestoneDate = workpack.getDate().toLocalDate();

    final LocalDate refDate = LocalDate.now();

    if (Boolean.TRUE.equals(workpack.getCompleted())) {
      if (!baselineDateMilestone.isPresent()) {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
      } else {
        if (milestoneDate.isEqual(snapshotDate) || milestoneDate.isBefore(snapshotDate)) {
          milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
        } else {
          milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE_CONCLUDED);
        }
      }
    } else {
      if (milestoneDate.isEqual(refDate) || milestoneDate.isAfter(refDate)) {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.ON_TIME);
      } else {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE);
      }
    }
  }

  public void addStatus(
    final Workpack workpack,
    final MilestoneDetailParentDto milestoneDetailDto
  ) {

    final Optional<LocalDateTime> baselineDateMilestone = this.milestoneRepository.fetchMilestoneBaselineDate(workpack.getId());
    final LocalDate snapshotDate = baselineDateMilestone.map(LocalDateTime::toLocalDate).orElse(null);
    final LocalDate milestoneDate = workpack.getDate().toLocalDate();
    final LocalDate refDate = LocalDate.now();

    if (Boolean.TRUE.equals(workpack.getCompleted())) {
      if (snapshotDate == null) {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
      } else {
        if (milestoneDate.isEqual(snapshotDate) || milestoneDate.isBefore(snapshotDate)) {
          milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
        } else {
          milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE_CONCLUDED);
        }
      }
    } else {
      if (milestoneDate.isEqual(refDate) || milestoneDate.isAfter(refDate)) {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.ON_TIME);
      } else {
        milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE);
      }
    }
  }

  public void addDate(
    final Milestone workpack,
    final MilestoneDetailDto milestoneDetailDto
  ) {
    milestoneDetailDto.setMilestoneDate(workpack.getDate().toLocalDate());

    this.milestoneRepository.fetchMilestoneBaselineDate(workpack.getId())
      .map(LocalDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setBaselineDate);

    final LocalDate milestoneDate = milestoneDetailDto.getMilestoneDate();
    final LocalDate baselineDate = milestoneDetailDto.getBaselineDate();

    LocalDateTime milestoneOrToday;
    final boolean concluded = Boolean.TRUE.equals(workpack.getCompleted());
    if (!concluded && LocalDate.now().isAfter(milestoneDate)) {
      milestoneOrToday = LocalDate.now().atStartOfDay();
    } else {
      milestoneOrToday = milestoneDate.atStartOfDay();
    }

    if (milestoneDate != null && baselineDate != null) {
      final Duration between = Duration.between(
        baselineDate.atStartOfDay(),
        milestoneOrToday
      );
      final long days = between.toDays();
      milestoneDetailDto.setDelayInDays(days);
    }
    milestoneDetailDto.setExpirationDate(workpack.getDate().toLocalDate());
  }

}
