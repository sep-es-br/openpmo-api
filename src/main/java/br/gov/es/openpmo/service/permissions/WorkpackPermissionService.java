package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WorkpackPermissionService {

  private final WorkpackPermissionRepository repository;

  public WorkpackPermissionService(final WorkpackPermissionRepository repository) {
    this.repository = repository;
  }

  public void deleteAll(final Set<CanAccessWorkpack> permissions) {
    this.repository.deleteAll(permissions);
  }

}
