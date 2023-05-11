package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class WorkpackModelParentsResponse {

  private final Collection<Long> parents;

  public WorkpackModelParentsResponse(final Collection<Long> parents) {
    this.parents = Collections.unmodifiableCollection(parents);
  }

  public static WorkpackModelParentsResponse of(final Collection<Long> parentsId) {
    return new WorkpackModelParentsResponse(
      Optional.ofNullable(parentsId)
        .orElseGet(ArrayList::new)
    );
  }

  public Collection<Long> getParents() {
    return this.parents;
  }

}
