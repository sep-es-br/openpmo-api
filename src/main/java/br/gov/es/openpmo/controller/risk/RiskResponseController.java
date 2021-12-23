package br.gov.es.openpmo.controller.risk;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.risk.response.RiskResponseCreateDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseDetailDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseUpdateDto;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.risk.RiskResponseService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/risk-responses")
public class RiskResponseController {

  private final RiskResponseService service;

  private final TokenService tokenService;

  public RiskResponseController(final RiskResponseService service, final TokenService tokenService) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(
    @RequestBody final RiskResponseCreateDto request,
    final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final RiskResponse riskResponse = this.service.create(request, idPerson);
    final ResponseBase<EntityDto> response = ResponseBase.of(new EntityDto(riskResponse.getId()));
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<RiskResponseDetailDto>> update(
    @RequestBody final RiskResponseUpdateDto request,
    final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final RiskResponseDetailDto dto = this.service.update(request, idPerson);
    final ResponseBase<RiskResponseDetailDto> response = ResponseBase.of(dto);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{idRiskResponse}")
  public ResponseEntity<ResponseBase<Void>> delete(@PathVariable final Long idRiskResponse) {
    this.service.deleteById(idRiskResponse);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{idRiskResponse}")
  public ResponseEntity<ResponseBase<RiskResponseDetailDto>> findById(@PathVariable final Long idRiskResponse) {
    final RiskResponseDetailDto risk = this.service.findRiskByIdAsDetailDto(idRiskResponse);
    return ResponseEntity.ok(ResponseBase.of(risk));
  }

}
