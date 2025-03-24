package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.baselines.GetAllBaselinesResponse;
import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllBaselineApprovedUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllBaselineRejectedUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllBaselineWaitingMyEvaluationUsingCustomFilter;
import br.gov.es.openpmo.repository.custom.filters.FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GetAllBaselinesService implements IGetAllBaselinesService {

  private static final int DO_NOT_SORT = 0;

  private final BaselineRepository baselineRepository;

  private final AppProperties appProperties;

  private final CustomFilterService customFilterService;

  private final FindAllBaselineApprovedUsingCustomFilter findAllBaselineApproved;

  private final FindAllBaselineRejectedUsingCustomFilter findAllBaselineRejected;

  private final FindAllBaselineWaitingMyEvaluationUsingCustomFilter findAllBaselineWaitingMyEvaluation;

  private final FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter findAllBaselineWaitingOthersEvaluations;


  @Autowired
  public GetAllBaselinesService(
    final BaselineRepository baselineRepository,
    final AppProperties appProperties,
    final CustomFilterService customFilterService,
    final FindAllBaselineApprovedUsingCustomFilter findAllBaselineApproved,
    final FindAllBaselineRejectedUsingCustomFilter findAllBaselineRejected,
    final FindAllBaselineWaitingMyEvaluationUsingCustomFilter findAllBaselineWaitingMyEvaluation,
    final FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter findAllBaselineWaitingOthersEvaluations
  ) {
    this.baselineRepository = baselineRepository;
    this.appProperties = appProperties;
    this.customFilterService = customFilterService;
    this.findAllBaselineApproved = findAllBaselineApproved;
    this.findAllBaselineRejected = findAllBaselineRejected;
    this.findAllBaselineWaitingMyEvaluation = findAllBaselineWaitingMyEvaluation;
    this.findAllBaselineWaitingOthersEvaluations = findAllBaselineWaitingOthersEvaluations;
  }

  private static GetAllBaselinesResponse getBaselinesResponse(
    final Baseline baseline
  ) {
    return new GetAllBaselinesResponse(
      baseline.getId(),
      baseline.getIdWorkpack(),
      baseline.getName(),
      baseline.getWorkpackName(),
      baseline.getStatus(),
      baseline.getDescription(),
      baseline.getActivationDate(),
      baseline.getProposalDate(),
      baseline.getMessage(),
      baseline.isCancelation(),
      baseline.isActive()
    );
  }

  private static int sortResponseByDate(
    final Baseline first,
    final Baseline second
  ) {
    final LocalDateTime firstActivationDate = first.getActivationDate();
    final LocalDateTime secondActivationDate = second.getActivationDate();
    if (firstActivationDate != null && secondActivationDate != null) {
      return secondActivationDate.compareTo(firstActivationDate);
    }
    final LocalDateTime firstProposalDate = first.getProposalDate();
    final LocalDateTime secondProposalDate = second.getProposalDate();
    if (firstProposalDate != null && secondProposalDate != null) {
      return secondProposalDate.compareTo(firstProposalDate);
    }
    return DO_NOT_SORT;
  }

  private static int sortResponseByDateReversed(
    final Baseline first,
    final Baseline second
  ) {
    final LocalDateTime firstActivationDate = first.getActivationDate();
    final LocalDateTime secondActivationDate = second.getActivationDate();
    if (firstActivationDate != null && secondActivationDate != null) {
      return firstActivationDate.compareTo(secondActivationDate);
    }
    final LocalDateTime firstProposalDate = first.getProposalDate();
    final LocalDateTime secondProposalDate = second.getProposalDate();
    if (firstProposalDate != null && secondProposalDate != null) {
      return firstProposalDate.compareTo(secondProposalDate);
    }
    return DO_NOT_SORT;
  }

  @Override
  public List<GetAllBaselinesResponse> getAllByWorkpackId(final Long idWorkpack) {
    return this.baselineRepository.findAllByWorkpackId(idWorkpack).stream()
      .map(baseline -> getBaselinesResponse(baseline))
      .collect(Collectors.toList());
  }

  @Override
  public List<GetAllBaselinesResponse> getAllByPersonIdAndStatus(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final BaselineViewStatus status
  ) {
    List<Baseline> baselines = new ArrayList<>();
    final Double searchCutOffScore = this.appProperties.getSearchCutOffScore();
    baselines = this.handleStatusBaseline(idPerson, idFilter, term, status, baselines, searchCutOffScore);
    return baselines.stream()
      .map(baseline -> getBaselinesResponse(baseline))
      .collect(Collectors.toList());
  }

  private List<Baseline> handleStatusBaseline(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final BaselineViewStatus status,
    final List<Baseline> baselines,
    final Double searchCutOffScore
  ) {
    final boolean hasFilter = idFilter == null;
    switch (status) {
      case WAITING_MY_EVALUATION:
        final List<Baseline> allWaitingMyEvaluationByPersonId = this.getAllWaitingMyEvaluationByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        if (hasFilter) {
          allWaitingMyEvaluationByPersonId
            .sort(GetAllBaselinesService::sortResponseByDateReversed);
        }
        return allWaitingMyEvaluationByPersonId;
      case WAITING_OTHERS_EVALUATIONS:
        final List<Baseline> allWaitingOthersEvaluationByPersonId = this.getAllWaitingOthersEvaluationByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        if (hasFilter) {
          allWaitingOthersEvaluationByPersonId
            .sort(GetAllBaselinesService::sortResponseByDateReversed);
        }
        return allWaitingOthersEvaluationByPersonId;
      case APPROVEDS:
        final List<Baseline> allApprovedByPersonId = this.getAllApprovedByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        if (hasFilter) {
          allApprovedByPersonId
            .sort(GetAllBaselinesService::sortResponseByDate);
        }
        return allApprovedByPersonId;
      case REJECTEDS:
        final List<Baseline> allRejectedByPersonId = this.getAllRejectedByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        if (hasFilter) {
          allRejectedByPersonId
            .sort(GetAllBaselinesService::sortResponseByDate);
        }
        return allRejectedByPersonId;
    }
    return baselines;
  }

  private List<Baseline> getAllApprovedByPersonId(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return this.baselineRepository.findAllApprovedByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = this.getParams(
      idPerson,
      term
    );
    return this.findAllBaselineApproved.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllRejectedByPersonId(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return this.baselineRepository.findAllRejectedByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = this.getParams(
      idPerson,
      term
    );
    return this.findAllBaselineRejected.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllWaitingMyEvaluationByPersonId(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return this.baselineRepository.findAllWaitingMyEvaluationByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = this.getParams(
      idPerson,
      term
    );
    return this.findAllBaselineWaitingMyEvaluation.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllWaitingOthersEvaluationByPersonId(
    final Long idPerson,
    final Long idFilter,
    final String term,
    final Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return this.baselineRepository.findAllWaitingOthersEvaluationByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = this.getParams(
      idPerson,
      term
    );
    return this.findAllBaselineWaitingOthersEvaluations.execute(
      customFilter,
      params
    );
  }

  private Map<String, Object> getParams(
    final Long idPerson,
    final String term
  ) {
    final Map<String, Object> params = new HashMap<>();
    params.put(
      "idPerson",
      idPerson
    );
    params.put(
      "term",
      term
    );
    params.put(
      "searchCutOffScore",
      this.appProperties.getSearchCutOffScore()
    );
    return params;
  }

}
