package br.gov.es.openpmo.service.actors;


import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.repository.AuthServiceRepository;
import br.gov.es.openpmo.repository.IsAuthenticatedByRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IsAuthenticatedByService {

  private final IsAuthenticatedByRepository repository;
  private final AuthServiceRepository authServiceRepository;

  @Value("${app.login.server.name}")
  private String authenticationServiceName;

  @Autowired
  public IsAuthenticatedByService(final IsAuthenticatedByRepository repository, final AuthServiceRepository authServiceRepository) {
    this.repository = repository;
    this.authServiceRepository = authServiceRepository;
  }

  public IsAuthenticatedBy save(final IsAuthenticatedBy isAuthenticatedBy) {
    return this.repository.save(isAuthenticatedBy);
  }

  public Optional<IsAuthenticatedBy> findAuthenticatedBy(final Long idPerson) {
    return this.repository.findAuthenticatedByUsingPersonAndDefaultServerName(
      idPerson,
      this.authenticationServiceName
    );
  }

  public String defaultAuthServerName() {
    return this.authenticationServiceName;
  }

  public boolean isCitizenServerAuthentication() {
    return this.findDefaultAuthenticationServer()
      .getServer()
      .equalsIgnoreCase("AcessoCidadao");
  }

  public AuthService findDefaultAuthenticationServer() {
    return this.authServiceRepository
      .findAuthServiceByServer(this.authenticationServiceName)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.AUTH_SERVICE_NOT_FOUND));
  }
}
