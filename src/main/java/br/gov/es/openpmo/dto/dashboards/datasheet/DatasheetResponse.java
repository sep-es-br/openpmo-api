package br.gov.es.openpmo.dto.dashboards.datasheet;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.Set;

public class DatasheetResponse {

  @JsonUnwrapped
  private final DatasheetTotalizers datasheetTotalizers;

  private final Set<DatasheetStakeholderResponse> stakeholders;

  public DatasheetResponse(
    final DatasheetTotalizers datasheetTotalizers,
    final Set<DatasheetStakeholderResponse> stakeholders
  ) {
    this.datasheetTotalizers = datasheetTotalizers;
    this.stakeholders = Collections.unmodifiableSet(stakeholders);
  }

  public DatasheetTotalizers getDatasheetTotalizers() {
    return this.datasheetTotalizers;
  }

  public Set<DatasheetStakeholderResponse> getStakeholders() {
    return this.stakeholders;
  }

}
