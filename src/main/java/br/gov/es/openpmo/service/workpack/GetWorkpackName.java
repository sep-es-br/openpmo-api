package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.dto.workpack.WorkpackNameResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Component
public class GetWorkpackName {

  private final WorkpackRepository workpackRepository;

  public GetWorkpackName(final WorkpackRepository workpackRepository) {this.workpackRepository = workpackRepository;}

  public WorkpackNameResponse execute(final Long idWorkpack) {

    final WorkpackName workpackName =
      this.workpackRepository.findWorkpackNameAndFullname(idWorkpack)
        .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));

    return new WorkpackNameResponse(
      workpackName.getName(),
      workpackName.getFullName()
    );
  }


}
