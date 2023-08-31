package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpacksByModelResponse;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import br.gov.es.openpmo.service.properties.GetSorterProperty;
import br.gov.es.openpmo.service.properties.SorterProperty;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.dto.dashboards.datasheet.WorkpacksByModelResponse.WorkpacksByModelItem;

@Component
public class DashboardDatasheetService implements IDashboardDatasheetService {

  private final DashboardDatasheetRepository repository;
  private final GetSorterProperty getSorterProperty;

  @Autowired
  public DashboardDatasheetService(
    final DashboardDatasheetRepository repository,
    final GetSorterProperty getSorterProperty
  ) {
    this.repository = repository;
    this.getSorterProperty = getSorterProperty;
  }

  private static <T> Collection<T> ifCollectionNullThenEmpty(final Collection<T> list) {
    if (list == null) return Collections.emptyList();
    return list;
  }

  @Override
  public DatasheetResponse build(final DashboardParameters parameters) {
    final Long workpackId = parameters.getWorkpackId();
    final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();

    final DatasheetTotalizers datasheetTotalizers = this.getDatasheetTotalizers(parameters);
    datasheetTotalizers.sort();
    return new DatasheetResponse(
      datasheetTotalizers,
      this.getDatasheetStakeholders(workpackId, uriComponentsBuilder)
    );
  }

  private DatasheetTotalizers getDatasheetTotalizers(final DashboardParameters parameters) {
    if (Boolean.TRUE.equals(parameters.getLinked())) {
      return new DatasheetTotalizers(this.getWorkpackByLinkedModel(parameters));
    }
    return new DatasheetTotalizers(this.getWorkpackByModel(parameters));
  }

  private List<WorkpacksByModelResponse> getWorkpackByModel(final DashboardParameters parameters) {

    final List<WorkpackModel> workpackModels = this.repository.findFirstLayerWorkpackModelChildren(
      parameters.getWorkpackId(),
      parameters.getWorkpackModelId()
    );

    final Collection<WorkpacksByModelResponse> responseList = new HashSet<>();

    for (final WorkpackModel parent : workpackModels) {

      if (parent.hasChildren()) {

        final Optional<WorkpackModel> equivalentParentLinkedModel =
          Optional.ofNullable(parent.getLinkedToRelationship())
            .flatMap(list -> list.stream()
              .map(IsLinkedTo::getOriginalWorkpackModel)
              .findFirst()
            );

        final Set<WorkpackModel> equivalentLinkedModelChildren = this.getEquivalentLinkedModelChildren(
          equivalentParentLinkedModel,
          parameters.getPlanId()
        );
        responseList.addAll(
          this.process(
            equivalentLinkedModelChildren,
            this.repository.findWorkpackModelChildren(
              parent.getId(),
              parameters.getWorkpackId(),
              parameters.getPlanId()
            ),
            parameters.getPlanId(),
            !equivalentLinkedModelChildren.isEmpty(),
            parameters.getWorkpackId(),
            this.getSortPropertyFunction(parameters.getPersonId()),
            1
          ));
      }

    }

    final Set<WorkpacksByModelResponse> expandedWorkpacksByModelResponse = workpackModels.stream()
      .filter(this::hasInstanceOrLinked)
      .map(WorkpacksByModelResponse::of)
      .collect(Collectors.toSet());

    final Map<Long, Set<WorkpacksByModelItem>> itemGroupedByWorkpackModel = new HashMap<>();

    final Function<Long, SorterProperty<?>> sortPropertyFunction =
      this.getSortPropertyFunction(parameters.getPersonId());

    for (final WorkpackModel workpackModel : workpackModels) {

      final Set<WorkpacksByModelItem> items = Optional.ofNullable(workpackModel.getInstances())
        .map(workpacks -> workpacks.stream()
          .map(workpack -> WorkpacksByModelItem.parent(
            workpack,
            workpackModel,
            parameters.getPlanId(),
            sortPropertyFunction
          ))
          .collect(Collectors.toSet())
        )
        .orElseGet(HashSet::new);

      final Set<WorkpacksByModelItem> itemsLinked = Optional.ofNullable(workpackModel.getLinkedToRelationship())
        .map(linkedTo -> linkedTo.stream()
          .map(IsLinkedTo::getWorkpack)
          .map(workpack -> WorkpacksByModelItem.parentLinked(workpack, workpackModel.getId(), sortPropertyFunction))
          .collect(Collectors.toSet())
        )
        .orElseGet(Collections::emptySet);

      items.addAll(itemsLinked);
      itemGroupedByWorkpackModel.put(workpackModel.getId(), items);
    }


    expandedWorkpacksByModelResponse.forEach(
      response -> {
        response.addItems(itemGroupedByWorkpackModel.getOrDefault(
          response.getIdWorkpackModel(),
          new HashSet<>()
        ));
      }
    );

    responseList.addAll(expandedWorkpacksByModelResponse);

   return responseList.stream()
      .sorted(
        Comparator.comparing(WorkpacksByModelResponse::getDepth)
          .thenComparing(WorkpacksByModelResponse::getModelName)
      )
      .collect(Collectors.toList());
  }

