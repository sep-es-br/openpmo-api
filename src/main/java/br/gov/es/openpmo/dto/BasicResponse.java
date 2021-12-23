package br.gov.es.openpmo.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class BasicResponse<T> extends ResponseEntity<ResponseBase<T>> implements Response<T> {

  public BasicResponse(final HttpStatus status) {
    super(status);
  }

  public BasicResponse(final ResponseBase<T> body, final HttpStatus status) {
    super(body, status);
  }

  public BasicResponse(final MultiValueMap<String, String> headers, final HttpStatus status) {
    super(headers, status);
  }

  public BasicResponse(final ResponseBase<T> body, final MultiValueMap<String, String> headers, final HttpStatus status) {
    super(body, headers, status);
  }

}
