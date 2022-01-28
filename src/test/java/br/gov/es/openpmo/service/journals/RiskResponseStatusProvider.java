package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.response.RiskResponseStatus;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

class RiskResponseStatusProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
    return Stream.of(
        Arguments.of(RiskResponseStatus.WAITING_TRIGGER, JournalAction.WAITING_TRIGGER),
        Arguments.of(RiskResponseStatus.RUNNING, JournalAction.RUNNING),
        Arguments.of(RiskResponseStatus.CANCELLED, JournalAction.CANCELLED),
        Arguments.of(RiskResponseStatus.DONE, JournalAction.DONE)
    );
  }

}
