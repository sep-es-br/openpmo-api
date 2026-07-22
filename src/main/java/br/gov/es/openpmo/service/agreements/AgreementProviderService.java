package br.gov.es.openpmo.service.agreements;

import br.gov.es.pmo.agreement_core.model.AgreementDto;
import br.gov.es.pmo.agreement_core.model.AgreementOrganizationDto;
import br.gov.es.pmo.agreement_core.model.AgreementType;
import br.gov.es.pmo.agreement_core.model.IAgreementProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AgreementProviderService {

    private final ObjectProvider<IAgreementProvider> agreementProvider;

    public AgreementProviderService(
        final ObjectProvider<IAgreementProvider> agreementProvider
    ) {
        this.agreementProvider = agreementProvider;
    }

    public List<Long> getYears(final AgreementType type) {
        final IAgreementProvider provider = this.getProvider();
        return provider == null
            ? Collections.emptyList()
            : provider.getYears(type);
    }

    public List<AgreementOrganizationDto> getOrganizations(
        final AgreementType type,
        final Long year
    ) {
        final IAgreementProvider provider = this.getProvider();
        return provider == null
            ? Collections.emptyList()
            : provider.getOrganizations(type, year);
    }

    public List<AgreementDto> getAgreements(
        final AgreementType type,
        final Long year,
        final String organizationIdentifier,
        final String organizationName
    ) {
        final IAgreementProvider provider = this.getProvider();

        if (provider == null) {
            return Collections.emptyList();
        }

        final AgreementOrganizationDto organization =
            new AgreementOrganizationDto(
                organizationIdentifier,
                organizationName
            );

        return provider.getAgreements(
            type,
            year,
            organization
        );
    }

    public AgreementDto getAgreement(
        final AgreementType type,
        final Long processId
    ) {
        final IAgreementProvider provider = this.getProvider();
        return provider == null
            ? null
            : provider.getAgreement(type, processId);
    }

    private IAgreementProvider getProvider() {
        return this.agreementProvider.getIfAvailable();
    }
}
