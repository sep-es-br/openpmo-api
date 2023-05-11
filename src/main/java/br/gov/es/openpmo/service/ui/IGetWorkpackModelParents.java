package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.WorkpackModelParentsResponse;

@FunctionalInterface
public interface IGetWorkpackModelParents {

  WorkpackModelParentsResponse execute(Long idWorkpackModel);

}
