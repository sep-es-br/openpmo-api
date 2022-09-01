package br.gov.es.openpmo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.DefaultCorrelationId;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.RequestFilter;
import org.zalando.logbook.ResponseFilter;
import org.zalando.logbook.json.PrettyPrintingJsonBodyFilter;

@Configuration
public class LogbookConfiguration {

  @Bean
  public Logbook logbook() {
    return Logbook.builder()
      .correlationId(new DefaultCorrelationId())
      .bodyFilter(new PrettyPrintingJsonBodyFilter())
      .requestFilter(RequestFilter.none())
      .responseFilter(ResponseFilter.none())
      .build();
  }

}