  private List<WorkpacksByModelResponse> getWorkpackByLinkedModel(final DashboardParameters parameters) {

    final List<WorkpackModel> workpackModels = this.repository.findFirstLayerWorkpackModelChildren(
      parameters.getWorkpackId(),
      parameters.getWorkpackModelId()
    );
    final List<WorkpackModel> linkedWorkpackModel = this.repository.findFirstLayerWorkpackModelLinkedChildren(
      parameters.getWorkpackModelLinkedId(),
      parameters.getPlanId()
    );

    final Collection<WorkpacksByModelResponse> responseList = new HashSet<>();

    for (final WorkpackModel parent : workpackModels) {

      final Optional<WorkpackModel> equivalentParentLinkedModel = linkedWorkpackModel.stream()
        .filter(parent::hasSameName)
        .findFirst();

      if (parent.hasChildren()) {
        final Set<WorkpackModel> equivalentLinkedModelChildren = equivalentParentLinkedModel
          .map(model -> this.repository.findWorkpackModelLinkedEquivalentChildren(
            model.getId(),
            parameters.getPlanId()
          ))
          .orElseGet(Collections::emptySet);
        final Set<WorkpackModel> firstLayerWorkpackModelLinkedChildren =
          this.repository.findWorkpackModelLinkedChildren(
            parent.getId(),
            parameters.getPlanId()
          );
        responseList.addAll(
          this.processLinked(
            equivalentLinkedModelChildren,
            firstLayerWorkpackModelLinkedChildren,
            parameters.getPlanId(),
            this.getSortPropertyFunction(parameters.getPersonId()),
            1
          ));
      }
    }

    final Set<WorkpacksByModelResponse> expandedWorkpacksByModelResponse = workpackModels.stream()
      .filter(workpackModel -> CollectionUtils.isNotEmpty(workpackModel.getInstances()))
      .map(workpackModel -> WorkpacksByModelResponse.linked(workpackModel, linkedWorkpackModel))
      .collect(Collectors.toSet());

    final Map<Long, Set<WorkpacksByModelItem>> itemGroupedByWorkpackModel = new HashMap<>();

    for (final WorkpackModel workpackModel : workpackModels) {

      final Optional<WorkpackModel> equivalentLinkedWorkpackModel = linkedWorkpackModel.stream()
        .filter(workpackModel::hasSameName)
        .findFirst();

      if (!equivalentLinkedWorkpackModel.isPresent()) {
        continue;
      }

      final Set<WorkpacksByModelItem> items = Optional.ofNullable(workpackModel.getInstances())
        .map(workpacks -> workpacks.stream()
          .map(workpack -> WorkpacksByModelItem.parentLinkedEquivalent(
            workpack,
            equivalentLinkedWorkpackModel.get(),
            parameters.getPlanId(),
            this.getSortPropertyFunction(parameters.getPersonId())
          ))
          .collect(Collectors.toSet())
        )
        .orElseGet(Collections::emptySet);

      itemGroupedByWorkpackModel.put(equivalentLinkedWorkpackModel.get().getId(), items);
    }

    expandedWorkpacksByModelResponse.forEach(
      response -> {
        response.addItems(itemGroupedByWorkpackModel.getOrDefault(
          response.getIdWorkpackModel(),
          new HashSet<>()
        ));
      }
    );
    responseList.addAll(expandedWorkpacksByModelResponse);

    return responseList.stream()
      .sorted(
        Comparator.comparing(WorkpacksByModelResponse::getDepth)
          .thenComparing(WorkpacksByModelResponse::getModelName)
      )
      .collect(Collectors.toList());
  }

