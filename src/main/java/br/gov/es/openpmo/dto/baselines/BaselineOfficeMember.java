package br.gov.es.openpmo.dto.baselines;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineOfficeMember {

  private Long idPerson;
  private Long idOffice;

  public Long getIdPerson() {
    return idPerson;
  }

  public void setIdPerson(final Long idPerson) {
    this.idPerson = idPerson;
  }

  public Long getIdOffice() {
    return idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }
}
