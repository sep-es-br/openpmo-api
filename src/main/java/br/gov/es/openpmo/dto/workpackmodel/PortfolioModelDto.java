package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

public class PortfolioModelDto extends WorkpackModelDto {

  public static final String PORTFOLIO_MODEL = "PortfolioModel";

  public static PortfolioModelDto of(final WorkpackModel workpackModel) {
    final PortfolioModelDto portfolioModelDto = new PortfolioModelDto();
    portfolioModelDto.setId(workpackModel.getId());
    portfolioModelDto.setModelNameInPlural(workpackModel.getModelNameInPlural());
    portfolioModelDto.setModelName(workpackModel.getModelName());
    portfolioModelDto.setType(PORTFOLIO_MODEL);
    portfolioModelDto.setFontIcon(workpackModel.getFontIcon());
    portfolioModelDto.setSortBy(PropertyModelDto.of(workpackModel.getSortBy()));
    return portfolioModelDto;
  }
}
