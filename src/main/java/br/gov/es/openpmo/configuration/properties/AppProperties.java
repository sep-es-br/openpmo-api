package br.gov.es.openpmo.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final Double searchCutOffScore;

  public AppProperties(final Double searchCutOffScore) {
    this.searchCutOffScore = searchCutOffScore;
  }

  public Double getSearchCutOffScore() {
    return this.searchCutOffScore;
  }

}
