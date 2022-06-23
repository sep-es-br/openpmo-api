package br.gov.es.openpmo.controller.filters;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterCreateRequest;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

public abstract class CreateAndUpdateUsingCommonFilterOperations extends CommonFilterOperations {

  @PostMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> save(
    @Valid @RequestBody final CustomFilterDto request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {

    final Long idUser = this.getTokenService().getUserId(authorization);

    final CustomFilterDto customFilterCreated =
      this.getCustomFilterService().create(new CustomFilterCreateRequest(
        request,
        this.getFilter(),
        null,
        idUser
      ));

    final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
      .setData(customFilterCreated)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> update(
    @Valid @RequestBody final CustomFilterDto request,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);

    final CustomFilterDto customFilterUpdated = this.getCustomFilterService()
      .update(request, this.getFilter(), null, idPerson);

    return ResponseEntity.ok(ResponseBase.of(customFilterUpdated));
  }
}
