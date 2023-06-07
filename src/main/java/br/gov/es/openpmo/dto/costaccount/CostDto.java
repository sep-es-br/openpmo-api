package br.gov.es.openpmo.dto.costaccount;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

public class CostDto {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Long idWorkpack;

  private BigDecimal planed;

  private BigDecimal actual;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BigDecimal limit;

  public CostDto(
    final Long idWorkpack,
    final BigDecimal planed,
    final BigDecimal actual
  ) {
    this.idWorkpack = idWorkpack;
    this.planed = planed;
    this.actual = actual;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public BigDecimal getPlaned() {
    return this.planed;
  }

  public void setPlaned(final BigDecimal planed) {
    this.planed = planed;
  }

  public BigDecimal getActual() {
    return this.actual;
  }

  public void setActual(final BigDecimal actual) {
    this.actual = actual;
  }

  public BigDecimal getLimit() {
    return limit;
  }

  public void setLimit(BigDecimal limit) {
    this.limit = limit;
  }

}
