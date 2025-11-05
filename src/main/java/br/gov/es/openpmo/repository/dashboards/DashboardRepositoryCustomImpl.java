/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gean.carneiro
 */
@Repository
public class DashboardRepositoryCustomImpl implements DashboardRepositoryCustom {

    private final SessionFactory sessionFactory;

    public DashboardRepositoryCustomImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public List<DashboardDataByMonth> getDataByMonth(Long scope, Long baselineId, Integer monthYear) {
        try(InputStream is = new ClassPathResource("cyphers/getDataByMonth.cypher").getInputStream()) {
            String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("scope", scope);
            parameters.put("baselineId", baselineId);
            parameters.put("monthYear", monthYear);
            
            
            Session session = this.sessionFactory.openSession();
            
            Result result = session.query(query, parameters);
            
            List<Map<String,Object>> listResult = new ArrayList<>();
            
            result.forEach(listResult::add);
            
            final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            return listResult.stream().map(record -> {
                DashboardDataByMonth dto = new DashboardDataByMonth();
                dto.setMes(((Long) record.get("mes")).intValue());
                dto.setCustoReprogramadoAcumuladoMes((Double) record.get("custoReprogramadoAcumuladoMes"));
                dto.setCustoPlanejadoAcumuladoMes((Double) record.get("custoPlanejadoAcumuladoMes"));
                dto.setCustoRealizadoAcumuladoMes((Double) record.get("custoRealizadoAcumuladoMes"));
                dto.setPcFisicoRealizadoAcumMesMedio((Double) record.get("pcFisicoRealizadoAcumMesMedio"));
                dto.setValorAgregado((Double) record.get("valorAgregado"));
                dto.setVariacaoPrazo((Double) record.get("variacaoPrazo"));
                dto.setVariacaoCusto((Double) record.get("variacaoCusto"));
                dto.setEstimadoNaConclusao((Double) record.get("estimadoNaConclusao"));
                dto.setEstimadoParaConclusao((Double) record.get("estimadoParaConclusao"));
                dto.setIdc(record.get("idc") == null ? null : (Double) record.get("idc"));
                dto.setIdp(record.get("idp") == null ? null : (Double) record.get("idp"));
                dto.setActualStartDate(LocalDate.parse(String.valueOf(record.get("actualStartDate")) , ISO_DATE_FORMATTER));
                dto.setActualEndDate(LocalDate.parse(String.valueOf(record.get("actualEndDate")) , ISO_DATE_FORMATTER));
                dto.setPlannedStartDate(LocalDate.parse(String.valueOf(record.get("plannedStartDate")) , ISO_DATE_FORMATTER));
                dto.setPlannedEndDate(LocalDate.parse(String.valueOf(record.get("plannedEndDate")) , ISO_DATE_FORMATTER));
                dto.setReprogStartDate(LocalDate.parse(String.valueOf(record.get("reprogStartDate")) , ISO_DATE_FORMATTER));
                dto.setReprogEndDate(LocalDate.parse(String.valueOf(record.get("reprogEndDate")) , ISO_DATE_FORMATTER));
                dto.setScheduleActualValue((Long) record.get("scheduleActualValue"));
                dto.setSchedulePlannedValue((Long) record.get("schedulePlannedValue"));
                dto.setScheduleForeseenValue((Long) record.get("scheduleForeseenValue"));
                return dto;
            }).collect(Collectors.toList()) ;
            
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    
}
