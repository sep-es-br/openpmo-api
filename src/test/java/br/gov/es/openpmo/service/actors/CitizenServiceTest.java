package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.person.CitizenDto;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.service.organization.WorkPlaceService;
import br.gov.es.pmo.user_a_identify.model.IPublicIdentityProvider;
import br.gov.es.pmo.user_a_identify.model.PublicAgentAssignment;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityResult;
import br.gov.es.pmo.user_a_identify.model.PublicIdentityType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CitizenServiceTest {

  private PersonService personService;
  private IPublicIdentityProvider identityProvider;
  private WorkPlaceService workPlaceService;
  private CitizenService service;

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() {
    this.personService = mock(PersonService.class);
    final IsInContactBookOfService contactService = mock(IsInContactBookOfService.class);
    final ObjectProvider<IPublicIdentityProvider> provider = mock(ObjectProvider.class);
    this.identityProvider = mock(IPublicIdentityProvider.class);
    this.workPlaceService = mock(WorkPlaceService.class);
    when(provider.getIfAvailable()).thenReturn(this.identityProvider);
    this.service = new CitizenService(
      this.personService,
      contactService,
      provider,
      this.workPlaceService
    );
  }

  @Test
  public void shouldReturnTheOrganizationResolvedFromThePublicAgentWorkLocation() {
    final PublicAgentAssignment assignment = new PublicAgentAssignment(
      "role-guid",
      "Role",
      "TYPE",
      "work-location-guid",
      null
    );
    final PublicIdentityResult identity = PublicIdentityResult.found(
      PublicIdentityType.PUBLIC_AGENT,
      "12345678900",
      "external-sub",
      "Public Agent",
      "agent@example.com",
      null,
      Collections.singletonList(assignment)
    );
    final Organization organization = new Organization();
    organization.setId(20L);
    organization.setName("Organization Name");
    organization.setGuid("organization-guid");

    when(this.identityProvider.findByCpf("12345678900")).thenReturn(identity);
    when(this.personService.findByKey("external-sub")).thenReturn(Optional.empty());
    when(this.workPlaceService.resolveOrganizationByWorkLocationGuid("work-location-guid"))
      .thenReturn(Optional.of(organization));

    final CitizenDto result = this.service.findPersonByCpf("12345678900", 1L, 2L);

    assertNotNull(result.getOrganization());
    assertEquals(Long.valueOf(20L), result.getOrganization().getId());
    assertEquals("Organization Name", result.getOrganization().getName());
    assertEquals("organization-guid", result.getOrganization().getGuid());
    assertEquals("Organization Name", result.getRoles().get(1).getWorkLocation());
  }
}
