package br.gov.es.openpmo.controller.filters;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public abstract class CreateAndUpdateUsingWorkpackModelFilterOperations extends CommonFilterWorkpackModelOperations {

    @PostMapping
    public ResponseEntity<ResponseBase<CustomFilterDto>> save(
            @PathVariable("idWorkpackModel") final Long idWorkpackModel,
            @Valid @RequestBody final CustomFilterDto request
    ) {
        final CustomFilterDto customFilterCreated = this.getCustomFilterService()
                .create(request, getFilter(), idWorkpackModel);

        final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
                .setData(customFilterCreated)
                .setSuccess(true);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<CustomFilterDto>> update(
            @PathVariable("idWorkpackModel") final Long idWorkpackModel,
            @Valid @RequestBody final CustomFilterDto request
    ) {
        final CustomFilterDto customFilterUpdated = this.getCustomFilterService()
                .update(request, getFilter(), idWorkpackModel);

        final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
                .setData(customFilterUpdated)
                .setSuccess(true);

        return ResponseEntity.ok(response);
    }

}
