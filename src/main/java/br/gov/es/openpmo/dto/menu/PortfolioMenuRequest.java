package br.gov.es.openpmo.dto.menu;

public class PortfolioMenuRequest {
  private final Long idOffice;
  private final Long idPlan;
  private final Long idUser;

  public PortfolioMenuRequest(final Long idOffice, final Long idUser) {
    this.idOffice = idOffice;
    this.idPlan = null;
    this.idUser = idUser;
  }

  public PortfolioMenuRequest(final Long idOffice, final Long idPlan, final Long idUser) {
    this.idOffice = idOffice;
    this.idPlan = idPlan;
    this.idUser = idUser;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public Long getIdPlan() {
    return idPlan;
  }

  public Long getIdUser() {
    return this.idUser;
  }
}
