package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class PortfolioDetailParentDto extends WorkpackDetailParentDto {

  public static PortfolioDetailParentDto of(final Workpack workpack) {
    return (PortfolioDetailParentDto) WorkpackDetailParentDto.of(workpack, PortfolioDetailParentDto::new);
  }

}
