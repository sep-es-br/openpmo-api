package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class JournalActionMapper {

  private JournalActionMapper() {
  }

  @Nullable
  public static JournalAction map(final Baseline baseline) {
    switch(baseline.getStatus()) {
      case DRAFT:
        return JournalAction.DRAFT;
      case PROPOSED:
        return JournalAction.PROPOSED;
      case APPROVED:
        return JournalAction.APPROVED;
      case REJECTED:
        return JournalAction.REJECTED;
      default:
        return null;
    }
  }

  @Nullable
  public static JournalAction map(final Issue issue) {
    switch(issue.getStatus()) {
      case OPEN:
        return JournalAction.OPEN;
      case CLOSED:
        return JournalAction.CLOSED;
      default:
        return null;
    }
  }

  @Nullable
  public static JournalAction map(final IssueResponse issueResponse) {
    switch(issueResponse.getStatus()) {
      case WAITING:
        return JournalAction.WAITING;
      case RUNNING:
        return JournalAction.RUNNING;
      case DONE:
        return JournalAction.DONE;
      case CANCELLED:
        return JournalAction.CANCELLED;
      default:
        return null;
    }
  }

  @Nullable
  public static JournalAction map(final Risk risk) {
    switch(risk.getStatus()) {
      case OPEN:
        return JournalAction.OPEN;
      case NOT_GONNA_HAPPEN:
        return JournalAction.NOT_GONNA_HAPPEN;
      case HAPPENED:
        return JournalAction.HAPPENED;
      default:
        return null;
    }
  }

  @Nullable
  public static JournalAction map(final RiskResponse riskResponse) {
    switch(riskResponse.getStatus()) {
      case WAITING_TRIGGER:
        return JournalAction.WAITING_TRIGGER;
      case RUNNING:
        return JournalAction.RUNNING;
      case DONE:
        return JournalAction.DONE;
      case CANCELLED:
        return JournalAction.CANCELLED;
      default:
        return null;
    }
  }

}
