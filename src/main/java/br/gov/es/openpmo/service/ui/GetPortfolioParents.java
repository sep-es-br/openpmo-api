package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.PortfolioParentsResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetPortfolioParents implements IGetPortfolioParents {

  private final WorkpackRepository repository;

  public GetPortfolioParents(final WorkpackRepository repository) {this.repository = repository;}

  @Override
  public PortfolioParentsResponse execute(final Long idPlan, final Long idWorkpack) {
    final List<Long> parentsId = this.repository.findWorkpackParentsHierarchy(idPlan, idWorkpack).parallelStream()
      .map(Workpack::getId)
      .collect(Collectors.toList());
    return PortfolioParentsResponse.of(parentsId);
  }

}
