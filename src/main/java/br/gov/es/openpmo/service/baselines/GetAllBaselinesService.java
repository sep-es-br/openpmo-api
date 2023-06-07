package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.baselines.GetAllBaselinesResponse;
import br.gov.es.openpmo.dto.baselines.GetAllCCBMemberBaselineResponse;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
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

  public static final int DO_NOT_SORT = 0;

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  private final AppProperties appProperties;

  private final CustomFilterService customFilterService;

  private final FindAllBaselineApprovedUsingCustomFilter findAllBaselineApproved;

  private final FindAllBaselineRejectedUsingCustomFilter findAllBaselineRejected;

  private final FindAllBaselineWaitingMyEvaluationUsingCustomFilter findAllBaselineWaitingMyEvaluation;

  private final FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter findAllBaselineWaitingOthersEvaluations;

  @Autowired
  public GetAllBaselinesService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IsCCBMemberRepository ccbMemberRepository,
    final AppProperties appProperties,
    final CustomFilterService customFilterService,
    final FindAllBaselineApprovedUsingCustomFilter findAllBaselineApproved,
    final FindAllBaselineRejectedUsingCustomFilter findAllBaselineRejected,
    final FindAllBaselineWaitingMyEvaluationUsingCustomFilter findAllBaselineWaitingMyEvaluation,
    final FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter findAllBaselineWaitingOthersEvaluations
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.ccbMemberRepository = ccbMemberRepository;
    this.appProperties = appProperties;
    this.customFilterService = customFilterService;
    this.findAllBaselineApproved = findAllBaselineApproved;
    this.findAllBaselineRejected = findAllBaselineRejected;
    this.findAllBaselineWaitingMyEvaluation = findAllBaselineWaitingMyEvaluation;
    this.findAllBaselineWaitingOthersEvaluations = findAllBaselineWaitingOthersEvaluations;
  }

  private static GetAllBaselinesResponse getBaselinesResponse(final Baseline baseline) {
    return new GetAllBaselinesResponse(
      baseline.getId(),
      baseline.getIdWorkpack(),
      baseline.getName(),
      baseline.getStatus(),
      baseline.getDescription(),
      baseline.getActivationDate(),
      baseline.getProposalDate(),
      baseline.getMessage(),
      baseline.isCancelation(),
      baseline.isActive()
    );
  }

  @Override
  public List<GetAllBaselinesResponse> getAllByWorkpackId(final Long idWorkpack) {
    return this.baselineRepository.findAllByWorkpackId(idWorkpack).stream()
      .map(GetAllBaselinesService::getBaselinesResponse)
      .collect(Collectors.toList());
  }

  @Override
  public List<GetAllCCBMemberBaselineResponse> getAllByPersonId(final Long idPerson) {
    return this.getWorkpacks(idPerson).stream()
      .map(this::getGetAllCCBMemberBaselineResponse)
      .collect(Collectors.toList());
  }

  @Override
  public List<GetAllBaselinesResponse> getAllByPersonIdAndStatus(
    Long idPerson,
    Long idFilter,
    String term,
    BaselineViewStatus status
  ) {
    List<Baseline> baselines = new ArrayList<>();
    final Double searchCutOffScore = appProperties.getSearchCutOffScore();
    switch (status) {
      case WAITING_MY_EVALUATION:
        baselines = getAllWaitingMyEvaluationByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        break;
      case WAITING_OTHERS_EVALUATIONS:
        baselines = getAllWaitingOthersEvaluationByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        break;
      case APPROVED:
        baselines = getAllApprovedByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        break;
      case REJECTED:
        baselines = getAllRejectedByPersonId(
          idPerson,
          idFilter,
          term,
          searchCutOffScore
        );
        break;
    }
    if (idFilter == null) {
      baselines.sort(GetAllBaselinesService::sortResponseByDate);
    }
    return baselines.stream()
      .map(GetAllBaselinesService::getBaselinesResponse)
      .collect(Collectors.toList());
  }

  private static int sortResponseByDate(
    Baseline first,
    Baseline second
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

  private List<Baseline> getAllApprovedByPersonId(
    Long idPerson,
    Long idFilter,
    String term,
    Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return baselineRepository.findAllApprovedByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = getParams(
      idPerson,
      term
    );
    return this.findAllBaselineApproved.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllRejectedByPersonId(
    Long idPerson,
    Long idFilter,
    String term,
    Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return baselineRepository.findAllRejectedByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = getParams(
      idPerson,
      term
    );
    return this.findAllBaselineRejected.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllWaitingMyEvaluationByPersonId(
    Long idPerson,
    Long idFilter,
    String term,
    Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return baselineRepository.findAllWaitingMyEvaluationByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = getParams(
      idPerson,
      term
    );
    return this.findAllBaselineWaitingMyEvaluation.execute(
      customFilter,
      params
    );
  }

  private List<Baseline> getAllWaitingOthersEvaluationByPersonId(
    Long idPerson,
    Long idFilter,
    String term,
    Double searchCutOffScore
  ) {
    if (idFilter == null) {
      return baselineRepository.findAllWaitingOthersEvaluationByPersonId(
        idPerson,
        term,
        searchCutOffScore
      );
    }
    final CustomFilter customFilter = this.customFilterService.findById(
      idFilter,
      idPerson
    );
    final Map<String, Object> params = getParams(
      idPerson,
      term
    );
    return this.findAllBaselineWaitingOthersEvaluations.execute(
      customFilter,
      params
    );
  }

  private Map<String, Object> getParams(
    Long idPerson,
    String term
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
      appProperties.getSearchCutOffScore()
    );
    return params;
  }

  private GetAllCCBMemberBaselineResponse getGetAllCCBMemberBaselineResponse(final Workpack workpack) {
    final List<GetAllBaselinesResponse> baselines = new ArrayList<>();
    for (final Baseline baseline : this.getBaselines(workpack)) {
      baselines.add(getBaselinesResponse(baseline));
    }
    return new GetAllCCBMemberBaselineResponse(
      workpack.getId(),
      this.getWorkpackName(workpack),
      baselines
    );
  }

  private String getWorkpackName(final Workpack workpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName)
      .orElse(null);
  }

  private List<Baseline> getBaselines(final Workpack workpack) {
    return this.baselineRepository.findAllByWorkpackId(workpack.getId());
  }

  private List<Workpack> getWorkpacks(final Long idPerson) {
    return this.ccbMemberRepository.findAllWorkpacksByPersonId(idPerson);
  }

}
