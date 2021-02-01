package br.gov.es.openpmo.dto.costaccount;

import java.math.BigDecimal;

public class CostDto {
    private Long idWorkpack;
    private BigDecimal planed;
    private BigDecimal atual;

    public CostDto(Long idWorkpack, BigDecimal planed, BigDecimal atual) {
        this.idWorkpack = idWorkpack;
        this.planed = planed;
        this.atual = atual;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public BigDecimal getPlaned() {
        return planed;
    }

    public void setPlaned(BigDecimal planed) {
        this.planed = planed;
    }

    public BigDecimal getAtual() {
        return atual;
    }

    public void setAtual(BigDecimal atual) {
        this.atual = atual;
    }
}
