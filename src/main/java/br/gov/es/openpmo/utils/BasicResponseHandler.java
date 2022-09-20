package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.dto.BasicResponse;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ResponseBase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class BasicResponseHandler implements ResponseHandler {

  @Override
  public <T> Response<T> success(final T data) {
    final ResponseBase<T> of = ResponseBase.of(data);
    return new BasicResponse<>(of, HttpStatus.OK);
  }

  @Override
  public Response<Void> success() {
    final ResponseBase<Void> success = ResponseBase.success();
    return new BasicResponse<>(success, HttpStatus.OK);
  }

}
