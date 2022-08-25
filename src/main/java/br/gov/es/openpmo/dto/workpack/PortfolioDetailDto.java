package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class PortfolioDetailDto extends WorkpackDetailDto {

  public static PortfolioDetailDto of(final Workpack workpack) {
    return (PortfolioDetailDto) WorkpackDetailDto.of(workpack, PortfolioDetailDto::new);
  }

}
