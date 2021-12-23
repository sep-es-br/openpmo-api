package br.gov.es.openpmo.controller.actor;


import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/auth-server-citizen")
public class AuthenticationServerController {


  private final IsAuthenticatedByService service;

  public AuthenticationServerController(final IsAuthenticatedByService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<Boolean>> isAuthenticationServerCitizen() {

    final boolean isAuthenticationServerCitizen = this.service.isCitizenServerAuthentication();

    final ResponseBase<Boolean> response = new ResponseBase<Boolean>()
      .setData(isAuthenticationServerCitizen)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }


}