  private Set<WorkpackModel> getEquivalentLinkedModelChildren(
    final Optional<? extends WorkpackModel> equivalentParentLinkedModel,
    final Long planId
  ) {
    return equivalentParentLinkedModel
      .map(WorkpackModel::getId)
      .map(workpackModelId -> this.repository.findWorkpackModelLinkedChildren(workpackModelId, planId))
      .orElseGet(Collections::emptySet);
  }

  private Set<WorkpacksByModelResponse> processLinked(
    final Collection<? extends WorkpackModel> equivalentLinkedModelChildren,
    final Iterable<? extends WorkpackModel> children,
    final Long planIdLinked,
    final Function<Long, SorterProperty<?>> sortPropertyFunction,
    final int depth
  ) {

    final Collection<WorkpacksByModelResponse> responseList = new LinkedHashSet<>();

    for (final WorkpackModel child : children) {

      final Collection<WorkpacksByModelItem> grandChildItem = new ArrayList<>();
      final boolean isGrandChild = depth == 1;

      final long totalWorkpack = ifCollectionNullThenEmpty(child.getInstances()).stream()
        .distinct()
        .count();

      final Optional<? extends WorkpackModel> maybeEquivalentLinkedModel = ifCollectionNullThenEmpty(
        equivalentLinkedModelChildren).stream()
        .filter(model -> model.hasSameName(child))
        .findFirst();

      if (!maybeEquivalentLinkedModel.isPresent()) {
        continue;
      }

      if (isGrandChild) {
        grandChildItem.addAll(
          child.getInstances().stream()
            .map(workpack -> WorkpacksByModelItem.parentLinkedEquivalent(
              workpack,
              maybeEquivalentLinkedModel.get(),
              planIdLinked,
              sortPropertyFunction
            ))
            .collect(Collectors.toList())
        );
      }


      if (totalWorkpack > 0) {
        final WorkpacksByModelResponse response = new WorkpacksByModelResponse(
          totalWorkpack,
          maybeEquivalentLinkedModel.map(WorkpackModel::getId).orElse(null),
          maybeEquivalentLinkedModel.map(
            model -> totalWorkpack > 1 ? model.getModelNameInPlural() : model.getModelName()
          ).orElse(null),
          maybeEquivalentLinkedModel.map(WorkpackModel::getFontIcon).orElse(null),
          depth
        );
        response.addItems(grandChildItem);
        responseList.add(response);
      }

      if (child.hasChildren()) {
        responseList.addAll(
          this.processLinked(
            maybeEquivalentLinkedModel
              .map(model -> this.repository.findWorkpackModelLinkedEquivalentChildren(model.getId(), planIdLinked))
              .orElseGet(Collections::emptySet),
            this.repository.findWorkpackModelLinkedChildren(child.getId(), planIdLinked),
            planIdLinked,
            sortPropertyFunction,
            depth + 1
          )
        );
      }
    }

    return responseList.stream()
      .sorted(Comparator.comparing(WorkpacksByModelResponse::getModelName))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<WorkpacksByModelResponse> process(
    final Collection<? extends WorkpackModel> equivalentLinkedModelChildren,
    final Iterable<? extends WorkpackModel> children,
    final Long planId,
    final boolean linked,
    final Long parentId,
    final Function<Long, SorterProperty<?>> sortPropertyFunction,
    final int depth
  ) {

    final Collection<WorkpacksByModelResponse> responseList = new LinkedHashSet<>();

    for (final WorkpackModel child : children) {

      final Collection<WorkpacksByModelItem> grandChildItem = new LinkedHashSet<>();
      final boolean isGrandChild = depth == 1;

      if (isGrandChild) {
        grandChildItem.addAll(
          ifCollectionNullThenEmpty(child.getInstances()).stream()
            .map(workpack -> WorkpacksByModelItem.parent(
              workpack,
              child,
              planId,
              sortPropertyFunction
            ))
            .collect(Collectors.toList())
        );
      }

      final long totalWorkpack = ifCollectionNullThenEmpty(child.getInstances()).stream()
        .distinct()
        .count();

      final Optional<? extends WorkpackModel> equivalentModelLinked = ifCollectionNullThenEmpty(
        equivalentLinkedModelChildren).stream()
        .filter(model -> model.hasSameName(child))
        .findFirst();

      final long quantity;
      if (linked) {
        final int totalLinkedChildrenWorkpack = equivalentModelLinked.map(WorkpackModel::getInstances)
          .map(Set::size)
          .orElse(0);
        if (isGrandChild) {
          grandChildItem.addAll(
            equivalentModelLinked.map(WorkpackModel::getInstances)
              .map(list -> list.stream()
                .map(workpack -> WorkpacksByModelItem.parentLinked(
                  workpack,
                  child,
                  sortPropertyFunction
                ))
                .collect(Collectors.toList())
              )
              .orElseGet(Collections::emptyList)
          );
        }
        quantity = totalWorkpack + totalLinkedChildrenWorkpack;
      } else {
        final long totalLinkedWorkpack = ifCollectionNullThenEmpty(child.getLinkedToRelationship()).stream()
          .map(IsLinkedTo::getWorkpack)
          .map(Workpack::getId)
          .count();
        if (isGrandChild) {
          grandChildItem.addAll(
            ifCollectionNullThenEmpty(child.getLinkedToRelationship()).stream()
              .map(IsLinkedTo::getWorkpack)
              .map(workpack -> WorkpacksByModelItem.parentLinked(workpack, child, sortPropertyFunction))
              .collect(Collectors.toList())
          );
        }
        quantity = totalWorkpack + totalLinkedWorkpack;
      }

      if (quantity > 0) {
        final WorkpacksByModelResponse response = WorkpacksByModelResponse.of(
          child,
          quantity,
          depth
        );
        response.addItems(grandChildItem);
        responseList.add(response);
      }

      if (child.hasChildren()) {

        if (child.hasLinkedWorkpack()) {
          final Optional<WorkpackModel> equivalentParentLinkedModel = child.getLinkedToRelationship().stream()
            .filter(relation -> relation.getWorkpackModelId().equals(child.getId()))
            .map(IsLinkedTo::getOriginalWorkpackModel)
            .findFirst();
          responseList.addAll(
            this.process(
              this.getEquivalentLinkedModelChildren(equivalentParentLinkedModel, planId),
              this.repository.findWorkpackModelChildren(child.getId(), parentId, planId),
              planId,
              true,
              parentId,
              sortPropertyFunction,
              depth + 1
            )
          );
        } else {
          responseList.addAll(
            this.process(
              this.getEquivalentLinkedModelChildren(
                equivalentModelLinked,
                planId
              ),
              this.repository.findWorkpackModelChildren(child.getId(), parentId, planId),
              planId,
              linked,
              parentId,
              sortPropertyFunction,
              depth + 1
            )
          );
        }

      }
    }

    responseList.forEach(WorkpacksByModelResponse::sortWorkpacks);

    return responseList.stream()
      .sorted(Comparator.comparing(WorkpacksByModelResponse::getModelName))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private boolean hasInstanceOrLinked(final WorkpackModel workpackModel) {
    return CollectionUtils.isNotEmpty(workpackModel.getInstances()) ||
           CollectionUtils.isNotEmpty(workpackModel.getLinkedToRelationship());
  }

  private Set<DatasheetStakeholderResponse> getDatasheetStakeholders(
    final Long workpackId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getStakeholders(workpackId).stream()
      .map(stakeholder -> stakeholder.mapToResponse(uriComponentsBuilder))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
    return new LinkedHashSet<>(this.repository.stakeholders(workpackId));
  }

  private Function<Long, SorterProperty<?>> getSortPropertyFunction(final Long idPerson) {
    return idWorkpack -> this.getSorterProperty.execute(idWorkpack, idPerson);
  }

}
