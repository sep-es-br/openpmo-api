/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.dashboards;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 *
 * @author gean.carneiro
 */
@QueryResult
public class DashboardStatusData {
    
    private Integer statusConcluida;
    private Integer statusEmExec;
    private Integer statusCancelada;
    private Integer statusCancelar;
    private Integer statusPlanejamento;
    private Integer statusParalisada;
    private Integer totalDeliverable;

    public Integer getTotalDeliverable() {
        return totalDeliverable;
    }

    public void setTotalDeliverable(Integer totalDeliverable) {
        this.totalDeliverable = totalDeliverable;
    }

    public Integer getStatusParalisada() {
        return statusParalisada;
    }

    public void setStatusParalisada(Integer statusParalisada) {
        this.statusParalisada = statusParalisada;
    }
    
    public Integer getStatusConcluida() {
        return statusConcluida;
    }

    public void setStatusConcluida(Integer statusConcluida) {
        this.statusConcluida = statusConcluida;
    }

    public Integer getStatusEmExec() {
        return statusEmExec;
    }

    public void setStatusEmExec(Integer statusEmExec) {
        this.statusEmExec = statusEmExec;
    }

    public Integer getStatusCancelada() {
        return statusCancelada;
    }

    public void setStatusCancelada(Integer statusCancelada) {
        this.statusCancelada = statusCancelada;
    }

    public Integer getStatusCancelar() {
        return statusCancelar;
    }

    public void setStatusCancelar(Integer statusCancelar) {
        this.statusCancelar = statusCancelar;
    }

    public Integer getStatusPlanejamento() {
        return statusPlanejamento;
    }

    public void setStatusPlanejamento(Integer statusPlanejamento) {
        this.statusPlanejamento = statusPlanejamento;
    }
    
    
}
