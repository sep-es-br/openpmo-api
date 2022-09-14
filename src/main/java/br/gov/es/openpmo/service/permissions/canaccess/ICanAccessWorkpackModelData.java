package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData.ICanAccessDataResponse;

@FunctionalInterface
public interface ICanAccessWorkpackModelData {

  ICanAccessDataResponse execute(
    Long idWorkpackModel,
    String authorization
  );

}
