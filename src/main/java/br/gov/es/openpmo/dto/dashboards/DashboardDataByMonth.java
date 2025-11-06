/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepDto;
import java.util.List;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 *
 * @author gean.carneiro
 */
@QueryResult
public class DashboardDataByMonth {
    
    private DashboardMonthDto dashboardMonthDto;
    private List<EarnedValueByStepDto> earnedValueByStepDto;

    public DashboardMonthDto getDashboardMonthDto() {
        return dashboardMonthDto;
    }

    public void setDashboardMonthDto(DashboardMonthDto dashboardMonthDto) {
        this.dashboardMonthDto = dashboardMonthDto;
    }

    public List<EarnedValueByStepDto> getEarnedValueByStepDto() {
        return earnedValueByStepDto;
    }

    public void setEarnedValueByStepDto(List<EarnedValueByStepDto> earnedValueByStepDto) {
        this.earnedValueByStepDto = earnedValueByStepDto;
    }
    
    
    
    
}
