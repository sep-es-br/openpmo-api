/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gean.carneiro
 */
@Repository
public class DashboardRepositoryCustomImpl implements DashboardRepositoryCustom {

    private final Driver driver;
    

    public DashboardRepositoryCustomImpl(Driver driver) {
        this.driver = driver;
    }

    @Override
    public DashboardDataByMonth getDataByMonth(Long scope, Long baselineId, Integer monthYear, boolean sCurve, boolean transversal) {
        
        Long startOfTask = System.currentTimeMillis();
        
        try (InputStream is = new ClassPathResource("cyphers/getDataByMonth.cypher").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String query = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("scope", scope);
            parameters.put("baselineId", baselineId);
            parameters.put("monthYear", monthYear);
            parameters.put("sCurve", sCurve);
            parameters.put("transversal", transversal);
            
            try (org.neo4j.driver.Session session = driver.session()) {
                Record record = session.readTransaction(tx -> {
                    org.neo4j.driver.Result result = tx.run(query, parameters);
                    return result.hasNext() ? result.single() : null;
                });

                if (record == null && transversal) {
                    DashboardDataByMonth empty = new DashboardDataByMonth();
                    DashboardMonthDto month = new DashboardMonthDto();
                    month.setTripleConstraint(null);
                    month.setPerformanceIndex(null);
                    empty.setDashboardMonthDto(month);
                    empty.setEarnedValueByStepDto(Collections.emptyList());
                    return empty;
                }
                if (record == null) {
                    throw new IllegalStateException("No dashboard data for scope " + scope);
                }
                
                Object node = record.values().get(0).asObject();

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
                DashboardDataByMonth dto = mapper.convertValue(node, DashboardDataByMonth.class);
                
                
                return dto;
                
            }
            
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    
}
