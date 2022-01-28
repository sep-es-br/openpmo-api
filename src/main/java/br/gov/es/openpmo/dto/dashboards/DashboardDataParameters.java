package br.gov.es.openpmo.dto.dashboards;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;

public class DashboardDataParameters {

  private final Boolean showHeader;
  private final Long idWorkpack;

  private final Long idBaseline;

  private final YearMonth yearMonth;

  private final UriComponentsBuilder uriComponentsBuilder;

  public DashboardDataParameters(
    final Boolean showHeader,
    final Long idWorkpack,
    final Long idBaseline,
    final YearMonth date,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.showHeader = showHeader;
    this.idWorkpack = idWorkpack;
    this.idBaseline = idBaseline;
    this.yearMonth = date;
    this.uriComponentsBuilder = uriComponentsBuilder;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public Long getIdBaseline() {
    return this.idBaseline;
  }

  public YearMonth getYearMonth() {
    return this.yearMonth;
  }

  public UriComponentsBuilder getUriComponentsBuilder() {
    return this.uriComponentsBuilder;
  }

  public Boolean getShowHeader() {
    return this.showHeader;
  }
}
