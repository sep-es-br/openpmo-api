package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.dto.Response;

public interface ResponseHandler {

  <T> Response<T> success(T data);

  Response<Void> success();

}
