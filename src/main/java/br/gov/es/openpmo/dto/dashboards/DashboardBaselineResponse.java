package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.model.baselines.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class DashboardBaselineResponse {

  private Long id;

  private String name;

  private Status status;

  @JsonProperty("default")
  private Boolean defaultBaseline;

  public DashboardBaselineResponse() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(final Status status) {
    this.status = status;
  }

  public Boolean getDefaultBaseline() {
    return this.defaultBaseline;
  }

  public void setDefaultBaseline(final Boolean defaultBaseline) {
    this.defaultBaseline = defaultBaseline;
  }

}
