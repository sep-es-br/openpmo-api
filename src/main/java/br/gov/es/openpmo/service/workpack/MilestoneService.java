package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.MilestoneDateQueryResult;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MilestoneService {

  private final MilestoneRepository repository;

  @Autowired
  public MilestoneService(final MilestoneRepository repository) {
    this.repository = repository;
  }

  public void addStatus(final Long milestoneId, final MilestoneDetailDto milestoneDetailDto) {
    final boolean late = this.repository.isLate(milestoneId);
    if (late) {
      milestoneDetailDto.setStatus(MilestoneStatus.LATE);
      return;
    }
    final boolean lateConcluded = this.repository.isLateConcluded(milestoneId);
    if (lateConcluded) {
      milestoneDetailDto.setStatus(MilestoneStatus.LATE_CONCLUDED);
      return;
    }
    final boolean onTime = this.repository.isOnTime(milestoneId);
    if (onTime) {
      milestoneDetailDto.setStatus(MilestoneStatus.ON_TIME);
      return;
    }
    final boolean concluded = this.repository.isConcluded(milestoneId);
    if (concluded) {
      milestoneDetailDto.setStatus(MilestoneStatus.CONCLUDED);
    }
  }

  public void addDate(final Long milestoneId, final MilestoneDetailDto milestoneDetailDto) {
    final MilestoneDateQueryResult queryResult = this.repository.getMilestoneDateQueryResult(milestoneId);
    milestoneDetailDto.setExpirationDate(queryResult.getExpirationDate().toLocalDate());
    milestoneDetailDto.setWithinAWeek(queryResult.isWithinAWeek());
  }

}
