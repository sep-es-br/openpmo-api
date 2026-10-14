package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetTotalizers;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpacksByModelResponse;
import br.gov.es.openpmo.dto.workpackmodel.TransversalStructuralLinkDto;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import br.gov.es.openpmo.repository.TransversalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DashboardDatasheetService implements IDashboardDatasheetService {

  private final DashboardDatasheetRepository repository;
  private final TransversalRepository transversalRepository;

  @Autowired
  public DashboardDatasheetService(
    final DashboardDatasheetRepository repository,
    final TransversalRepository transversalRepository
  ) {
    this.repository = repository;
    this.transversalRepository = transversalRepository;
  }

  @Override
  public DatasheetResponse build(final DashboardParameters parameters) {
    final Long workpackId = parameters.getWorkpackId();
    final Long workpackModelId = getWorkpackModelId(parameters);
    final UriComponentsBuilder uriComponentsBuilder = parameters.getUriComponentsBuilder();
    final boolean transversal = parameters.isTransversal();
    final List<TransversalStructuralLinkDto> transversalLinks = transversal
      ? this.getTransversalStructuralLinks(workpackId)
      : Collections.emptyList();

    return new DatasheetResponse(
      this.getDatasheetTotalizers(parameters.getPlanId(), workpackId, workpackModelId, transversal, transversalLinks),
      this.getDatasheetStakeholders(workpackId, uriComponentsBuilder)
    );
  }

  private static Long getWorkpackModelId(DashboardParameters parameters) {
    if (Boolean.TRUE.equals(parameters.getLinked())) {
      return parameters.getWorkpackModelLinkedId();
    }
    return parameters.getWorkpackModelId();
  }

  private DatasheetTotalizers getDatasheetTotalizers(
    final Long planId,
    final Long workpackId,
    final Long workpackModelId,
    final boolean transversal,
    final List<TransversalStructuralLinkDto> transversalLinks
  ) {
    final List<WorkpacksByModelResponse> workpacksByModel = transversal
      ? this.getWorkpackByModelForTransversal(workpackId, transversalLinks)
      : workpackId == null
      ? this.getWorkpackByModelForPlan(planId)
      : this.getWorkpackByModel(workpackId, workpackModelId);
    final Long deliverableCount = transversal
      ? this.countTransversalDeliverables(transversalLinks)
      : null;
    return new DatasheetTotalizers(workpacksByModel, deliverableCount);
  }

  private List<WorkpacksByModelResponse> getWorkpackByModelForTransversal(
    final Long workpackId,
    final List<TransversalStructuralLinkDto> transversalLinks
  ) {
    final Map<Long, WorkpacksByModelResponse> existingModels = this.repository
      .workpackByModelForTransversal(workpackId).stream()
      .map(WorkpacksByModelResponse::from)
      .collect(Collectors.toMap(WorkpacksByModelResponse::getIdWorkpackModel, Function.identity(), (first, second) -> first));
    final Map<Long, List<TransversalStructuralLinkDto>> linksByModel = transversalLinks
      .stream()
      .filter(TransversalStructuralLinkDto::isClickable)
      .collect(Collectors.groupingBy(TransversalStructuralLinkDto::getIdWorkpackModel));

    return linksByModel.entrySet().stream()
      .map(entry -> {
        final List<TransversalStructuralLinkDto> links = entry.getValue();
        final TransversalStructuralLinkDto first = links.get(0);
        final WorkpacksByModelResponse response = existingModels.getOrDefault(entry.getKey(), new WorkpacksByModelResponse());
        response.setIdWorkpackModel(entry.getKey());
        response.setQuantity((long) links.stream()
          .map(TransversalStructuralLinkDto::getIdWorkpack)
          .distinct()
          .count());
        response.setModelName(response.getQuantity() == 1
          ? first.getModelName()
          : first.getModelNameInPlural());
        response.setIcon(first.getFontIcon());
        if (response.getLevel() == null) { response.setLevel(0L); }
        if (response.getPosition() == null) { response.setPosition(0L); }
        return response;
      })
      .sorted((first, second) -> {
        final int byPosition = first.getPosition().compareTo(second.getPosition());
        return byPosition != 0 ? byPosition : first.getModelName().compareToIgnoreCase(second.getModelName());
      })
      .collect(Collectors.toList());
  }

  private Long countTransversalDeliverables(final List<TransversalStructuralLinkDto> transversalLinks) {
    return transversalLinks.stream()
      .filter(TransversalStructuralLinkDto::isClickable)
      .filter(link -> "Deliverable".equals(link.getWorkpackModelType()))
      .map(TransversalStructuralLinkDto::getIdWorkpack)
      .distinct()
      .count();
  }

  private List<TransversalStructuralLinkDto> getTransversalStructuralLinks(final Long workpackId) {
    if (workpackId == null) {
      return Collections.emptyList();
    }
    return this.transversalRepository.findStructuralLinks(workpackId, null, null, true, true);
  }

  private List<WorkpacksByModelResponse> getWorkpackByModelForPlan(final Long planId) {
    if (planId == null) {
      return Collections.emptyList();
    }
    return this.repository.workpackByModelForPlan(planId).stream()
      .map(WorkpacksByModelResponse::from)
      .collect(Collectors.toList());
  }

  private List<WorkpacksByModelResponse> getWorkpackByModel(final Long workpackId, Long workpackModelId) {
    if (workpackId == null) {
      return Collections.emptyList();
    }
    List<WorkpackByModelQueryResult> queryResults = this.repository.workpackByModel(workpackId, workpackModelId);
    List<WorkpacksByModelResponse> result = new ArrayList<>();
    for (WorkpackByModelQueryResult queryResult : queryResults) {
      WorkpacksByModelResponse from = WorkpacksByModelResponse.from(queryResult);
      result.add(from);
    }
    return result;
  }

  private Set<DatasheetStakeholderResponse> getDatasheetStakeholders(
    final Long workpackId,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    if (workpackId == null) {
      return Collections.emptySet();
    }
    final Collection<DatasheetStakeholderQueryResult> stakeholders = this.getStakeholders(workpackId);
    return stakeholders.stream()
      .map(stakeholder -> stakeholder.mapToResponse(uriComponentsBuilder))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private Set<DatasheetStakeholderQueryResult> getStakeholders(final Long workpackId) {
    final  List<DatasheetStakeholderQueryResult> stakeholders = this.repository.stakeholders(workpackId);
    return new LinkedHashSet<>(this.repository.stakeholders(workpackId));
  }

}
