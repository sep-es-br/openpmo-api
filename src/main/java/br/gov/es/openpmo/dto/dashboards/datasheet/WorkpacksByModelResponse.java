package br.gov.es.openpmo.dto.dashboards.datasheet;

import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.HasValue;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.properties.SorterProperty;
import br.gov.es.openpmo.service.workpack.PropertyComparator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkpacksByModelResponse {

  private final Long quantity;

  private final Long idWorkpackModel;

  private final String modelName;

  private final String icon;

  private final int depth;

  private final List<WorkpacksByModelItem> workpacks = new ArrayList<>();

  public WorkpacksByModelResponse(
    final Long quantity,
    final Long idWorkpackModel,
    final String modelName,
    final String icon,
    final int depth
  ) {
    this.quantity = quantity;
    this.idWorkpackModel = idWorkpackModel;
    this.modelName = modelName;
    this.icon = icon;
    this.depth = depth;
  }

  public static WorkpacksByModelResponse of(final WorkpackModel workpackModel) {
    final long quantity = Stream.of(
        ifCollectionNullThenEmpty(workpackModel.getInstances()).stream().map(Workpack::getId),
        ifCollectionNullThenEmpty(workpackModel.getLinkedToRelationship()).stream()
          .map(IsLinkedTo::getWorkpack)
          .map(Workpack::getId)
      )
      .flatMap(Function.identity())
      .count();
    return new WorkpacksByModelResponse(
      quantity,
      workpackModel.getId(),
      quantity == 1 ? workpackModel.getModelName() : workpackModel.getModelNameInPlural(),
      workpackModel.getFontIcon(),
      0
    );
  }

  public static WorkpacksByModelResponse of(
    final WorkpackModel workpackModel,
    final long quantity,
    final int depth
  ) {
    return new WorkpacksByModelResponse(
      quantity,
      workpackModel.getId(),
      quantity == 1 ? workpackModel.getModelName() : workpackModel.getModelNameInPlural(),
      workpackModel.getFontIcon(),
      depth
    );
  }

  private static <T> Collection<T> ifCollectionNullThenEmpty(final Collection<T> list) {
    if (list == null) return Collections.emptyList();
    return list;
  }

  public static WorkpacksByModelResponse linked(
    final WorkpackModel workpackModel,
    final Collection<WorkpackModel> linkedWorkpackModel
  ) {
    final long quantity = ifCollectionNullThenEmpty(workpackModel.getInstances()).size();
    final Optional<WorkpackModel> equivalentLinkedWorkpackModel = linkedWorkpackModel.stream()
      .filter(model -> model.hasSameName(workpackModel))
      .findFirst();
    return new WorkpacksByModelResponse(
      quantity,
      equivalentLinkedWorkpackModel.map(WorkpackModel::getId).orElse(null),
      equivalentLinkedWorkpackModel.map(
          model -> quantity == 1 ? model.getModelName() : model.getModelNameInPlural())
        .orElse(null),
      equivalentLinkedWorkpackModel.map(WorkpackModel::getFontIcon).orElse(null),
      0
    );
  }


  public Long getQuantity() {
    return this.quantity;
  }

  public String getModelName() {
    return this.modelName;
  }

  public String getIcon() {
    return this.icon;
  }

  public List<WorkpacksByModelItem> getWorkpacks() {
    return Collections.unmodifiableList(this.workpacks);
  }

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public int getDepth() {
    return this.depth;
  }

  public void addItems(final Collection<WorkpacksByModelItem> items) {
    this.workpacks.addAll(items);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    final WorkpacksByModelResponse that = (WorkpacksByModelResponse) o;

    return this.idWorkpackModel.equals(that.idWorkpackModel);
  }

  @Override
  public int hashCode() {
    return this.idWorkpackModel.hashCode();
  }

  public void sortWorkpacks() {
    if (this.workpacks.isEmpty()) return;
    this.workpacks.sort(
      (a, b) -> {
        final SortByDirectionEnum sort = Optional.ofNullable(a.getSort().getDirection())
          .orElseGet(() -> Optional.ofNullable(b.getSort().getDirection()).orElse(SortByDirectionEnum.ASC));
        return PropertyComparator.compare(a.getSort().getValue(), b.getSort().getValue()) * sort.getOrder();
      }
    );
    this.workpacks.forEach(WorkpacksByModelItem::sortWorkpacks);
  }

  public static class WorkpacksByModelItem {
    private final Long id;
    private final Long idWorkpackModel;
    private final Long idPlan;
    private final String name;
    private final String icon;
    private final Boolean linked;
    private final SorterProperty<?> sort;
    private final List<WorkpacksByModelItem> workpacks = new ArrayList<>();

    public WorkpacksByModelItem(
      final Long id,
      final Long idWorkpackModel,
      final Long idPlan,
      final String name,
      final String icon,
      final Boolean linked,
      final SorterProperty<?> sort
    ) {
      this.id = id;
      this.idWorkpackModel = idWorkpackModel;
      this.idPlan = idPlan;
      this.name = name;
      this.icon = icon;
      this.linked = linked;
      this.sort = sort;
    }

    public static WorkpacksByModelItem parent(
      final Workpack workpack,
      final WorkpackModel workpackModel,
      final Long planId,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {
      final WorkpacksByModelItem item = new WorkpacksByModelItem(
        workpack.getId(),
        workpack.getIdWorkpackModel(),
        workpack.getOriginalPlan().map(Plan::getId).orElse(null),
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        workpack.getIcon(),
        false,
        sortPropertyFunction.apply(workpack.getId())
      );
      item.addChildren(
        Optional.ofNullable(workpack.getChildren())
          .map(children -> children.stream()
            .map(child -> child(child, workpackModel, planId, sortPropertyFunction))
            .collect(Collectors.toList())
          )
          .orElseGet(Collections::emptyList)
      );
      return item;
    }

    public static WorkpacksByModelItem parentLinked(
      final Workpack workpack,
      final Long idWorkpackModel,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {
      final WorkpacksByModelItem item = new WorkpacksByModelItem(
        workpack.getId(),
        workpack.getLinkedWorkpackModel(idWorkpackModel).map(WorkpackModel::getId).orElse(null),
        workpack.getOriginalPlan().map(Plan::getId).orElse(null),
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        workpack.getLinkedWorkpackModel(idWorkpackModel).map(WorkpackModel::getFontIcon).orElse(null),
        true,
        sortPropertyFunction.apply(workpack.getId())
      );
      final Optional<WorkpackModel> linkedWorkpackModel = workpack.getLinkedWorkpackModel(idWorkpackModel);
      item.addChildren(
        Optional.ofNullable(workpack.getChildren())
          .map(children -> children.stream()
            .map(child -> WorkpacksByModelItem.childLinked(
              child,
              linkedWorkpackModel.orElseGet(workpack::getWorkpackModelInstance),
              sortPropertyFunction
            ))
            .collect(Collectors.toList())
          )
          .orElseGet(Collections::emptyList)

      );
      return item;
    }

    public static WorkpacksByModelItem parentLinked(
      final Workpack workpack,
      final WorkpackModel workpackModel,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {
      final Optional<WorkpackModel> linkedWorkpackModel = workpack.getLinkedWorkpackModel(workpackModel.getId());
      final WorkpacksByModelItem item = new WorkpacksByModelItem(
        workpack.getId(),
        workpackModel.getId(),
        workpack.getOriginalPlan().map(Plan::getId).orElse(null),
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        workpackModel.getFontIcon(),
        true,
        sortPropertyFunction.apply(workpack.getId())
      );
      item.addChildren(
        Optional.ofNullable(workpack.getChildren())
          .map(children -> children.stream()
            .map(child -> WorkpacksByModelItem.childLinked(
              child,
              linkedWorkpackModel.orElseGet(workpack::getWorkpackModelInstance),
              sortPropertyFunction
            ))
            .collect(Collectors.toList())
          )
          .orElseGet(Collections::emptyList)

      );
      return item;
    }

    public static WorkpacksByModelItem child(
      final Workpack workpack,
      final WorkpackModel workpackModel,
      final Long planId,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {

      final boolean hasLinkedToRelationship = workpack.hasLinkedToRelationship();

      final Optional<WorkpackModel> maybeLinkedModel = workpackModel.getChildren().stream()
        .filter(model -> workpack.getLinkedWorkpackModel(model.getId()).isPresent())
        .findFirst();
      if (hasLinkedToRelationship) {
        return new WorkpacksByModelItem(
          workpack.getId(),
          maybeLinkedModel.map(WorkpackModel::getId).orElse(null),
          planId,
          workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
          maybeLinkedModel.map(WorkpackModel::getFontIcon).orElseGet(workpack::getIcon),
          true,
          sortPropertyFunction.apply(workpack.getId())
        );
      }
      return new WorkpacksByModelItem(
        workpack.getId(),
        workpack.getIdWorkpackModel(),
        planId,
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        workpack.getIcon(),
        false,
        sortPropertyFunction.apply(workpack.getId())
      );
    }

    public static WorkpacksByModelItem childLinked(
      final Workpack workpack,
      final WorkpackModel linkedToModel,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {

      final Optional<WorkpackModel> equivalentChildModel = linkedToModel.getChildren().stream()
        .filter(childModel -> childModel.hasSameName(workpack.getWorkpackModelInstance()))
        .findFirst();

      return new WorkpacksByModelItem(
        workpack.getId(),
        equivalentChildModel.map(WorkpackModel::getId).orElse(null),
        workpack.getOriginalPlan().map(Plan::getId).orElse(null),
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        equivalentChildModel.map(WorkpackModel::getFontIcon).orElse(null),
        true,
        sortPropertyFunction.apply(workpack.getId())
      );
    }

    public static WorkpacksByModelItem parentLinkedEquivalent(
      final Workpack workpack,
      final WorkpackModel linkedModelEquivalent,
      final Long planId,
      final Function<Long, SorterProperty<?>> sortPropertyFunction
    ) {
      final WorkpacksByModelItem parent = new WorkpacksByModelItem(
        workpack.getId(),
        linkedModelEquivalent.getId(),
        planId,
        workpack.getPropertyName().map(HasValue::getValue).map(String.class::cast).orElse(null),
        linkedModelEquivalent.getFontIcon(),
        true,
        sortPropertyFunction.apply(workpack.getId())
      );
      parent.addChildren(
        Optional.ofNullable(workpack.getChildren())
          .map(children -> children.stream()
            .map(child -> WorkpacksByModelItem.childLinked(
              child,
              linkedModelEquivalent,
              sortPropertyFunction
            ))
            .collect(Collectors.toList())
          )
          .orElseGet(Collections::emptyList)

      );
      return parent;
    }

    private void addChildren(final Collection<? extends WorkpacksByModelItem> children) {
      this.workpacks.addAll(children);
    }

    public Long getId() {
      return this.id;
    }


    public Long getIdWorkpackModel() {
      return this.idWorkpackModel;
    }


    public Long getIdPlan() {
      return this.idPlan;
    }


    public String getName() {
      return this.name;
    }


    public String getIcon() {
      return this.icon;
    }


    public Boolean getLinked() {
      return this.linked;
    }


    public List<WorkpacksByModelItem> getWorkpacks() {
      return this.workpacks;
    }

    @JsonIgnore
    public SorterProperty<?> getSort() {
      return this.sort;
    }

    public void sortWorkpacks() {
      if (this.workpacks.isEmpty()) return;
      this.workpacks.sort(
        (a, b) -> {
          final SortByDirectionEnum sort = Optional.ofNullable(a.sort.getDirection())
            .orElseGet(() -> Optional.ofNullable(b.sort.getDirection()).orElse(SortByDirectionEnum.ASC));
          return PropertyComparator.compare(a.sort.getValue(), b.sort.getValue()) * sort.getOrder();
        }
      );
      this.workpacks.forEach(WorkpacksByModelItem::sortWorkpacks);
    }
  }

}
