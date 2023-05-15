package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WorkpackHasChildren {

  private final WorkpackRepository repository;

  public WorkpackHasChildren(final WorkpackRepository repository) {this.repository = repository;}

  public boolean execute(final Long idWorkpack) {
    Objects.requireNonNull(idWorkpack);
    return this.repository.hasChildren(idWorkpack);
  }

}
