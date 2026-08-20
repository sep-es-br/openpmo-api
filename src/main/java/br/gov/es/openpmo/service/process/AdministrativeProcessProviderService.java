package br.gov.es.openpmo.service.process;

import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.pmo.administrative_process_core.model.AdministrativeProcessDto;
import br.gov.es.pmo.administrative_process_core.model.IAdministrativeProcessProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AdministrativeProcessProviderService {

    private final ObjectProvider<IAdministrativeProcessProvider> provider;
    private final JournalCreator journalCreator;

    public AdministrativeProcessProviderService(
        final ObjectProvider<IAdministrativeProcessProvider> provider,
        final JournalCreator journalCreator
    ) {
        this.provider = provider;
        this.journalCreator = journalCreator;
    }

    public AdministrativeProcessDto getProcess(
        final String protocol,
        final Long idPerson
    ) {
        try {
            return requiredProvider().getProcess(protocol);
        } catch (final RuntimeException exception) {
            this.journalCreator.failure(idPerson);
            throw exception;
        }
    }

    public List<AdministrativeProcessDto> getProcesses(final List<String> protocols) {
        final IAdministrativeProcessProvider availableProvider = getProvider();
        return availableProvider == null
            ? Collections.emptyList()
            : availableProvider.getProcesses(protocols);
    }

    public boolean isAvailable() {
        return getProvider() != null;
    }

    private IAdministrativeProcessProvider requiredProvider() {
        final IAdministrativeProcessProvider availableProvider = getProvider();
        if (availableProvider == null) {
            throw new IllegalStateException("Plugin de processo administrativo não está disponível");
        }
        return availableProvider;
    }

    private IAdministrativeProcessProvider getProvider() {
        return this.provider.getIfAvailable();
    }
}
