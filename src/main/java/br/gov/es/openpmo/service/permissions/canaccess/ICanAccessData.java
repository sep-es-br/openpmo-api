package br.gov.es.openpmo.service.permissions.canaccess;

import java.util.List;


public interface ICanAccessData {

  ICanAccessDataResponse execute(
    Long id,
    String authorizationHeader
  );

  ICanAccessDataResponse execute(
    List<Long> ids,
    String authorizationHeader
  );

  ICanAccessDataResponse execute(String authorizationHeader);


}
