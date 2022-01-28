package br.gov.es.openpmo.dto.workpack;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.ZonedDateTime;

@QueryResult
public class MilestoneDateQueryResult {

  private ZonedDateTime expirationDate;

  private boolean isWithinAWeek;

  public ZonedDateTime getExpirationDate() {
    return this.expirationDate;
  }

  public void setExpirationDate(final ZonedDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  public boolean isWithinAWeek() {
    return this.isWithinAWeek;
  }

  public void setWithinAWeek(final boolean withinAWeek) {
    this.isWithinAWeek = withinAWeek;
  }

}
