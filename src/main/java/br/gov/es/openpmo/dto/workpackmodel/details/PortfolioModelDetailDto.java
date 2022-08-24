package br.gov.es.openpmo.dto.workpackmodel.details;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class PortfolioModelDetailDto extends WorkpackModelDetailDto {

  public static PortfolioModelDetailDto of(final WorkpackModel workpackModel) {
    return (PortfolioModelDetailDto) WorkpackModelDetailDto.of(workpackModel, PortfolioModelDetailDto::new);
  }

  @Override
  public String getType() {
    return "PortfolioModel";
  }

}
