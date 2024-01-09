package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelRepresentation;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessDataResponse;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GetWorkpackBreakdownStructure {

  private final GetWorkpackRepresentation getWorkpackRepresentation;

  private final WorkpackRepository workpackRepository;

  private final ICanAccessData canAccessData;

  public GetWorkpackBreakdownStructure(
    final GetWorkpackRepresentation getWorkpackRepresentation,
    final WorkpackRepository workpackRepository,
    final ICanAccessData canAccessData
  ) {
    this.getWorkpackRepresentation = getWorkpackRepresentation;
    this.workpackRepository = workpackRepository;
    this.canAccessData = canAccessData;
  }

  public WorkpackBreakdownStructure execute(
    final Long idWorkpack,
    final Boolean allLevels,
    final String authorization
  ) {
    if (idWorkpack == null) {
      throw new IllegalStateException("Id do workpack nulo!");
    }
    if (this.hasOnlyBasicReadPermission(idWorkpack, authorization)) {
      return null;
    }
    final Workpack workpack = this.getWorkpack(idWorkpack, allLevels);
    final WorkpackModel model = workpack.getWorkpackModelInstance();
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
    final WorkpackBreakdownStructure rootStructure = new WorkpackBreakdownStructure();
    rootStructure.setWorkpackModels(children);
    rootStructure.setRepresentation(this.getWorkpackRepresentation.execute(workpack));
    return rootStructure;
  }

  private Workpack getWorkpack(
    final Long idWorkpack,
    final Boolean allLevels) 
  {
    if (allLevels) {
      return this.workpackRepository.findWorkpackWithModelStructureById(idWorkpack)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
    }
    return this.workpackRepository.findWorkpackWithModelStructureByIdFirstLevel(idWorkpack)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private boolean hasOnlyBasicReadPermission(
    final Long idWorkpack,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccessData = this.canAccessData.execute(idWorkpack, authorization);
    if (canAccessData.getAdmin()) {
      return false;
    }
    if (canAccessData.getEdit()) {
      return false;
    }
    if (canAccessData.getRead()) {
      return false;
    }
    return canAccessData.getBasicRead();
  }

  private List<WorkpackModelBreakdownStructure> getChildren(
    final Workpack parent,
    final WorkpackModel model,
    final Boolean allLevels
  ) {
    final Set<WorkpackModel> modelChildren = model.getChildren();
    final List<WorkpackModelBreakdownStructure> structures = new ArrayList<>();
    if (modelChildren != null && !modelChildren.isEmpty()) {
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
        final List<WorkpackBreakdownStructure> workpackBreakdownStructures = new CopyOnWriteArrayList<>();
        workpacks.forEach(workpack -> {
          final WorkpackBreakdownStructure structure = this.getStructure(
            workpack,
            child,
            allLevels
          );
          workpackBreakdownStructures.add(structure);
        });
        workpackBreakdownStructures.sort(Comparator.comparing(
          WorkpackBreakdownStructure::getOrder,
          Comparator.nullsLast(Comparator.naturalOrder())
        ));
        workpackModelBreakdownStructure.setWorkpacks(workpackBreakdownStructures);
        structures.add(workpackModelBreakdownStructure);
      }
      structures.sort(
        Comparator.comparing(WorkpackModelBreakdownStructure::getRepresentationPosition)
          .thenComparing(
            WorkpackModelBreakdownStructure::getName,
            Comparator.nullsLast(Comparator.naturalOrder())
          ));
    }
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
    if (allLevels) {
      final List<WorkpackModelBreakdownStructure> children = this.getChildren(workpack, child, true);
      structure.setWorkpackModels(children);
      return structure;
    }
    structure.setHasChildren(workpack.isIsParent());
    structure.setWorkpackModels(Collections.emptyList());
    return structure;
  }

  private static Set<Workpack> getWorkpacks(
    final Workpack parent,
    final WorkpackModel child
  ) {
    final Set<? extends Workpack> childWorkpacks = child.getInstances();
    final Set<Workpack> workpacks = new HashSet<>();
    if (childWorkpacks != null) {
      for (final Workpack workpack : childWorkpacks) {
        if (parent.containsChild(workpack)) {
          workpacks.add(workpack);
        }
      }
    }
    return workpacks;
  }

}
