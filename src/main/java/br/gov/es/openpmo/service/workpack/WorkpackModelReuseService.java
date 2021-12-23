package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackreuse.ReusableWorkpackModelHierarchyDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;

@Service
public class WorkpackModelReuseService {


  private final WorkpackModelRepository repository;

  @Autowired
  public WorkpackModelReuseService(final WorkpackModelRepository repository) {
    this.repository = repository;
  }

  public WorkpackModel reuse(final Long idWorkpackModelParent, final Long idWorkpackModel) {
    WorkpackModelReuseService.ifAnyNullThrowException(idWorkpackModelParent, idWorkpackModel);

    final WorkpackModel children = this.findWorkpackModel(idWorkpackModel);
    final WorkpackModel parent = this.findWorkpackModel(idWorkpackModelParent);

    children.addParent(parent);

    this.repository.save(children);

    return children;
  }

  private static void ifAnyNullThrowException(final Long idWorkpackModelParent, final Long idWorkpackModel) {
    if(idWorkpackModelParent == null || idWorkpackModel == null) throw new NegocioException(ID_WORKPACK_NOT_NULL);
  }

  private WorkpackModel findWorkpackModel(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

  public List<ReusableWorkpackModelHierarchyDto> findWorkpackModelReusable(final Long idWorkpackModel, final Long idPlanModel) {
    final Set<WorkpackModel> models = this.repository.findAllByIdPlanModelWithChildren(idPlanModel);
    return this.createHierarchy(idWorkpackModel, models);
  }

  private List<ReusableWorkpackModelHierarchyDto> createHierarchy(
    final Long idWorkpackModel,
    final Collection<? extends WorkpackModel> models
  ) {
    if(idWorkpackModel == null) throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);

    final List<ReusableWorkpackModelHierarchyDto> hierarchy = models.stream()
      .map(ReusableWorkpackModelHierarchyDto::of)
      .collect(Collectors.toList());

    this.analyzeHierarchy(
      hierarchy,
      idWorkpackModel
    );

    return hierarchy;
  }

  private void analyzeHierarchy(
    final Iterable<ReusableWorkpackModelHierarchyDto> hierarchy,
    final Long idWorkpackModel
  ) {
    for(final ReusableWorkpackModelHierarchyDto item : hierarchy) {
      if(item.getId().equals(idWorkpackModel)) {
        item.doNotReuse();
        item.doNotReuseChildren();
        item.doNotReuseParent();
        this.doNotReuseParentAscending(item.getParent());
      }
      this.analyzeHierarchy(item.getChildren(), idWorkpackModel);
    }
  }

  private void doNotReuseParentAscending(final Iterable<ReusableWorkpackModelHierarchyDto> itens) {
    for(final ReusableWorkpackModelHierarchyDto item : itens) {
      item.doNotReuseParent();
      if(item.getParent() != null) this.doNotReuseParentAscending(item.getParent());
    }
  }
}
