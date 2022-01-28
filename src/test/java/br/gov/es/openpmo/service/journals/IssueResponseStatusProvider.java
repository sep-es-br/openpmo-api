package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import br.gov.es.openpmo.model.journals.JournalAction;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

class IssueResponseStatusProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
    return Stream.of(
        Arguments.of(IssueResponseStatus.WAITING, JournalAction.WAITING),
        Arguments.of(IssueResponseStatus.RUNNING, JournalAction.RUNNING),
        Arguments.of(IssueResponseStatus.CANCELLED, JournalAction.CANCELLED),
        Arguments.of(IssueResponseStatus.DONE, JournalAction.DONE)
    );
  }

}
