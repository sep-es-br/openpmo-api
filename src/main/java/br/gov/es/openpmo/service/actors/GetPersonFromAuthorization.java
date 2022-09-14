package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.service.authentication.TokenService;
import org.springframework.stereotype.Component;

@Component
public class GetPersonFromAuthorization implements IGetPersonFromAuthorization {

  private final TokenService tokenService;
  private final PersonService personService;

  public GetPersonFromAuthorization(
    final TokenService tokenService,
    final PersonService personService
  ) {
    this.tokenService = tokenService;
    this.personService = personService;
  }

  @Override
  public PersonDataResponse execute(final String authorization) {
    final Long userId = this.tokenService.getUserId(authorization);

    return this.personService
      .findByIdPersonWithRelationshipAuthServiceAcessoCidadao(userId);
  }

}
