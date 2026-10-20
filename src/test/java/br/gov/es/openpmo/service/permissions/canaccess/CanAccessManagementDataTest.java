package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.exception.CannotAccessResourceException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CanAccessManagementDataTest {

  private static final Long OFFICE_ID = 2L;
  private static final String AUTHORIZATION = "Bearer token";
  private static final String TARGET_KEY = "target-key";
  private static final String CURRENT_USER_KEY = "current-user-key";

  private ICanAccessData canAccessData;
  private ICanAccessDataResponse accessResponse;
  private CanAccessManagementData canAccessManagementData;

  @Before
  public void setUp() {
    this.canAccessData = mock(ICanAccessData.class);
    this.accessResponse = mock(ICanAccessDataResponse.class);
    this.canAccessManagementData = new CanAccessManagementData(this.canAccessData);
    when(this.canAccessData.execute(OFFICE_ID, AUTHORIZATION)).thenReturn(this.accessResponse);
    when(this.accessResponse.getKey()).thenReturn(CURRENT_USER_KEY);
    when(this.accessResponse.getAdmin()).thenReturn(false);
    when(this.accessResponse.getManagementEdit()).thenReturn(false);
  }

  @Test
  public void shouldAllowReadingManagementDataWithInheritedReadAccess() {
    when(this.accessResponse.canReadResource()).thenReturn(true);

    this.canAccessManagementData.ensureCanReadManagementResource(OFFICE_ID, TARGET_KEY, AUTHORIZATION);
  }

  @Test
  public void shouldRejectReadingManagementDataWithoutReadOrEditAccess() {
    when(this.accessResponse.canReadResource()).thenReturn(false);

    try {
      this.canAccessManagementData.ensureCanReadManagementResource(OFFICE_ID, TARGET_KEY, AUTHORIZATION);
      fail("Expected office management read to be denied");
    } catch (final CannotAccessResourceException expected) {
      // Expected: no inherited read, self, administrator, or management edit access.
    }
  }

  @Test
  public void shouldNotAllowManagementChangesWithReadOnlyAccess() {
    when(this.accessResponse.canReadResource()).thenReturn(true);

    try {
      this.canAccessManagementData.ensureCanAccessManagementResource(OFFICE_ID, AUTHORIZATION);
      fail("Expected management changes to remain denied for read-only access");
    } catch (final CannotAccessResourceException expected) {
      // Expected: read access does not grant management edit access.
    }
  }
}
