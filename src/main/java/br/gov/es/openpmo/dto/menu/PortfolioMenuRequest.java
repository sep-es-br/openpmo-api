package br.gov.es.openpmo.dto.menu;

public class PortfolioMenuRequest {
  private final Long idOffice;
  private final Long idUser;

  public PortfolioMenuRequest(final Long idOffice, final Long idUser) {
    this.idOffice = idOffice;
    this.idUser = idUser;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public Long getIdUser() {
    return this.idUser;
  }
}
