package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.WorkpackModelParentsResponse;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class GetWorkpackModelParents implements IGetWorkpackModelParents {

  private final WorkpackModelRepository repository;

  public GetWorkpackModelParents(final WorkpackModelRepository repository) {this.repository = repository;}

  @Override
  public WorkpackModelParentsResponse execute(final Long idWorkpackModel) {
    final Set<Long> parentsId = this.repository.findWorkpackModelParentsHierarchy(idWorkpackModel);
    return WorkpackModelParentsResponse.of(parentsId);
  }

}
