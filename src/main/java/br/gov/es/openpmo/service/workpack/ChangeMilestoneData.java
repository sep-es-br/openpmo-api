package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.ChangeMilestoneDateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.MilestoneRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ChangeMilestoneData {

  private final WorkpackRepository workpackRepository;

  private final MilestoneRepository milestoneRepository;

  private final PropertyRepository propertyRepository;

  public ChangeMilestoneData(
    WorkpackRepository workpackRepository,
    MilestoneRepository milestoneRepository,
    PropertyRepository propertyRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.milestoneRepository = milestoneRepository;
    this.propertyRepository = propertyRepository;
  }

  public Milestone execute(
    Long idMilestone,
    ChangeMilestoneDateRequest request
  ) {
    final LocalDate newDate = getDate(request);
    final Milestone milestone = getMilestone(idMilestone);
    final boolean concluded = milestoneRepository.isConcluded(idMilestone);
    if (!concluded) {
      milestone.setReasonRequired(true);
    }
    final boolean isOnActualBaseline = milestoneRepository.isOnActualBaseline(idMilestone);
    if (isOnActualBaseline) {
      milestone.setReasonRequired(true);
    }
    final Date milestoneDate = getMilestoneDate(idMilestone);
    final LocalDate previousDate = milestoneDate.getValue().toLocalDate();
    if (!previousDate.isEqual(newDate)) {
      final Date baselineDate = getBaselineDate(idMilestone);
      if (baselineDate != null && !previousDate.isEqual(newDate)) {
        milestone.setReasonRequired(true);
      }
    }
    milestone.setNewDate(newDate);
    milestone.setPreviousDate(previousDate);
    updateMilestoneDate(
      milestoneDate,
      newDate
    );
    return milestone;
  }

  private void updateMilestoneDate(
    Date milestoneDate,
    LocalDate date
  ) {
    milestoneDate.setValue(date.atStartOfDay());
    propertyRepository.save(milestoneDate);
  }

  private Date getMilestoneDate(Long idMilestone) {
    return milestoneRepository.fetchMilestoneDate(idMilestone)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.DATE_NOT_FOUND));
  }

  private Date getBaselineDate(Long idMilestone) {
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
