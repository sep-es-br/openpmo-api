package br.gov.es.openpmo.apis.acessocidadao;

import br.gov.es.openpmo.apis.acessocidadao.response.OperationalOrganizationResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentEmailResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;

import java.util.List;
import java.util.Optional;

public interface AcessoCidadaoApi {

  List<OperationalOrganizationResponse> findAllOperationalOrganizations(Long idPerson);

  List<PublicAgentRoleResponse> findRoles(String guid, Long idPerson);

  List<PublicAgentResponse> findAllPublicAgents(Long idPerson);

  Optional<PublicAgentEmailResponse> findAgentEmail(String sub, Long idPerson);

  Optional<PublicAgentResponse> findPublicAgentBySub(String sub, Long idPerson);

  Optional<String> findSubByCpf(String cpf, Long idPerson);

  String getWorkLocation(String organizationGuid, Long idPerson);

  void load(Long idPerson);

  void unload();

}
