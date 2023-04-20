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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GetWorkpackBreakdownStructure {

  private final GetWorkpackRepresentation getWorkpackRepresentation;

  private final WorkpackRepository workpackRepository;

  public GetWorkpackBreakdownStructure(
    GetWorkpackRepresentation getWorkpackRepresentation,
    WorkpackRepository workpackRepository
  ) {
    this.getWorkpackRepresentation = getWorkpackRepresentation;
    this.workpackRepository = workpackRepository;
  }

  public WorkpackBreakdownStructure execute(Long idWorkpack) {
    if (idWorkpack == null) {
      throw new IllegalStateException("Id do workpack nulo!");
    }
    Workpack workpack = workpackRepository.findWorkpackWithModelStructureById(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    final WorkpackModel model = workpack.getWorkpackModelInstance();
    final WorkpackBreakdownStructure rootStructure = new WorkpackBreakdownStructure();
    if (model == null) {
      return null;
    }
    final List<WorkpackModelBreakdownStructure> children = getChildren(
      workpack,
      model
    );
    if (children.isEmpty()) {
      return null;
    }
    rootStructure.setWorkpackModels(children);
    rootStructure.setRepresentation(getWorkpackRepresentation.execute(workpack));
    return rootStructure;
  }

  private List<WorkpackModelBreakdownStructure> getChildren(
    Workpack parent,
    WorkpackModel model
  ) {
    final Set<WorkpackModel> modelChildren = model.getChildren();
    if (modelChildren == null) {
      return new ArrayList<>();
    }
    List<WorkpackModelBreakdownStructure> structures = new ArrayList<>();
    for (WorkpackModel child : modelChildren) {
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
      workpackModelBreakdownStructure.setRepresentation(summary);
      final List<WorkpackBreakdownStructure> workpackBreakdownStructures = new ArrayList<>();
      for (Workpack workpack : workpacks) {
        final WorkpackBreakdownStructure structure = getStructure(
          workpack,
          child
        );
        workpackBreakdownStructures.add(structure);
      }
      workpackModelBreakdownStructure.setWorkpacks(workpackBreakdownStructures);
      structures.add(workpackModelBreakdownStructure);
    }
    return structures;
  }

  private static Set<Workpack> getWorkpacks(
    Workpack parent,
    WorkpackModel child
  ) {
    final Set<Workpack> childWorkpacks = child.getWorkpacks();
    if (childWorkpacks == null) {
      return new HashSet<>();
    }
    Set<Workpack> workpacks = new HashSet<>();
    for (Workpack workpack : childWorkpacks) {
      if (parent.containsChild(workpack)) {
        workpacks.add(workpack);
      }
    }
    return workpacks;
  }

  private WorkpackBreakdownStructure getStructure(
    Workpack workpack,
    WorkpackModel child
  ) {
    final WorkpackBreakdownStructure structure = new WorkpackBreakdownStructure();
    structure.setRepresentation(getWorkpackRepresentation.execute(workpack));
    final List<WorkpackModelBreakdownStructure> children = getChildren(
      workpack,
      child
    );
    structure.setWorkpackModels(children);
    return structure;
  }

}
