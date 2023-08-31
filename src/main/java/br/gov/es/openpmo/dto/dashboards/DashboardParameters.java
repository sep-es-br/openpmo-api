package br.gov.es.openpmo.dto.dashboards;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;

public class DashboardParameters {

  private final Boolean showHeader;

  private final Long workpackId;

  private final Long workpackModelId;

  private final Long workpackModelLinkedId;

  private final Long planId;

  private final Long baselineId;

  private final YearMonth yearMonth;

  private final Boolean linked;

  private final Long personId;

  private final UriComponentsBuilder uriComponentsBuilder;

  public DashboardParameters(
    final Boolean showHeader,
    final Long workpackId,
    final Long workpackModelId,
    final Long workpackModelLinkedId,
    final Long planId,
    final Long baselineId,
    final YearMonth date,
    final Boolean linked,
    final Long personId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.showHeader = showHeader;
    this.workpackId = workpackId;
    this.workpackModelId = workpackModelId;
    this.workpackModelLinkedId = workpackModelLinkedId;
    this.planId = planId;
    this.baselineId = baselineId;
    this.yearMonth = date;
    this.linked = linked;
    this.personId = personId;
    this.uriComponentsBuilder = uriComponentsBuilder;
  }

  public Long getWorkpackId() {
    return this.workpackId;
  }

  public Long getBaselineId() {
    return this.baselineId;
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

  public Long getPlanId() {
    return this.planId;
  }

  public Long getWorkpackModelId() {
    return this.workpackModelId;
  }

  public Long getWorkpackModelLinkedId() {
    return this.workpackModelLinkedId;
  }

  public Boolean getLinked() {
    return this.linked;
  }

  public Long getPersonId() {
    return this.personId;
  }

}
