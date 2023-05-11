package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class PortfolioParentsResponse {

  private final Collection<Long> parents;

  public PortfolioParentsResponse(final Collection<Long> parents) {
    this.parents = Collections.unmodifiableCollection(parents);
  }

  public static PortfolioParentsResponse of(final Collection<Long> parentsId) {
    return new PortfolioParentsResponse(
      Optional.ofNullable(parentsId)
        .orElseGet(ArrayList::new)
    );
  }

  public Collection<Long> getParents() {
    return this.parents;
  }

}
