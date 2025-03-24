package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.ChangeMilestoneDateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.MilestoneRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ChangeMilestoneData {

  private final WorkpackRepository workpackRepository;

  private final MilestoneRepository milestoneRepository;


  public ChangeMilestoneData(
    WorkpackRepository workpackRepository,
    MilestoneRepository milestoneRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.milestoneRepository = milestoneRepository;
  }

  public Milestone execute(
    Long idMilestone,
    ChangeMilestoneDateRequest request
  ) {
    final LocalDate newDate = getDate(request);
    final Milestone milestone = getMilestone(idMilestone);
    milestone.setReasonRequired(false);

    final LocalDateTime milestoneDate = milestone.getDate();
    final LocalDate previousDate = milestoneDate.toLocalDate();
    if (!previousDate.isEqual(newDate)) {
      final LocalDateTime baselineDate = getBaselineDate(idMilestone);
      if (baselineDate != null && !newDate.isEqual(baselineDate.toLocalDate())) {
        milestone.setReasonRequired(true);
      }
    }
    milestone.setNewDate(newDate);
    milestone.setPreviousDate(previousDate);
    milestone.setDate(newDate.atStartOfDay());
    workpackRepository.save(milestone);
    return milestone;
  }

  private LocalDateTime getBaselineDate(Long idMilestone) {
    return milestoneRepository.fetchMilestoneBaselineDate(idMilestone)
      .orElse(null);
  }

  private static LocalDate getDate(ChangeMilestoneDateRequest request) {
    return Optional.ofNullable(request.getDate())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.DATE_NOT_PRESENT));
  }

  private Milestone getMilestone(Long idMilestone) {
    final Workpack workpack = getWorkpack(idMilestone);
    if (workpack instanceof Milestone) {
      return (Milestone) workpack;
    }
    throw new NegocioException(ApplicationMessage.WORKPACK_TYPE_MISMATCH);
  }

  private Workpack getWorkpack(Long idMilestone) {
    return workpackRepository.findById(idMilestone)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

}
