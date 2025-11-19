/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;

/**
 *
 * @author gean.carneiro
 */
public interface DashboardRepositoryCustom {
    DashboardDataByMonth getDataByMonth(Long scope, Long baselineId, Integer monthYear, boolean sCurve);
}
