package br.gov.es.openpmo.controller.filters;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public abstract class CommonFilterWorkpackModelOperations {

  @DeleteMapping("/{idCustomFilter}")
  public ResponseEntity<ResponseBase<Void>> delete(
    @PathVariable final Long idCustomFilter,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);
    this.getCustomFilterService().delete(idCustomFilter, idPerson);
    return ResponseEntity.ok(ResponseBase.of());
  }

  protected abstract CustomFilterService getCustomFilterService();

  protected abstract TokenService getTokenService();

  @GetMapping
  public ResponseEntity<ResponseBaseItens<CustomFilterDto>> getCombo(
    @PathVariable("idWorkpackModel") final Long idWorkpackModel,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);
    final List<CustomFilterDto> customFilterItens = this.getCustomFilterService().getCombo(
      idWorkpackModel,
      this.getFilter(),
      idPerson
    );
    return ResponseEntity.ok(ResponseBaseItens.of(customFilterItens));
  }

  protected abstract CustomFilterEnum getFilter();

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<CustomFilterDto>> getById(
    @PathVariable final Long id,
    @RequestHeader("Authorization") final String authorization
  ) {
    final Long idPerson = this.getTokenService().getUserId(authorization);
    final CustomFilterDto dto = this.getCustomFilterService().getById(id, idPerson);
    return ResponseEntity.ok(ResponseBase.of(dto));
  }

}
