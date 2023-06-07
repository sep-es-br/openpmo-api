package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelRepresentation;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetWorkpackBreakdownStructure {

  private final GetWorkpackRepresentation getWorkpackRepresentation;

  private final WorkpackRepository workpackRepository;

  public GetWorkpackBreakdownStructure(
    final GetWorkpackRepresentation getWorkpackRepresentation,
    final WorkpackRepository workpackRepository
  ) {
    this.getWorkpackRepresentation = getWorkpackRepresentation;
    this.workpackRepository = workpackRepository;
  }

  private static Set<Workpack> getWorkpacks(
    final Workpack parent,
    final WorkpackModel child
  ) {
    final Set<Workpack> childWorkpacks = child.getWorkpacks();
    if (childWorkpacks == null) {
      return new HashSet<>();
    }
    final Set<Workpack> workpacks = new HashSet<>();
    for (final Workpack workpack : childWorkpacks) {
      if (parent.containsChild(workpack)) {
        workpacks.add(workpack);
      }
    }
    return workpacks;
  }

  public WorkpackBreakdownStructure execute(
    final Long idWorkpack,
    final Boolean allLevels
  ) {
    if (idWorkpack == null) {
      throw new IllegalStateException("Id do workpack nulo!");
    }
    final Workpack workpack = this.workpackRepository.findWorkpackWithModelStructureById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    final WorkpackModel model = workpack.getWorkpackModelInstance();
    final WorkpackBreakdownStructure rootStructure = new WorkpackBreakdownStructure();
    if (model == null) {
      return null;
    }
    final List<WorkpackModelBreakdownStructure> children = this.getChildren(
      workpack,
      model,
      allLevels
    );
    if (children.isEmpty()) {
      return null;
    }
    rootStructure.setWorkpackModels(children);
    rootStructure.setRepresentation(this.getWorkpackRepresentation.execute(workpack));
    return rootStructure;
  }

  private List<WorkpackModelBreakdownStructure> getChildren(
    final Workpack parent,
    final WorkpackModel model,
    final Boolean allLevels
  ) {
    final Set<WorkpackModel> modelChildren = model.getChildren();
    if (modelChildren == null) {
      return new ArrayList<>();
    }
    final List<WorkpackModelBreakdownStructure> structures = new ArrayList<>();
    for (final WorkpackModel child : modelChildren) {
      final Set<Workpack> workpacks = getWorkpacks(
        parent,
        child
      );
      if (workpacks.isEmpty()) {
        continue;
      }
      final WorkpackModelBreakdownStructure workpackModelBreakdownStructure = new WorkpackModelBreakdownStructure();
      final WorkpackModelRepresentation summary = new WorkpackModelRepresentation();
      summary.setIdWorkpackModel(child.getId());
      summary.setWorkpackModelName(child.getModelNameInPlural());
      summary.setWorkpackModelType(child.getType());
      summary.setWorkpackModelPosition(child.getPosition());
      workpackModelBreakdownStructure.setRepresentation(summary);
      final List<WorkpackBreakdownStructure> workpackBreakdownStructures = new ArrayList<>();
      for (final Workpack workpack : workpacks) {
        final WorkpackBreakdownStructure structure = this.getStructure(
          workpack,
          child,
          allLevels
        );
        workpackBreakdownStructures.add(structure);
      }
      workpackBreakdownStructures.sort(Comparator.comparing(
        WorkpackBreakdownStructure::getOrder,
        Comparator.nullsLast(Comparator.naturalOrder())
      ));
      workpackModelBreakdownStructure.setWorkpacks(workpackBreakdownStructures);
      structures.add(workpackModelBreakdownStructure);
    }
    structures.sort(Comparator.comparing(WorkpackModelBreakdownStructure::getRepresentationPosition)
      .thenComparing(
        WorkpackModelBreakdownStructure::getName,
        Comparator.nullsLast(Comparator.naturalOrder())
      ));
    return structures;
  }

  private WorkpackBreakdownStructure getStructure(
    final Workpack workpack,
    final WorkpackModel child,
    final Boolean allLevels
  ) {
    final WorkpackBreakdownStructure structure = new WorkpackBreakdownStructure();
    structure.setOrder(workpack.getOrder());
    structure.setRepresentation(this.getWorkpackRepresentation.execute(workpack));
    if (!allLevels) {
      structure.setHasChildren(workpack.hasChildren());
      structure.setWorkpackModels(Collections.emptyList());
      return structure;
    }
    final List<WorkpackModelBreakdownStructure> children = this.getChildren(
      workpack,
      child,
      true
    );
    structure.setWorkpackModels(children);
    return structure;
  }

}
