package br.gov.es.openpmo.service;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization.PersonDataResponse;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessDataResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CanAccessDataTransversalStakeholderTest {

  private static final Long WORKPACK_ID = 250L;
  private static final String AUTHORIZATION = "Bearer token";
  private static final String USER_KEY = "user-key";

  private IGetPersonFromAuthorization getPersonFromAuthorization;
  private PermissionRepository permissionRepository;
  private PersonDataResponse personData;
  private CanAccessData canAccessData;

  @Before
  public void setUp() {
    this.getPersonFromAuthorization = mock(IGetPersonFromAuthorization.class);
    this.permissionRepository = mock(PermissionRepository.class);
    this.personData = mock(PersonDataResponse.class);
    final Person person = mock(Person.class);
    when(this.getPersonFromAuthorization.execute(AUTHORIZATION)).thenReturn(this.personData);
    when(this.personData.getKey()).thenReturn(USER_KEY);
    when(this.personData.getPerson()).thenReturn(person);
    when(person.getAdministrator()).thenReturn(false);
    this.canAccessData = new CanAccessData(this.getPersonFromAuthorization, this.permissionRepository);
  }

  @Test
  public void shouldGrantReadOnlyAccessToWorkpackInStakeholderTransversalScope() {
    when(this.permissionRepository.hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY)).thenReturn(true);

    final CanAccessDataResponse response = (CanAccessDataResponse) this.canAccessData
      .executeWorkpack(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    assertFalse(response.canEditResource());
    assertFalse(response.canUpdateResource());
  }

  @Test
  public void shouldApplyTransversalStakeholderReadToSingleResourceChecks() {
    when(this.permissionRepository.hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY)).thenReturn(true);

    final CanAccessDataResponse response = this.canAccessData.execute(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    assertFalse(response.canEditResource());
  }

  @Test
  public void shouldGrantReadOnlyAccessToTransversalViewModel() {
    when(this.permissionRepository.hasTransversalStakeholderViewReadAccess(WORKPACK_ID, USER_KEY))
      .thenReturn(true);

    final CanAccessDataResponse response = this.canAccessData.execute(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    assertFalse(response.canEditResource());
    assertFalse(response.canUpdateResource());
  }

  @Test
  public void shouldGrantReadOnlyAccessToModelsUsedByStakeholderTransversalView() {
    when(this.permissionRepository.hasTransversalStakeholderModelReadAccess(WORKPACK_ID, USER_KEY))
      .thenReturn(true);

    final CanAccessDataResponse response = this.canAccessData.execute(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    assertFalse(response.canEditResource());
    assertFalse(response.canUpdateResource());
  }

  @Test
  public void shouldNotGrantReadWhenWorkpackIsOutsideStakeholderTransversalScope() {
    when(this.permissionRepository.hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY)).thenReturn(false);
    when(this.permissionRepository.hasBasicReadPermission(java.util.Collections.singletonList(WORKPACK_ID), USER_KEY))
      .thenReturn(false);

    final CanAccessDataResponse response = (CanAccessDataResponse) this.canAccessData
      .executeWorkpack(WORKPACK_ID, AUTHORIZATION);

    assertFalse(response.canReadResource());
    verify(this.permissionRepository, atLeastOnce()).hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY);
  }

  @Test
  public void shouldGrantWorkpackReadWhenBasicReadPermissionIsInherited() {
    when(this.permissionRepository.hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY)).thenReturn(false);
    when(this.permissionRepository.hasBasicReadPermission(java.util.Collections.singletonList(WORKPACK_ID), USER_KEY))
      .thenReturn(true);

    final CanAccessDataResponse response = (CanAccessDataResponse) this.canAccessData
      .executeWorkpack(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    assertFalse(response.canEditResource());
    assertFalse(response.canUpdateResource());
    verify(this.permissionRepository).hasBasicReadPermission(java.util.Collections.singletonList(WORKPACK_ID), USER_KEY);
  }

  @Test
  public void shouldKeepExistingReadPermissionWithoutCheckingTransversalScope() {
    when(this.permissionRepository.hasPermisionOfficeOrPlan(WORKPACK_ID, USER_KEY)).thenReturn(true);

    final CanAccessDataResponse response = (CanAccessDataResponse) this.canAccessData
      .executeWorkpack(WORKPACK_ID, AUTHORIZATION);

    assertTrue(response.canReadResource());
    verify(this.permissionRepository, never()).hasTransversalStakeholderReadAccess(WORKPACK_ID, USER_KEY);
  }
}
