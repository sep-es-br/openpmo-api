package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class PortfolioModelDto extends WorkpackModelDto {

  private static final String PORTFOLIO_MODEL = "PortfolioModel";

  public static PortfolioModelDto of(final WorkpackModel workpackModel) {
    return (PortfolioModelDto) WorkpackModelDto.of(
      workpackModel,
      PortfolioModelDto::new
    );
  }

  @Override
  public String getType() {
    return PORTFOLIO_MODEL;
  }

}
