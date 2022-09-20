package br.gov.es.openpmo.dto.costaccount;

import java.math.BigDecimal;

public class CostDto {

  private Long idWorkpack;
  private BigDecimal planed;
  private BigDecimal atual;

  public CostDto(
    final Long idWorkpack,
    final BigDecimal planed,
    final BigDecimal atual
  ) {
    this.idWorkpack = idWorkpack;
    this.planed = planed;
    this.atual = atual;
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

  public BigDecimal getAtual() {
    return this.atual;
  }

  public void setAtual(final BigDecimal atual) {
    this.atual = atual;
  }

}
