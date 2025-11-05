/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.dashboards;

import java.time.LocalDate;

/**
 *
 * @author gean.carneiro
 */
public class DashboardDataByMonth {
    
    private Integer mes;
    private Double custoReprogramadoAcumuladoMes;
    private Double custoPlanejadoAcumuladoMes;
    private Double custoRealizadoAcumuladoMes;
    private Double pcFisicoRealizadoAcumMesMedio;
    private Double valorAgregado;
    private Double variacaoPrazo;
    private Double variacaoCusto;
    private Double estimadoNaConclusao;
    private Double estimadoParaConclusao;
    private Double idc;
    private Double idp;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate reprogStartDate;
    private LocalDate reprogEndDate;
    private Long scheduleActualValue;
    private Long schedulePlannedValue;
    private Long scheduleForeseenValue;
    
    
    
    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Double getCustoReprogramadoAcumuladoMes() {
        return custoReprogramadoAcumuladoMes;
    }

    public void setCustoReprogramadoAcumuladoMes(Double custoReprogramadoAcumuladoMes) {
        this.custoReprogramadoAcumuladoMes = custoReprogramadoAcumuladoMes;
    }

    public Double getCustoPlanejadoAcumuladoMes() {
        return custoPlanejadoAcumuladoMes;
    }

    public void setCustoPlanejadoAcumuladoMes(Double custoPlanejadoAcumuladoMes) {
        this.custoPlanejadoAcumuladoMes = custoPlanejadoAcumuladoMes;
    }

    public Double getCustoRealizadoAcumuladoMes() {
        return custoRealizadoAcumuladoMes;
    }

    public void setCustoRealizadoAcumuladoMes(Double custoRealizadoAcumuladoMes) {
        this.custoRealizadoAcumuladoMes = custoRealizadoAcumuladoMes;
    }

    public Double getPcFisicoRealizadoAcumMesMedio() {
        return pcFisicoRealizadoAcumMesMedio;
    }

    public void setPcFisicoRealizadoAcumMesMedio(Double pcFisicoRealizadoAcumMesMedio) {
        this.pcFisicoRealizadoAcumMesMedio = pcFisicoRealizadoAcumMesMedio;
    }

    public Double getValorAgregado() {
        return valorAgregado;
    }

    public void setValorAgregado(Double valorAgregado) {
        this.valorAgregado = valorAgregado;
    }

    public Double getVariacaoPrazo() {
        return variacaoPrazo;
    }

    public void setVariacaoPrazo(Double variacaoPrazo) {
        this.variacaoPrazo = variacaoPrazo;
    }

    public Double getVariacaoCusto() {
        return variacaoCusto;
    }

    public void setVariacaoCusto(Double variacaoCusto) {
        this.variacaoCusto = variacaoCusto;
    }

    public Double getEstimadoNaConclusao() {
        return estimadoNaConclusao;
    }

    public void setEstimadoNaConclusao(Double estimadoNaConclusao) {
        this.estimadoNaConclusao = estimadoNaConclusao;
    }

    public Double getEstimadoParaConclusao() {
        return estimadoParaConclusao;
    }

    public void setEstimadoParaConclusao(Double estimadoParaConclusao) {
        this.estimadoParaConclusao = estimadoParaConclusao;
    }

    public Double getIdc() {
        return idc;
    }

    public void setIdc(Double idc) {
        this.idc = idc;
    }

    public Double getIdp() {
        return idp;
    }

    public void setIdp(Double idp) {
        this.idp = idp;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public LocalDate getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(LocalDate plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public LocalDate getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(LocalDate plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public LocalDate getReprogStartDate() {
        return reprogStartDate;
    }

    public void setReprogStartDate(LocalDate reprogStartDate) {
        this.reprogStartDate = reprogStartDate;
    }

    public LocalDate getReprogEndDate() {
        return reprogEndDate;
    }

    public void setReprogEndDate(LocalDate reprogEndDate) {
        this.reprogEndDate = reprogEndDate;
    }

    public Long getScheduleActualValue() {
        return scheduleActualValue;
    }

    public void setScheduleActualValue(Long scheduleActualValue) {
        this.scheduleActualValue = scheduleActualValue;
    }

    public Long getSchedulePlannedValue() {
        return schedulePlannedValue;
    }

    public void setSchedulePlannedValue(Long schedulePlannedValue) {
        this.schedulePlannedValue = schedulePlannedValue;
    }

    public Long getScheduleForeseenValue() {
        return scheduleForeseenValue;
    }

    public void setScheduleForeseenValue(Long scheduleForeseenValue) {
        this.scheduleForeseenValue = scheduleForeseenValue;
    }
    
    
    
    
}
