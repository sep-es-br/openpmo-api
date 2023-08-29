package br.gov.es.openpmo.dto.workpack;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.ZonedDateTime;

@QueryResult
public class MilestoneDateQueryResult {

  private ZonedDateTime expirationDate;

  public ZonedDateTime getExpirationDate() {
    return this.expirationDate;
  }

  public void setExpirationDate(final ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

}
