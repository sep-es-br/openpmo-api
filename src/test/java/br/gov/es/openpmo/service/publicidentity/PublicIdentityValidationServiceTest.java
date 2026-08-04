package br.gov.es.openpmo.service.publicidentity;

import br.gov.es.openpmo.dto.publicidentity.PublicIdentitySearchType;
import br.gov.es.openpmo.dto.publicidentity.PublicIdentityValidationDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PublicIdentityValidationServiceTest {

  private IPublicIdentityProvider provider;
  private PublicIdentityValidationService service;

  @Before
  public void setUp() {
    this.provider = mock(IPublicIdentityProvider.class);
    final ObjectProvider<IPublicIdentityProvider> objectProvider = mock(ObjectProvider.class);
    final IsAuthenticatedByService authenticatedByService = mock(IsAuthenticatedByService.class);
    when(objectProvider.getIfAvailable()).thenReturn(this.provider);
    when(authenticatedByService.isCitizenServerAuthentication()).thenReturn(true);
    this.service = new PublicIdentityValidationService(objectProvider, authenticatedByService);
  }

  @Test
  public void shouldAcceptMatchingCpfAndSub() {
    when(this.provider.findByCpf("123.456.789-00")).thenReturn(foundCitizen("person-sub"));

    this.service.validate(cpfValidation("123.456.789-00", "person-sub"), "person-sub");
  }

  @Test(expected = NegocioException.class)
  public void shouldRejectCpfThatResolvesToAnotherSub() {
    when(this.provider.findByCpf("123.456.789-00")).thenReturn(foundCitizen("other-sub"));

    this.service.validate(cpfValidation("123.456.789-00", "person-sub"), "person-sub");
  }

  @Test(expected = NegocioException.class)
  public void shouldRequireValidationForCitizenAuthentication() {
    this.service.validate(null, "person-sub");
  }

  private static PublicIdentityValidationDto cpfValidation(
    final String cpf,
    final String sub
  ) {
    final PublicIdentityValidationDto validation = new PublicIdentityValidationDto();
    validation.setSearchType(PublicIdentitySearchType.CPF);
    validation.setCpf(cpf);
    validation.setSub(sub);
    return validation;
  }

  private static PublicIdentityResult foundCitizen(final String sub) {
    return PublicIdentityResult.found(
      PublicIdentityType.CITIZEN,
      "12345678900",
      sub,
      "Person",
      "person@example.com",
      null,
      Collections.emptyList()
    );
  }
}
