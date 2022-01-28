package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

class RiskStatusProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
    return Stream.of(
        Arguments.of(StatusOfRisk.OPEN, JournalAction.OPEN),
        Arguments.of(StatusOfRisk.NOT_GONNA_HAPPEN, JournalAction.NOT_GONNA_HAPPEN),
        Arguments.of(StatusOfRisk.HAPPENED, JournalAction.HAPPENED)
    );
  }

}
