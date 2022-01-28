package br.gov.es.openpmo.dto.dashboards.datasheet;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.List;

public class DatasheetResponse {

  @JsonUnwrapped
  private final DatasheetTotalizers datasheetTotalizers;

  private final List<DatasheetStakeholderResponse> stakeholders;

  public DatasheetResponse(
      final DatasheetTotalizers datasheetTotalizers,
      final List<DatasheetStakeholderResponse> stakeholders
  ) {
    this.datasheetTotalizers = datasheetTotalizers;
    this.stakeholders = Collections.unmodifiableList(stakeholders);
  }

  public DatasheetTotalizers getDatasheetTotalizers() {
    return this.datasheetTotalizers;
  }

  public List<DatasheetStakeholderResponse> getStakeholders() {
    return this.stakeholders;
  }

}
