/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataByMonth;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    public DashboardDataByMonth getDataByMonth(Long scope, Long baselineId, Integer monthYear) {
        try (InputStream is = new ClassPathResource("cyphers/getDataByMonth.cypher").getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String query = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("scope", scope);
            parameters.put("baselineId", baselineId);
            parameters.put("monthYear", monthYear);
            
            
            Session session = this.sessionFactory.openSession();
            
            
            Result result = session.query(query, parameters);
            Map<String, Object> row = result.iterator().next();
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            DashboardDataByMonth dto = mapper.convertValue(row.values().iterator().next(), DashboardDataByMonth.class);
            
            return dto;
            
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    
}
