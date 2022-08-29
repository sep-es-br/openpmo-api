package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.permission.WorkpackPermissionResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetWorkpackPermissions {

  private final WorkpackPermissionVerifier workpackPermissionVerifier;
  private final WorkpackRepository repository;

  public GetWorkpackPermissions(
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final WorkpackRepository repository
  ) {
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.repository = repository;
  }

  public WorkpackPermissionResponse execute(
    final Long idUser,
    final Long idWorkpack,
    final Long idPlan
  ) {

    final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchPermissions(
      idUser,
      idPlan,
      idWorkpack
    );

    final Workpack workpack = this.getWorkpack(idWorkpack);

    return WorkpackPermissionResponse.of(
      idWorkpack,
      permissions,
      workpack.isCanceled(),
      workpack.getEndManagementDate()
    );
  }

  private Workpack getWorkpack(final Long idWorkpack) {
    return this.repository.findById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

}
