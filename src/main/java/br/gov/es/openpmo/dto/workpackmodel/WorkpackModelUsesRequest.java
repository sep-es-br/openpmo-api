package br.gov.es.openpmo.dto.workpackmodel;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class WorkpackModelUsesRequest {

  @NotNull
  private Set<Long> idsWorkpackModel = new LinkedHashSet<>();

  public Set<Long> getIdsWorkpackModel() {
    return this.idsWorkpackModel == null
      ? Collections.emptySet()
      : this.idsWorkpackModel;
  }

  public void setIdsWorkpackModel(final Set<Long> idsWorkpackModel) {
    this.idsWorkpackModel = idsWorkpackModel;
  }
}
