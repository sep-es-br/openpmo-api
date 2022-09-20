package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import br.gov.es.openpmo.model.risk.response.RiskResponseStatus;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Service;

@Service
public class JournalActionMapper {

  private JournalActionMapper() {
  }

  public static JournalAction mapBaselineStatus(final Status status) {
    if(status == Status.DRAFT) {
      return JournalAction.DRAFT;
    }
    if(status == Status.PROPOSED) {
      return JournalAction.PROPOSED;
    }
    if(status == Status.APPROVED) {
      return JournalAction.APPROVED;
    }
    if(status == Status.REJECTED) {
      return JournalAction.REJECTED;
    }
    throw new NegocioException(ApplicationMessage.BASELINE_UNDEFINED_STATUS);
  }

  public static JournalAction mapIssueStatus(final StatusOfIssue status) {
    if(status == StatusOfIssue.OPEN) {
      return JournalAction.OPEN;
    }
    if(status == StatusOfIssue.CLOSED) {
      return JournalAction.CLOSED;
    }
    throw new NegocioException(ApplicationMessage.ISSUE_UNDEFINED_STATUS);
  }

  public static JournalAction mapIssueResponseStatus(final IssueResponseStatus status) {
    if(status == IssueResponseStatus.WAITING) {
      return JournalAction.WAITING;
    }
    if(status == IssueResponseStatus.RUNNING) {
      return JournalAction.RUNNING;
    }
    if(status == IssueResponseStatus.DONE) {
      return JournalAction.DONE;
    }
    if(status == IssueResponseStatus.CANCELLED) {
      return JournalAction.CANCELLED;
    }
    throw new NegocioException(ApplicationMessage.ISSUE_RESPONSE_UNDEFINED_STATUS);
  }

  public static JournalAction mapRiskStatus(final StatusOfRisk status) {
    if(status == StatusOfRisk.OPEN) {
      return JournalAction.OPEN;
    }
    if(status == StatusOfRisk.NOT_GONNA_HAPPEN) {
      return JournalAction.NOT_GONNA_HAPPEN;
    }
    if(status == StatusOfRisk.HAPPENED) {
      return JournalAction.HAPPENED;
    }
    throw new NegocioException(ApplicationMessage.RISK_UNDEFINED_STATUS);
  }

  public static JournalAction mapRiskResponseStatus(final RiskResponseStatus status) {
    if(status == RiskResponseStatus.WAITING_TRIGGER) {
      return JournalAction.WAITING_TRIGGER;
    }
    if(status == RiskResponseStatus.RUNNING) {
      return JournalAction.RUNNING;
    }
    if(status == RiskResponseStatus.DONE) {
      return JournalAction.DONE;
    }
    if(status == RiskResponseStatus.CANCELLED) {
      return JournalAction.CANCELLED;
    }
    throw new NegocioException(ApplicationMessage.RISK_RESPONSE_UNDEFINED_STATUS);
  }

}
