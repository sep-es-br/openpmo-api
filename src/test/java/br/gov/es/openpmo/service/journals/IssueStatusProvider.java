package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.journals.JournalAction;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

class IssueStatusProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
    return Stream.of(
        Arguments.of(StatusOfIssue.OPEN, JournalAction.OPEN),
        Arguments.of(StatusOfIssue.CLOSED, JournalAction.CLOSED)
    );
  }

}
