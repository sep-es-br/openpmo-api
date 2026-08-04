package br.gov.es.openpmo.service.publicidentity;

import br.gov.es.openpmo.dto.publicidentity.PublicIdentitySearchType;
import br.gov.es.openpmo.dto.publicidentity.PublicIdentityValidationDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityStatus;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class PublicIdentityValidationService {

  private static final String CITIZEN_NOT_REGISTERED =
    "O usuário informado não possui Acesso Cidadão.";
  private static final String VALIDATION_UNAVAILABLE =
    "Não foi possível validar o usuário no Acesso Cidadão.";
  private static final String IDENTITY_MISMATCH =
    "A identidade informada não corresponde ao usuário pesquisado.";

  private final ObjectProvider<IPublicIdentityProvider> provider;
  private final IsAuthenticatedByService authenticatedByService;

  public PublicIdentityValidationService(
    final ObjectProvider<IPublicIdentityProvider> provider,
    final IsAuthenticatedByService authenticatedByService
  ) {
    this.provider = provider;
    this.authenticatedByService = authenticatedByService;
  }

  public void validate(
    final PublicIdentityValidationDto validation,
    final String expectedSub
  ) {
    if(!this.authenticatedByService.isCitizenServerAuthentication()) {
      return;
    }
    if(validation == null || validation.getSearchType() == null) {
      throw new NegocioException(CITIZEN_NOT_REGISTERED);
    }

    final IPublicIdentityProvider identityProvider = this.provider.getIfAvailable();
    if(identityProvider == null) {
      throw new NegocioException(VALIDATION_UNAVAILABLE);
    }

    final PublicIdentityResult result;
    if(PublicIdentitySearchType.CPF.equals(validation.getSearchType())) {
      result = identityProvider.findByCpf(validation.getCpf());
    }
    else {
      result = identityProvider.findPublicAgentBySub(validation.getSub());
      if(result.isFound() && !PublicIdentityType.PUBLIC_AGENT.equals(result.getType())) {
        throw new NegocioException(IDENTITY_MISMATCH);
      }
    }

    this.ensureFound(result);
    if(!same(result.getSub(), validation.getSub()) || !same(result.getSub(), expectedSub)) {
      throw new NegocioException(IDENTITY_MISMATCH);
    }
  }

  private void ensureFound(final PublicIdentityResult result) {
    if(PublicIdentityStatus.NOT_FOUND.equals(result.getStatus())) {
      throw new NegocioException(CITIZEN_NOT_REGISTERED);
    }
    if(!PublicIdentityStatus.FOUND.equals(result.getStatus())) {
      throw new NegocioException(VALIDATION_UNAVAILABLE);
    }
  }

  private static boolean same(final String first, final String second) {
    return first != null && first.equals(second);
  }
}
