package br.gov.es.openpmo.controller.filters;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterCreateRequest;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

public abstract class CreateAndUpdateUsingWorkpackModelFilterOperations extends CommonFilterWorkpackModelOperations {

  @PostMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> save(
    @PathVariable("idWorkpackModel") final Long idWorkpackModel,
    @Valid @RequestBody final CustomFilterDto request,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);
    final CustomFilterDto customFilterCreated = this.getCustomFilterService()
      .create(new CustomFilterCreateRequest(request, this.getFilter(), idWorkpackModel, idPerson));
    return ResponseEntity.ok(ResponseBase.of(customFilterCreated));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> update(
    @PathVariable("idWorkpackModel") final Long idWorkpackModel,
    @Valid @RequestBody final CustomFilterDto request,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);

    final CustomFilterDto customFilterUpdated = this.getCustomFilterService()
      .update(request, this.getFilter(), idWorkpackModel, idPerson);

    return ResponseEntity.ok(ResponseBase.of(customFilterUpdated));
  }

}
