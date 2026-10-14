package br.gov.es.openpmo.dto.dashboards.datasheet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class DatasheetTotalizers {

  private final List<WorkpacksByModelResponse> workpacksByModel;
  private final Long transversalDeliverableCount;

  public DatasheetTotalizers(final List<WorkpacksByModelResponse> workpacksByModel) {
    this(workpacksByModel, null);
  }

  public DatasheetTotalizers(
    final List<WorkpacksByModelResponse> workpacksByModel,
    final Long transversalDeliverableCount
  ) {
    this.workpacksByModel = workpacksByModel;
    this.transversalDeliverableCount = transversalDeliverableCount;
  }

  public List<WorkpacksByModelResponse> getWorkpacksByModel() {
    return this.workpacksByModel;
  }

  @JsonIgnore
  public Long getTransversalDeliverableCount() {
    return this.transversalDeliverableCount;
  }

}
