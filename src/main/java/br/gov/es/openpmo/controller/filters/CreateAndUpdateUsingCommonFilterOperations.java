package br.gov.es.openpmo.controller.filters;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public abstract class CreateAndUpdateUsingCommonFilterOperations extends CommonFilterOperations {


  @PostMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> save(@Valid @RequestBody final CustomFilterDto request) {
    final CustomFilterDto customFilterCreated = this.getCustomFilterService().create(
      request,
      this.getFilter(),
      null
    );

    final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
      .setData(customFilterCreated)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<CustomFilterDto>> update(@Valid @RequestBody final CustomFilterDto request) {
    final CustomFilterDto customFilterUpdated = this.getCustomFilterService().update(
      request,
      this.getFilter(),
      null
    );

    final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
      .setData(customFilterUpdated)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }
}
