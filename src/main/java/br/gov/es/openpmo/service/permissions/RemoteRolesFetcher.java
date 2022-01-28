package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.repository.IsAuthenticatedByRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.AUTH_SERVICE_NOT_FOUND;

@Component
public class RemoteRolesFetcher implements IRemoteRolesFetcher {

  private final IsAuthenticatedByRepository authenticationRepository;

  private final AcessoCidadaoApi acessoCidadaoApi;

  @Value("${app.login.server.name}")
  private String serverName;

  @Autowired
  public RemoteRolesFetcher(
      final IsAuthenticatedByRepository authenticationRepository,
      final AcessoCidadaoApi acessoCidadaoApi
  ) {
    this.authenticationRepository = authenticationRepository;
    this.acessoCidadaoApi = acessoCidadaoApi;
  }

  public List<RoleResource> fetch(final Long idPerson) {
    final IsAuthenticatedBy authentication = this.findAuthenticatedByUsingPersonAndDefaultServerName(idPerson);

    final String guid = authentication.getGuid();

    if (guid == null) return Collections.emptyList();

    final List<PublicAgentRoleResponse> publicAgentRoles = this.acessoCidadaoApi.findRoles(guid, idPerson);

    return publicAgentRoles.stream()
        .map(agentRole -> this.mapToRoleResource(agentRole, idPerson))
        .collect(Collectors.toList());
  }

  private RoleResource mapToRoleResource(final PublicAgentRoleResponse agentRole, final Long idPerson) {
    final String workLocation = this.acessoCidadaoApi.getWorkLocation(agentRole.getOrganizationGuid(), idPerson);
    return new RoleResource(agentRole.getName(), workLocation);
  }

  private IsAuthenticatedBy findAuthenticatedByUsingPersonAndDefaultServerName(final Long idPerson) {
    return this.authenticationRepository.findAuthenticatedByUsingPersonAndDefaultServerName(
        idPerson,
        this.serverName
    ).orElseThrow(() -> new NegocioException(AUTH_SERVICE_NOT_FOUND));
  }

}
