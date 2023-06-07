package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.reports.ReportDesign;

public class ActiveReportItem {

  private final Long id;

  private final String name;

  private final String fullName;

  private ActiveReportItem(final Long id, final String name, final String fullName) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
  }

  public static ActiveReportItem of(final ReportDesign reportDesign) {
    return new ActiveReportItem(
      reportDesign.getId(),
      reportDesign.getName(),
      reportDesign.getFullName()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

}
