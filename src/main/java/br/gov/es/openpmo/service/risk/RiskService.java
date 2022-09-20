package br.gov.es.openpmo.service.risk;

import br.gov.es.openpmo.dto.risk.RiskCardDto;
import br.gov.es.openpmo.dto.risk.RiskCreateDto;
import br.gov.es.openpmo.dto.risk.RiskDetailDto;
import br.gov.es.openpmo.dto.risk.RiskUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.RiskResponseRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllRiskUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.HAS_ISSUE_RELATIONSHIP;
import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.RISK_NOT_FOUND;

@Service
public class RiskService {

  private final RiskRepository repository;

  private final RiskResponseRepository riskResponseRepository;

  private final WorkpackService workpackService;

  private final CustomFilterService customFilterService;

  private final FindAllRiskUsingCustomFilter findAllRisk;

  private final JournalCreator journalCreator;

  public RiskService(
    final RiskRepository repository,
    final RiskResponseRepository riskResponseRepository,
    final WorkpackService workpackService,
    final CustomFilterService customFilterService,
    final FindAllRiskUsingCustomFilter findAllRisk,
    final JournalCreator journalCreator
  ) {
    this.repository = repository;
    this.riskResponseRepository = riskResponseRepository;
    this.workpackService = workpackService;
    this.customFilterService = customFilterService;
    this.findAllRisk = findAllRisk;
    this.journalCreator = journalCreator;
  }

  public List<RiskCardDto> findAllAsCardDto(
    final Long idWorkpack,
    final Long idFilter,
    final Long idPerson
  ) {
    if(idWorkpack == null) {
      throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
    }
    if(idFilter == null) {
      return this.findAllAsCardDto(idWorkpack);
    }
    final CustomFilter customFilter = this.customFilterService.findById(idFilter, idPerson);
    final Map<String, Object> params = new HashMap<>();

    params.put("idWorkpack", idWorkpack);

    final List<Risk> risks = this.findAllRisk.execute(customFilter, params);

    return risks.stream()
      .map(RiskCardDto::of)
      .collect(Collectors.toList());
  }

  private List<RiskCardDto> findAllAsCardDto(final Long idWorkpack) {
    return this.repository.findAll(idWorkpack).stream()
      .map(RiskCardDto::of)
      .collect(Collectors.toList());
  }

  public Risk create(
    final RiskCreateDto request,
    final Long idPerson
  ) {
    if(request.getIdWorkpack() == null) {
      throw new IllegalStateException(ID_WORKPACK_NOT_NULL);
    }
    final Workpack workpack = this.workpackService.findById(request.getIdWorkpack());
    final Risk risk = Risk.of(request, workpack);
    this.repository.save(risk);
    this.journalCreator.risk(risk, idPerson);
    return risk;
  }

  public RiskDetailDto update(
    final @Valid RiskUpdateDto request,
    final Long idPerson
  ) {
    final Risk risk = this.findById(request.getId());
    risk.update(request);
    this.repository.save(risk, 0);
    this.journalCreator.risk(risk, idPerson);
    return RiskDetailDto.of(risk);
  }

  public Risk findById(final Long id) {
    return this.repository.findRiskDetailById(id)
      .orElseThrow(() -> new NegocioException(RISK_NOT_FOUND));
  }

  public RiskDetailDto findByIdAsRiskDetail(final Long id) {
    return RiskDetailDto.of(this.findById(id));
  }

  @Transactional
  public void deleteById(final Long idRisk) {
    this.throwExceptionIfRiskHaveRelationshipWithIssue(idRisk);
    this.deleteAllRiskResponseRelated(idRisk);
    this.repository.deleteById(idRisk);
  }

  private void throwExceptionIfRiskHaveRelationshipWithIssue(final Long idRisk) {
    final Optional<Risk> maybeRisk = this.repository.findRiskIfHasIssueRelationship(idRisk);
    maybeRisk.ifPresent(risk -> {
      throw new NegocioException(HAS_ISSUE_RELATIONSHIP);
    });
  }

  private void deleteAllRiskResponseRelated(final Long idRisk) {
    final Collection<RiskResponse> riskResponses = this.riskResponseRepository.findAllByRiskId(idRisk);
    this.riskResponseRepository.deleteAll(riskResponses);
  }

}
