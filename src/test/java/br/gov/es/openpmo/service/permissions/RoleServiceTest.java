package br.gov.es.openpmo.service.permissions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.apis.acessocidadao.IAcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.PersonService;
import java.util.Collections;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;

public class RoleServiceTest {

  @Test
  public void keepsOfficeAndPlanPermissionsWhenRemoteRoleHasGuidButLocalOnesDoNot() {
    final IAcessoCidadaoApi api = mock(IAcessoCidadaoApi.class);
    final PersonService people = mock(PersonService.class);
    final OfficePermissionRepository offices = mock(OfficePermissionRepository.class);
    final PlanPermissionRepository plans = mock(PlanPermissionRepository.class);
    final WorkpackPermissionRepository workpacks = mock(WorkpackPermissionRepository.class);
    final IsAuthenticatedByService authentication = mock(IsAuthenticatedByService.class);
    final Person person = new Person();
    person.setId(7L);
    final String role = "Analista 3o CGTI-SEP";

    when(authentication.isCitizenServerAuthentication()).thenReturn(true);
    when(api.findRoles("key", 7L)).thenReturn(Collections.singletonList(
      new PublicAgentRoleResponse("remote-guid", role, null, null, null)));
    when(people.findByKey("key")).thenReturn(Optional.of(person));
    when(offices.findAllPermissionsOfPerson(7L)).thenReturn(Collections.singleton(
      new CanAccessOffice(null, "", role, PermissionLevelEnum.EDIT, person, null)));
    when(plans.findAllPermissionsOfPerson(7L)).thenReturn(Collections.singleton(
      new CanAccessPlan(null, "", role, PermissionLevelEnum.EDIT, person, null)));
    when(workpacks.findAllPermissionsOfPerson(7L)).thenReturn(Collections.emptySet());

    final RoleService service = new RoleService(api, people, offices, plans, workpacks, authentication);
    service.getRolesByKey(7L, "key");

    verify(offices).deleteAll(Collections.emptySet());
    verify(plans).deleteAll(Collections.emptySet());
  }

  @Test
  public void comparesGuidsWhenBothRolesHaveThem() {
    final RoleResource first = new RoleResource("Old name", null, "same-guid");
    final RoleResource same = new RoleResource("New name", null, "same-guid");
    final RoleResource different = new RoleResource("Old name", null, "different-guid");

    assertTrue(HasRole.sameRole(first, same));
    assertFalse(HasRole.sameRole(first, different));
  }

  @Test
  public void fillsPlanAndOfficeRoleGuidsWhenRequestOnlyHasRoleName() {
    final IAcessoCidadaoApi api = mock(IAcessoCidadaoApi.class);
    final IsAuthenticatedByService authentication = mock(IsAuthenticatedByService.class);
    when(authentication.isCitizenServerAuthentication()).thenReturn(true);
    when(api.findRoles("key", 7L)).thenReturn(Collections.singletonList(
      new PublicAgentRoleResponse("remote-guid", "Analista", null, null, null)));

    final PermissionDto plan = new PermissionDto(1L, "Analista", PermissionLevelEnum.EDIT);
    final PermissionDto office = new PermissionDto(2L, "Analista", PermissionLevelEnum.READ);
    final RoleService service = new RoleService(api, mock(PersonService.class),
      mock(OfficePermissionRepository.class), mock(PlanPermissionRepository.class),
      mock(WorkpackPermissionRepository.class), authentication);

    service.fillMissingRoleGuids(7L, "key", Arrays.asList(plan, office), Collections.emptyList());

    assertEquals("remote-guid", plan.getRoleGuid());
    assertEquals("remote-guid", office.getRoleGuid());
  }

  @Test
  public void keepsExistingGuidWithoutRemoteLookup() {
    final IAcessoCidadaoApi api = mock(IAcessoCidadaoApi.class);
    final IsAuthenticatedByService authentication = mock(IsAuthenticatedByService.class);
    when(authentication.isCitizenServerAuthentication()).thenReturn(true);
    final CanAccessPlan existing = new CanAccessPlan(3L, "", "Analista", PermissionLevelEnum.EDIT, null, null);
    existing.setRoleGuid("saved-guid");
    final PermissionDto request = new PermissionDto(3L, "Analista", PermissionLevelEnum.READ);
    final RoleService service = new RoleService(api, mock(PersonService.class),
      mock(OfficePermissionRepository.class), mock(PlanPermissionRepository.class),
      mock(WorkpackPermissionRepository.class), authentication);

    service.fillMissingRoleGuids(7L, "key", Collections.singletonList(request), Collections.singletonList(existing));

    assertEquals("saved-guid", request.getRoleGuid());
    verify(api, org.mockito.Mockito.never()).findRoles("key", 7L);
  }

  @Test
  public void remoteLookupFailureDoesNotPreventPermissionUpdate() {
    final IAcessoCidadaoApi api = mock(IAcessoCidadaoApi.class);
    final IsAuthenticatedByService authentication = mock(IsAuthenticatedByService.class);
    when(authentication.isCitizenServerAuthentication()).thenReturn(true);
    doThrow(new IllegalStateException("identity unavailable")).when(api).findRoles("key", 7L);
    final PermissionDto request = new PermissionDto(null, "Analista", PermissionLevelEnum.EDIT);
    final RoleService service = new RoleService(api, mock(PersonService.class),
      mock(OfficePermissionRepository.class), mock(PlanPermissionRepository.class),
      mock(WorkpackPermissionRepository.class), authentication);

    service.fillMissingRoleGuids(7L, "key", Collections.singletonList(request), Collections.emptyList());

    assertEquals(null, request.getRoleGuid());
  }
}
