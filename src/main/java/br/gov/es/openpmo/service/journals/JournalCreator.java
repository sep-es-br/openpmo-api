package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.journals.JournalRequest;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IssueRepository;
import br.gov.es.openpmo.repository.IssueResponseRepository;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.RiskResponseRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

  public void baseline(
    final Baseline baseline,
    final Long personId
  ) {
    final Long workpackId = this.getWorkpackIdByBaseline(baseline);
    final JournalAction journalAction = JournalActionMapper.mapBaselineStatus(baseline.getStatus());

    this.createBaselineJournalEntry(
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
      .orElseGet(() -> this.findWorkpackIdByBaselineId(baseline));
  }

  public JournalEntry create(
    final JournalType type,
    final JournalAction action,
    final String nameItem,
    final String description,
    final Long workpackId,
    final Long personId
  ) {
    return this.create(
      type,
      action,
      nameItem,
      description,
      null,
      null,
      null,
      workpackId,
      personId
    );
  }

  public JournalEntry createBaselineJournalEntry(
          final JournalType type,
          final JournalAction action,
          final String nameItem,
          final String description,
          final Long workpackId,
          final Long personId
  ) {
    return this.journalRepository.createJournalEntryBaselineEvaluate(
            type.name(), action.name(), nameItem, description, workpackId, personId, LocalDateTime.now());
  }

  private Long findWorkpackIdByBaselineId(final Baseline baseline) {
    return this.baselineRepository.findWorkpackIdByBaselineId(baseline.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  public JournalEntry create(
    final JournalType type,
    final JournalAction action,
    final String nameItem,
    final String description,
    final String reason,
    final LocalDate newDate,
    final LocalDate previousDate,
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
      reason,
      newDate,
      previousDate,
      workpack,
      person
    );
  }

  private Workpack findWorkpackById(final Long workpackId) {
    return this.workpackRepository.findById(workpackId)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
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
    final String reason,
    final LocalDate newDate,
    final LocalDate previousDate,
    final Workpack workpack,
    final Person person
  ) {
    final JournalEntry journalEntry = new JournalEntry(
      type,
      action,
      nameItem,
      description,
      reason,
      newDate,
      previousDate,
      workpack,
      person
    );

    return this.saveJournal(journalEntry);
  }

  private JournalEntry saveJournal(final JournalEntry journalEntry) {
    return this.journalRepository.save(
      journalEntry,
      1
    );
  }

  private JournalEntry saveJournalWorkpackPermission(final JournalEntry journalEntry) {
    return this.journalRepository.createJournalEntryWorkpackPermission(
            journalEntry.getType().name(),
            journalEntry.getAction().name(),
            journalEntry.getLevel().name(),
            journalEntry.getNameItem(),
            journalEntry.getDescription(),
            journalEntry.getWorkpackId(),
            journalEntry.getAuthor().getId(),
            journalEntry.getTarget().getId(),
            journalEntry.getDate()
    );
  }

  public void officePermission(
    final Office office,
    final Person target,
    final Person author,
    final PermissionLevelEnum level,
    final JournalAction journalAction
  ) {
    this.createJournalOfficePermission(
      JournalType.OFFICE_PERMISSION,
      journalAction,
      level,
      office.getName(),
      office.getFullName(),
      office,
      target,
      author
    );
  }

  public JournalEntry createJournalOfficePermission(
          final JournalType type,
          final JournalAction action,
          final PermissionLevelEnum level,
          final String nameItem,
          final String description,
          final Office office,
          final Person target,
          final Person author
  ) {
    final JournalEntry journalEntry = JournalEntry.of(
            type,
            action,
            level,
            nameItem,
            description,
            office,
            target,
            author
    );
    return this.saveJournalOfficePermission(journalEntry);
  }

  private JournalEntry saveJournalOfficePermission(final JournalEntry journalEntry) {
    return this.journalRepository.createJournalEntryOfficePermission(
            journalEntry.getType().name(),
            journalEntry.getAction().name(),
            journalEntry.getLevel().name(),
            journalEntry.getNameItem(),
            journalEntry.getDescription(),
            journalEntry.getOffice().getId(),
            journalEntry.getAuthor().getId(),
            journalEntry.getTarget().getId(),
            journalEntry.getDate()
    );
  }

  public JournalEntry create(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Office office,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = JournalEntry.of(
      type,
      action,
      level,
      nameItem,
      description,
      office,
      target,
      author
    );
    return this.saveJournal(journalEntry);
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

  public void issue(
    final Issue issue,
    final Long personId
  ) {
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

  public void issueResponse(
    final IssueResponse issueResponse,
    final Long personId
  ) {
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

  public void risk(
    final Risk risk,
    final Long personId
  ) {
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

  public void riskResponse(
    final RiskResponse riskResponse,
    final Long personId
  ) {
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

  public void edition(
    final Workpack workpack,
    final JournalAction journalAction,
    final Long personId
  ) {
    this.create(
      JournalType.EDITION,
      journalAction,
      null,
      null,
      workpack.getId(),
      personId
    );
  }

  public void dateChanged(
    final Workpack workpack,
    final JournalAction journalAction,
    final String reason,
    final LocalDate newDate,
    final LocalDate previousDate,
    final Long personId
  ) {
    this.create(
      JournalType.DATE_CHANGED,
      journalAction,
      null,
      null,
      reason,
      newDate,
      previousDate,
      workpack.getId(),
      personId
    );
  }

  public EntityDto newInformation(
    final JournalRequest journalRequest,
    final Long personId
  ) {
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

  public void planPermission(
    final Plan plan,
    final Person target,
    final Person author,
    final PermissionLevelEnum level,
    final JournalAction journalAction
  ) {
    this.createJournalPlanPermission(
      JournalType.PLAN_PERMISSION,
      journalAction,
      level,
      plan.getName(),
      plan.getFullName(),
      plan,
      target,
      author
    );
  }

  public JournalEntry createJournalPlanPermission(
          final JournalType type,
          final JournalAction action,
          final PermissionLevelEnum level,
          final String nameItem,
          final String description,
          final Plan plan,
          final Person target,
          final Person author
  ) {
    final JournalEntry journalEntry = JournalEntry.of(
            type,
            action,
            level,
            nameItem,
            description,
            plan,
            target,
            author
    );
    return this.saveJournalPlanPermission(journalEntry);
  }

  private JournalEntry saveJournalPlanPermission(final JournalEntry journalEntry) {
    return this.journalRepository.createJournalEntryPlanPermission(
            journalEntry.getType().name(),
            journalEntry.getAction().name(),
            journalEntry.getLevel().name(),
            journalEntry.getNameItem(),
            journalEntry.getDescription(),
            journalEntry.getPlan().getId(),
            journalEntry.getAuthor().getId(),
            journalEntry.getTarget().getId(),
            journalEntry.getDate()
    );
  }


  private JournalEntry create(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Plan plan,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = JournalEntry.of(
      type,
      action,
      level,
      nameItem,
      description,
      plan,
      target,
      author
    );
    return this.saveJournal(journalEntry);
  }

  public void workpackPermission(
    final Workpack workpack,
    final Person target,
    final Person author,
    final PermissionLevelEnum level,
    final JournalAction journalAction
  ) {
    this.createJournalWorkpackPermission(
      JournalType.WORKPACK_PERMISSION,
      journalAction,
      level,
      workpack.getName(),
      workpack.getReason(),
      workpack,
      target,
      author
    );
  }

  private JournalEntry createJournalWorkpackPermission(
    final JournalType type,
    final JournalAction action,
    final PermissionLevelEnum level,
    final String nameItem,
    final String description,
    final Workpack workpack,
    final Person target,
    final Person author
  ) {
    final JournalEntry journalEntry = JournalEntry.of(
      type,
      action,
      level,
      nameItem,
      description,
      workpack,
      target,
      author
    );
    return this.saveJournalWorkpackPermission(journalEntry);
  }

  public JournalEntry create(
    final JournalType type,
    final JournalAction action,
    final String nameItem,
    final String description,
    final Workpack workpack,
    final Person person
  ) {
    return this.create(
      type,
      action,
      nameItem,
      description,
      null,
      null,
      null,
      workpack,
      person
    );
  }

}
