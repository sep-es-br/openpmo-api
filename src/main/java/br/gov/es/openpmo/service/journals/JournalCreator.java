package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.journals.JournalRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.*;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JournalCreator {

  private final WorkpackRepository workpackRepository;

  private final PersonRepository personRepository;

  private final JournalRepository journalRepository;

  private final BaselineRepository baselineRepository;

  private final IssueRepository issueRepository;

  private final IssueResponseRepository issueResponseRepository;

  private final RiskRepository riskRepository;

  private final RiskResponseRepository riskResponseRepository;

  @Autowired
  public JournalCreator(
      final WorkpackRepository workpackRepository,
      final PersonRepository personRepository,
      final JournalRepository journalRepository,
      final BaselineRepository baselineRepository,
      final IssueRepository issueRepository,
      final IssueResponseRepository issueResponseRepository,
      final RiskRepository riskRepository,
      final RiskResponseRepository riskResponseRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.personRepository = personRepository;
    this.journalRepository = journalRepository;
    this.baselineRepository = baselineRepository;
    this.issueRepository = issueRepository;
    this.issueResponseRepository = issueResponseRepository;
    this.riskRepository = riskRepository;
    this.riskResponseRepository = riskResponseRepository;
  }

  public void baseline(final Baseline baseline, final Long personId) {
    final Long workpackId = this.getWorkpackIdByBaseline(baseline);
    final JournalAction journalAction = JournalActionMapper.mapBaselineStatus(baseline.getStatus());

    this.create(
        JournalType.BASELINE,
        journalAction,
        baseline.getName(),
        baseline.getDescription(),
        workpackId,
        personId
    );
  }

  private Long getWorkpackIdByBaseline(final Baseline baseline) {
    return Optional.ofNullable(baseline.getIdWorkpack())
        .orElse(this.findWorkpackIdByBaselineId(baseline));
  }

  private JournalEntry saveJournal(final JournalEntry journalEntry) {
    return this.journalRepository.save(journalEntry, 1);
  }

  private Long findWorkpackIdByBaselineId(final Baseline baseline) {
    return this.baselineRepository.findWorkpackIdByBaselineId(baseline.getId())
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  public void failure(final Long personId) {
    if (personId == null) {
      return;
    }

    final String description = "Failed to load resource: the server responded with a status of 404 (Not Found)";

    this.create(
        JournalType.FAIL,
        null,
        null,
        description,
        null,
        personId
    );
  }

  private Person findPersonById(final Long personId) {
    return this.personRepository.findById(personId)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  public JournalEntry create(
      final JournalType type,
      final JournalAction action,
      final String nameItem,
      final String description,
      final Workpack workpack,
      final Person person
  ) {
    final JournalEntry journalEntry = new JournalEntry(
        type,
        action,
        nameItem,
        description,
        workpack,
        person
    );

    return this.saveJournal(journalEntry);
  }

  public JournalEntry create(
      final JournalType type,
      final JournalAction action,
      final String nameItem,
      final String description,
      final Long workpackId,
      final Long personId
  ) {
    Workpack workpack = null;

    if (type != JournalType.FAIL) {
      workpack = this.findWorkpackById(workpackId);
    }

    final Person person = this.findPersonById(personId);

    return this.create(
        type,
        action,
        nameItem,
        description,
        workpack,
        person
    );
  }

  private Workpack findWorkpackById(final Long workpackId) {
    return this.workpackRepository.findById(workpackId)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  public void issue(final Issue issue, final Long personId) {
    final Long workpackId = this.getWorkpackIdByIssue(issue);
    final JournalAction journalAction = JournalActionMapper.mapIssueStatus(issue.getStatus());

    this.create(
        JournalType.ISSUE,
        journalAction,
        issue.getName(),
        issue.getDescription(),
        workpackId,
        personId
    );
  }

  private Long getWorkpackIdByIssue(final Issue issue) {
    return Optional.ofNullable(issue.getWorkpackId())
        .orElse(this.findWorkpackIdByIssueId(issue));
  }

  private Long findWorkpackIdByIssueId(final Issue issue) {
    return this.issueRepository.findWorkpackIdByIssueId(issue.getId())
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  public void issueResponse(final IssueResponse issueResponse, final Long personId) {
    final Long workpackId = this.getWorkpackIdByIssueResponse(issueResponse);
    final JournalAction journalAction = JournalActionMapper.mapIssueResponseStatus(issueResponse.getStatus());

    this.create(
        JournalType.ISSUE_RESPONSE,
        journalAction,
        issueResponse.getName(),
        issueResponse.getPlan(),
        workpackId,
        personId
    );
  }

  private Long getWorkpackIdByIssueResponse(final IssueResponse issueResponse) {
    return Optional.ofNullable(issueResponse.getWorkpackId())
        .orElse(this.findWorkpackIdByIssueResponseId(issueResponse));
  }

  private Long findWorkpackIdByIssueResponseId(final IssueResponse issueResponse) {
    return this.issueResponseRepository.findIssueByIssueResponseId(issueResponse.getId())
        .map(this::findWorkpackIdByIssueId)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.ISSUE_NOT_FOUND));
  }

  public void risk(final Risk risk, final Long personId) {
    final Long workpackId = this.getWorkpackIdByRisk(risk);
    final JournalAction journalAction = JournalActionMapper.mapRiskStatus(risk.getStatus());

    this.create(
        JournalType.RISK,
        journalAction,
        risk.getName(),
        risk.getDescription(),
        workpackId,
        personId
    );
  }

  private Long getWorkpackIdByRisk(final Risk risk) {
    return Optional.ofNullable(risk.getIdWorkpack())
        .orElse(this.findWorkpackIdByRiskId(risk));
  }

  private Long findWorkpackIdByRiskId(final Risk risk) {
    return this.riskRepository.findWorkpackIdByRiskId(risk.getId())
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  public void riskResponse(final RiskResponse riskResponse, final Long personId) {
    final Long workpackId = this.getWorkpackIdByRiskResponse(riskResponse);
    final JournalAction journalAction = JournalActionMapper.mapRiskResponseStatus(riskResponse.getStatus());

    this.create(
        JournalType.RISK_RESPONSE,
        journalAction,
        riskResponse.getName(),
        riskResponse.getPlan(),
        workpackId,
        personId
    );
  }

  private Long getWorkpackIdByRiskResponse(final RiskResponse riskResponse) {
    return Optional.ofNullable(riskResponse.getIdWorkpack())
        .orElse(this.findWorkpackIdByRiskResponseId(riskResponse));
  }

  private Long findWorkpackIdByRiskResponseId(final RiskResponse riskResponse) {
    return this.riskResponseRepository.findRiskByRiskResponseId(riskResponse.getId())
        .map(this::findWorkpackIdByRiskId)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.RISK_NOT_FOUND));
  }

  public void edition(final Workpack workpack, final JournalAction journalAction, final Long personId) {
    this.create(
        JournalType.EDITION,
        journalAction,
        null,
        null,
        workpack.getId(),
        personId
    );
  }

  public EntityDto newInformation(final JournalRequest journalRequest, final Long personId) {
    final JournalEntry journalEntry = this.create(
        JournalType.INFORMATION,
        null,
        null,
        journalRequest.getDescription(),
        journalRequest.getWorkpackId(),
        personId
    );

    return new EntityDto(journalEntry.getId());
  }

}
