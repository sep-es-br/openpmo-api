package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

@Component
public class HasScheduleSessionActive {

  private final WorkpackRepository workpackRepository;

  public HasScheduleSessionActive(final WorkpackRepository workpackRepository) {this.workpackRepository = workpackRepository;}

  public boolean execute(final Long workpackId) {
    if(Boolean.TRUE.equals(this.workpackRepository.hasScheduleSessionActive(workpackId))) {
      return true;
    }
    return Boolean.TRUE.equals(this.workpackRepository.hasAnyChildrenWithScheduleSessionActive(workpackId));
  }

}
