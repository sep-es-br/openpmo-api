package br.gov.es.openpmo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.gov.es.openpmo.utils.ApplicationCacheUtil;

@Configuration
public class AppCacheConfiguration {

    @Bean
    public ApplicationCacheUtil getApplicationCacheUtil() {
        return new ApplicationCacheUtil();
    }
}
