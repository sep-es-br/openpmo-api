package br.gov.es.openpmo.controller.risk;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.risk.RiskCardDto;
import br.gov.es.openpmo.dto.risk.RiskCreateDto;
import br.gov.es.openpmo.dto.risk.RiskDetailDto;
import br.gov.es.openpmo.dto.risk.RiskUpdateDto;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
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
  private final ICanAccessService canAccessService;

  public RiskController(
      final RiskService service,
      final TokenService tokenService,
      final ICanAccessService canAccessService) {
    this.service = service;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<RiskCardDto>>> findAll(
      @RequestParam("id-workpack") final Long idWorkpack,
      @RequestParam(required = false) final Long idFilter,
      @RequestParam(required = false) final String term,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<RiskCardDto> risks = this.service.findAllAsCardDto(idWorkpack, idFilter, term, idPerson);
    final ResponseBase<List<RiskCardDto>> response = ResponseBase.of(risks);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{idRisk}")
  public ResponseEntity<ResponseBase<RiskDetailDto>> findById(
      @PathVariable final Long idRisk,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanReadResource(idRisk, authorization);
    final RiskDetailDto risks = this.service.findByIdAsRiskDetail(idRisk);
    final ResponseBase<RiskDetailDto> response = ResponseBase.of(risks);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(
      @Valid @RequestBody final RiskCreateDto request,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final Risk risk = this.service.create(request, idPerson);
    final ResponseBase<EntityDto> response = ResponseBase.of(new EntityDto(risk.getId()));
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<RiskDetailDto>> update(
      @Valid @RequestBody final RiskUpdateDto request,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanEditResource(request.getId(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final RiskDetailDto risk = this.service.update(request, idPerson);
    final ResponseBase<RiskDetailDto> response = ResponseBase.of(risk);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{riskId}")
  public ResponseEntity<ResponseBase<Void>> delete(
      @PathVariable final Long riskId,
      @Authorization final String authorization) {
    this.canAccessService.ensureCanEditResource(riskId, authorization);
    this.service.deleteById(riskId);
    return ResponseEntity.ok().build();
  }

}
