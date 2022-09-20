package br.gov.es.openpmo.dto.dashboards.datasheet;

import java.util.List;

public class DatasheetTotalizers {

  private final List<WorkpacksByModelResponse> workpacksByModel;

  public DatasheetTotalizers(final List<WorkpacksByModelResponse> workpacksByModel) {
    this.workpacksByModel = workpacksByModel;
  }

  public List<WorkpacksByModelResponse> getWorkpacksByModel() {
    return this.workpacksByModel;
  }

}
