package br.gov.es.openpmo.service.organization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.openpmo.repository.WorkPlaceRepository;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.pmo.organization_parser.pmo_base.model.IWorkLocationParser;
import br.gov.es.pmo.organization_parser.pmo_base.model.WorkLocationDto;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.PublicAgentAssignment;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityType;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

public class WorkPlaceServiceTest {

  private WorkPlaceRepository repository;
  private OrganizationRepository organizationRepository;
  private IPublicIdentityProvider identity;
  private IWorkLocationParser parser;
  private OrganizationTokenService organizationTokenService;
  private TokenService tokenService;
  private WorkPlaceService service;

  @Before
  public void setUp() {
    this.repository = mock(WorkPlaceRepository.class);
    this.organizationRepository = mock(OrganizationRepository.class);
    this.identity = mock(IPublicIdentityProvider.class);
    this.parser = mock(IWorkLocationParser.class);
    this.organizationTokenService = mock(OrganizationTokenService.class);
    this.tokenService = mock(TokenService.class);

    @SuppressWarnings("unchecked")
    final ObjectProvider<IPublicIdentityProvider> identityProvider = mock(ObjectProvider.class);
    @SuppressWarnings("unchecked")
    final ObjectProvider<IWorkLocationParser> parserProvider = mock(ObjectProvider.class);
    when(identityProvider.getIfAvailable()).thenReturn(this.identity);
    when(parserProvider.getIfAvailable()).thenReturn(this.parser);

    this.service = new WorkPlaceService(
      this.repository,
      this.organizationRepository,
      identityProvider,
      parserProvider,
      this.organizationTokenService,
      this.tokenService
    );
  }

  @Test
  public void shouldKeepTheOrganizationAlreadySelectedByTheUser() {
    final Organization selected = organization(10L, "organization-guid");
    when(this.tokenService.getUserId("Bearer token")).thenReturn(1L);
    when(this.repository.findOrganizationByPersonAndOffice(1L, 2L))
      .thenReturn(Optional.of(selected));

    final Optional<Organization> result = this.service.resolveForAuthenticatedUser(
      "Bearer token",
      2L
    );

    assertEquals(selected, result.orElse(null));
    verify(this.identity, never()).findPublicAgentBySub(org.mockito.ArgumentMatchers.anyString());
  }

  @Test
  public void shouldResolveWorkLocationAndSelectTheLocalOrganization() {
    final PublicAgentAssignment assignment = new PublicAgentAssignment(
      "role-guid",
      "Role",
      "TYPE",
      "work-location-guid",
      null
    );
    final PublicIdentityResult identityResult = PublicIdentityResult.found(
      PublicIdentityType.PUBLIC_AGENT,
      null,
      "external-sub",
      "Public Agent",
      null,
      null,
      Collections.singletonList(assignment)
    );
    final Organization organization = organization(20L, "organization-guid");

    when(this.tokenService.getUserId("Bearer token")).thenReturn(1L);
    when(this.tokenService.getExternalIdentityKey("Bearer token")).thenReturn("external-sub");
    when(this.repository.findOrganizationByPersonAndOffice(1L, 2L)).thenReturn(Optional.empty());
    when(this.identity.findPublicAgentBySub("external-sub")).thenReturn(identityResult);
    when(this.organizationTokenService.fetchSystemToken()).thenReturn("organization-token");
    when(this.parser.findByGuid("work-location-guid", "organization-token"))
      .thenReturn(Optional.of(new WorkLocationDto(
        "work-location-guid",
        "Work Location",
        "WL",
        "organization-guid"
      )));
    when(this.organizationRepository.findByGuid("organization-guid"))
      .thenReturn(Optional.of(organization));

    final Optional<Organization> result = this.service.resolveForAuthenticatedUser(
      "Bearer token",
      2L
    );

    assertEquals(organization, result.orElse(null));
    verify(this.repository).replaceOrganization(1L, 2L, 20L);
  }

  @Test
  public void shouldNotSelectAnOrganizationForAnOrdinaryCitizen() {
    when(this.tokenService.getUserId("Bearer token")).thenReturn(1L);
    when(this.tokenService.getExternalIdentityKey("Bearer token")).thenReturn("citizen-sub");
    when(this.repository.findOrganizationByPersonAndOffice(1L, 2L)).thenReturn(Optional.empty());
    when(this.identity.findPublicAgentBySub("citizen-sub"))
      .thenReturn(PublicIdentityResult.notFound(null));

    final Optional<Organization> result = this.service.resolveForAuthenticatedUser(
      "Bearer token",
      2L
    );

    assertFalse(result.isPresent());
    verify(this.parser, never()).findByGuid(
      org.mockito.ArgumentMatchers.anyString(),
      org.mockito.ArgumentMatchers.anyString()
    );
  }

  @Test
  public void shouldFailWhenThePersonHasNoWorkPlaceForTheOffice() {
    final Organization organization = organization(20L, "organization-guid");
    when(this.organizationRepository.findById(20L)).thenReturn(Optional.of(organization));
    when(this.repository.replaceOrganization(1L, 2L, 20L)).thenReturn(0L);

    try {
      this.service.selectOrganization(1L, 2L, 20L);
      fail("Expected the missing contact data to prevent a false successful update.");
    }
    catch(final NegocioException expected) {
      assertEquals("contact-data.not.found", expected.getMessage());
    }
  }

  @Test
  public void shouldCreateTheMissingWorkPlaceBeforeSelectingTheOrganization() {
    final Organization organization = organization(20L, "organization-guid");
    when(this.organizationRepository.findById(20L)).thenReturn(Optional.of(organization));
    when(this.repository.replaceOrganization(1L, 2L, 20L)).thenReturn(1L);

    assertEquals(organization, this.service.selectOrganization(1L, 2L, 20L));

    verify(this.repository).createWorkPlaceWithOrganizationIfMissing(1L, 2L, 20L);
    verify(this.repository).removeDuplicateWorkPlaces(1L, 2L);
    verify(this.repository).replaceOrganization(1L, 2L, 20L);
  }

  private static Organization organization(final Long id, final String guid) {
    final Organization organization = new Organization();
    organization.setId(id);
    organization.setGuid(guid);
    return organization;
  }
}
