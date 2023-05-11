package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.PortfolioParentsResponse;

@FunctionalInterface
public interface IGetPortfolioParents {

  PortfolioParentsResponse execute(Long idPlan, Long idWorkpack);

}
