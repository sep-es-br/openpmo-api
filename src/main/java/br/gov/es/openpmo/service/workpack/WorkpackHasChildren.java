package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.WorkpackHasChildrenResponse;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessDataResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WorkpackHasChildren {

  private final WorkpackRepository repository;
  private final ICanAccessData canAccessData;

  public WorkpackHasChildren(final WorkpackRepository repository, final ICanAccessData canAccessData) {
    this.repository = repository;
    this.canAccessData = canAccessData;
  }

  public WorkpackHasChildrenResponse execute(final Long idWorkpack, final String authorization) {
    Objects.requireNonNull(idWorkpack);
    final boolean hasChildren = this.repository.hasChildren(idWorkpack);
    final ICanAccessDataResponse canAccessData = this.canAccessData.execute(idWorkpack, authorization);
    return WorkpackHasChildrenResponse.of(hasChildren, this.hasOnlyBasicRead(canAccessData));
  }

  private boolean hasOnlyBasicRead(final ICanAccessDataResponse canAccessData) {
    if (canAccessData.getEdit()) {
      return false;
    }
    if (canAccessData.getRead()) {
      return false;
    }
    return canAccessData.getBasicRead();
  }

}
