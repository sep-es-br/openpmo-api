package br.gov.es.openpmo.controller.risk;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.risk.RiskCardDto;
import br.gov.es.openpmo.dto.risk.RiskCreateDto;
import br.gov.es.openpmo.dto.risk.RiskDetailDto;
import br.gov.es.openpmo.dto.risk.RiskUpdateDto;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.risk.RiskService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/risks")
public class RiskController {

  private final RiskService service;

  private final TokenService tokenService;

  public RiskController(final RiskService service, final TokenService tokenService) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<RiskCardDto>>> findAll(
    @RequestParam("id-workpack") final Long idWorkpack,
    @RequestParam(required = false) final Long idFilter
  ) {
    final List<RiskCardDto> risks = this.service.findAllAsCardDto(idWorkpack, idFilter);
    final ResponseBase<List<RiskCardDto>> response = ResponseBase.of(risks);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{idRisk}")
  public ResponseEntity<ResponseBase<RiskDetailDto>> findById(
    @PathVariable final Long idRisk
  ) {
    final RiskDetailDto risks = this.service.findByIdAsRiskDetail(idRisk);
    final ResponseBase<RiskDetailDto> response = ResponseBase.of(risks);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(
    @Valid @RequestBody final RiskCreateDto request,
    final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final Risk risk = this.service.create(request, idPerson);
    final ResponseBase<EntityDto> response = ResponseBase.of(new EntityDto(risk.getId()));
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<RiskDetailDto>> update(
    @Valid @RequestBody final RiskUpdateDto request,
    final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final RiskDetailDto risk = this.service.update(request, idPerson);
    final ResponseBase<RiskDetailDto> response = ResponseBase.of(risk);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{riskId}")
  public ResponseEntity<ResponseBase<Void>> delete(@PathVariable final Long riskId) {
    this.service.deleteById(riskId);
    return ResponseEntity.ok().build();
  }

}
