package br.gov.es.openpmo.service.issue;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.issue.IssueCardDto;
import br.gov.es.openpmo.dto.issue.IssueCreateDto;
import br.gov.es.openpmo.dto.issue.IssueDetailDto;
import br.gov.es.openpmo.dto.issue.IssueUpdateDto;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IssueRepository;
import br.gov.es.openpmo.repository.IssueResponseRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllIssueUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.risk.RiskService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.ISSUE_NOT_FOUND;

@Service
public class IssueService {

  private final IssueRepository repository;

  private final IssueResponseRepository issueResponseRepository;

  private final RiskService riskService;

  private final WorkpackService workpackService;

  private final FindAllIssueUsingCustomFilter findAllIssue;

  private final CustomFilterService customFilterService;

  private final JournalCreator journalCreator;

  public IssueService(
    final IssueRepository repository,
    final IssueResponseRepository issueResponseRepository,
    final RiskService riskService,
    final WorkpackService workpackService,
    final FindAllIssueUsingCustomFilter findAllIssue,
    final CustomFilterService customFilterService,
    final JournalCreator journalCreator
  ) {
    this.repository = repository;
    this.issueResponseRepository = issueResponseRepository;
    this.riskService = riskService;
    this.workpackService = workpackService;
    this.findAllIssue = findAllIssue;
    this.customFilterService = customFilterService;
    this.journalCreator = journalCreator;
  }

  public List<IssueCardDto> findAllAsCardDto(final Long idWorkpack, final Long idRisk, final Long idFilter, final Long idPerson) {
    if(idWorkpack == null) {
      throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
    }
    if(idFilter == null) {
      return this.findAllAsCardDto(idWorkpack, idRisk);
    }

    final CustomFilter customFilter = this.customFilterService.findById(idFilter, idPerson);
    final Map<String, Object> params = new HashMap<>();

    params.put("idWorkpack", idWorkpack);

    final List<Issue> issues = this.findAllIssue.execute(customFilter, params);

    return issues.stream()
      .map(IssueCardDto::of)
      .collect(Collectors.toList());
  }

  private List<IssueCardDto> findAllAsCardDto(final Long idWorkpack, final Long idRisk) {
    return this.repository.findAllAsIssueCardDto(idWorkpack, idRisk).stream()
      .map(IssueCardDto::of)
      .collect(Collectors.toList());
  }

  @Transactional
  public EntityDto createIssueFromRisk(final Long idRisk, final Long idPerson) {
    final Risk risk = this.riskService.findById(idRisk);
    final Issue issue = Issue.of(risk);
    this.repository.save(issue);
    this.issueResponseRepository.saveAll(issue.getResponses());

    this.journalCreator.issue(issue, idPerson);

    return EntityDto.of(issue);
  }

  @Transactional
  public EntityDto create(final IssueCreateDto request, final Long idPerson) {
    final Workpack workpack = this.workpackService.findById(request.getIdWorkpack());
    final Issue issue = Issue.of(request, workpack);
    this.repository.save(issue);

    this.journalCreator.issue(issue, idPerson);

    return EntityDto.of(issue);
  }

  @Transactional
  public IssueDetailDto update(final IssueUpdateDto request, final Long idPerson) {
    final Issue issue = this.repository.findById(request.getId())
      .orElseThrow(() -> new RegistroNaoEncontradoException(ISSUE_NOT_FOUND));

    issue.update(request);
    this.repository.save(issue, 0);

    this.journalCreator.issue(issue, idPerson);

    return IssueDetailDto.of(issue);
  }

  @Transactional
  public void deleteById(final Long issueId) {
    this.deleteAllIssueResponseRelated(issueId);
    this.repository.deleteById(issueId);
  }

  private void deleteAllIssueResponseRelated(final Long issueId) {
    final Collection<IssueResponse> responses = this.issueResponseRepository.findAllByIssueId(issueId);
    this.issueResponseRepository.deleteAll(responses);
  }

  public IssueDetailDto findIssueDetailById(final Long id) {
    return this.repository.findIssueDetailById(id)
      .map(IssueDetailDto::of)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ISSUE_NOT_FOUND));
  }

  public Issue findById(final Long issueId) {
    return this.repository.findById(issueId)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ISSUE_NOT_FOUND));
  }

}
