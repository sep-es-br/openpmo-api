package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.dto.person.RoleResource;

import java.util.List;

public interface IRemoteRolesFetcher {
  List<RoleResource> fetch(Long idPerson);
}
