package br.gov.es.openpmo.controller.filters;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public abstract class CommonFilterWorkpackModelOperations {

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBase<Void>> delete(@PathVariable final Long id) {
        this.getCustomFilterService().delete(id);

        final ResponseBase<Void> response = new ResponseBase<Void>().setSuccess(true);

        return ResponseEntity.ok(response);
    }

    protected abstract CustomFilterService getCustomFilterService();

    @GetMapping
    public ResponseEntity<ResponseBase<List<?>>> getCombo(@PathVariable("idWorkpackModel") final Long idWorkpackModel) {
        final List<?> informations = this.getCustomFilterService().getCombo(idWorkpackModel, this.getFilter());

        final ResponseBase<List<?>> response = new ResponseBase<List<?>>()
                .setData(informations)
                .setSuccess(true);

        return ResponseEntity.ok(response);
    }

    protected abstract CustomFilterEnum getFilter();

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<CustomFilterDto>> getById(@PathVariable final Long id) {
        final CustomFilterDto dto = this.getCustomFilterService().getById(id);

        final ResponseBase<CustomFilterDto> response = new ResponseBase<CustomFilterDto>()
                .setData(dto)
                .setSuccess(true);

        return ResponseEntity.ok(response);
    }

}
