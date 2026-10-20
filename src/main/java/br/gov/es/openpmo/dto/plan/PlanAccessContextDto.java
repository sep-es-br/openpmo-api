package br.gov.es.openpmo.dto.plan;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PlanAccessContextDto {

  private Long idPlan;
  private Long idOffice;

  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(final Long idPlan) { this.idPlan = idPlan; }
  public Long getIdOffice() { return idOffice; }
  public void setIdOffice(final Long idOffice) { this.idOffice = idOffice; }
}
