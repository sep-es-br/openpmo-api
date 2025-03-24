package br.gov.es.openpmo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import br.gov.es.openpmo.utils.DashboardCacheUtil;

@Configuration
public class AppCacheConfiguration {

    @Bean
    public ApplicationCacheUtil getApplicationCacheUtil() {
        return new ApplicationCacheUtil();
    }

    @Bean
    public DashboardCacheUtil getDashboardCacheUtil() {
        return new DashboardCacheUtil();
    }
}
