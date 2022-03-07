package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.CitizenByNameQuery;
import br.gov.es.openpmo.dto.person.CitizenDto;
import br.gov.es.openpmo.service.actors.CitizenService;
import br.gov.es.openpmo.service.authentication.TokenService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/citizen-users")
public class CitizenController {

  private final CitizenService service;

  private final AcessoCidadaoApi acessoCidadaoApi;

  private final TokenService tokenService;

  @Autowired
  public CitizenController(final CitizenService service, final AcessoCidadaoApi acessoCidadaoApi, final TokenService tokenService) {
    this.service = service;
    this.acessoCidadaoApi = acessoCidadaoApi;
    this.tokenService = tokenService;
  }

  @GetMapping("/name")
  public ResponseEntity<ResponseBase<List<CitizenByNameQuery>>> findCitizenNameAndSub(
      @RequestParam final String name,
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<CitizenByNameQuery> personDto = this.service.findPersonByName(name, idPerson);
    final ResponseBase<List<CitizenByNameQuery>> response = new ResponseBase<List<CitizenByNameQuery>>()
        .setData(personDto)
        .setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{sub}")
  public ResponseEntity<ResponseBase<CitizenDto>> findPersonBySub(
      @PathVariable final String sub,
      @RequestParam(required = false) final Long idOffice,
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final CitizenDto citizenDto = this.service.findCitizenBySub(sub, idOffice, idPerson);

    final ResponseBase<CitizenDto> response = new ResponseBase<CitizenDto>()
        .setData(citizenDto)
        .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/cpf")
  public ResponseEntity<CitizenDto> findPersonByCpf(
      @RequestParam final String cpf,
      @RequestParam(required = false) final Long idOffice,
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final CitizenDto citizen = this.service.findPersonByCpf(cpf, idOffice, idPerson);
    return ResponseEntity.ok(citizen);
  }

  @GetMapping("/load")
  public ResponseEntity<Void> load(
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.acessoCidadaoApi.load(idPerson);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/unload")
  public ResponseEntity<Void> unload() {
    this.acessoCidadaoApi.unload();
    return ResponseEntity.ok().build();
  }

}
