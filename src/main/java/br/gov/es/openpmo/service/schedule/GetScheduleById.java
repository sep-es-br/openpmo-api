package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.repository.ScheduleRepository;
import org.springframework.stereotype.Component;

import static br.gov.es.openpmo.utils.ApplicationMessage.SCHEDULE_NOT_FOUND;

@Component
public class GetScheduleById {

  private final ScheduleRepository repository;

  public GetScheduleById(final ScheduleRepository repository) {this.repository = repository;}

  public Schedule execute(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(SCHEDULE_NOT_FOUND));
  }

}
