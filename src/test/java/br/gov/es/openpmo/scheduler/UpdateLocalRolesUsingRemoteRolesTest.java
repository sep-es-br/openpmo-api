package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.repository.IsAuthenticatedByRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateLocalRolesUsingRemoteRolesTest {

  private UpdateLocalRolesUsingRemoteRoles updateLocalRolesUsingRemoteRoles;

  @Mock
  private AcessoCidadaoApi acessoCidadaoApi;

  @Mock
  private IsAuthenticatedByRepository isAuthenticatedByRepository;

  @Mock
  private PlanPermissionRepository planPermissionRepository;

  @Mock
  private WorkpackPermissionRepository workpackPermissionRepository;

  @Mock
  private OfficePermissionRepository officePermissionRepository;

  @Mock
  private IsAuthenticatedBy authenticatedBy;

  @Mock
  private Person person;

  @Mock
  private CanAccessWorkpack workpackPermission;

  @Mock
  private IsCCBMemberRepository ccbMemberRepository;

  @BeforeEach
  void setUp() {
    this.updateLocalRolesUsingRemoteRoles = new UpdateLocalRolesUsingRemoteRoles(
      this.acessoCidadaoApi,
      this.isAuthenticatedByRepository,
      this.planPermissionRepository,
      this.workpackPermissionRepository,
      this.officePermissionRepository,
      this.ccbMemberRepository
    );
  }

  @Test
  void shouldDeletePermission() {
    this.givenPermissionRoleInvalid();

    this.updateLocalRolesUsingRemoteRoles.updatePersonRoles();

    verify(this.workpackPermission, times(1)).getRole();
    verify(this.workpackPermissionRepository, times(1)).deleteAll(singletonList(this.workpackPermission));
  }

  private void givenPermissionRoleInvalid() {
    when(this.acessoCidadaoApi.findRoles(anyString(), isNull())).thenReturn(asList(
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 1"),
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 2"),
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 3")
    ));
    when(this.isAuthenticatedByRepository.findAll()).thenReturn(asList(this.authenticatedBy));
    when(this.authenticatedBy.getGuid()).thenReturn("guid");
    when(this.authenticatedBy.getPerson()).thenReturn(this.person);
    when(this.person.getId()).thenReturn(1L);
    when(this.workpackPermission.getRole()).thenReturn("role 4");
    when(this.workpackPermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>(singletonList(this.workpackPermission)));
    when(this.officePermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>());
    when(this.planPermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>());
    doNothing().when(this.workpackPermissionRepository).deleteAll(anyCollection());
  }

  private static PublicAgentRoleResponse createPublicAgentRole(final String role) {
    return new PublicAgentRoleResponse(
      null,
      role,
      null,
      null,
      null
    );
  }

  @Test
  void shouldNotDeletePermissionsIfValid() {
    this.givenPermissionRoleIsValid();

    this.updateLocalRolesUsingRemoteRoles.updatePersonRoles();

    verify(this.workpackPermissionRepository, never()).deleteAll(singletonList(this.workpackPermission));
  }

  private void givenPermissionRoleIsValid() {
    when(this.acessoCidadaoApi.findRoles(anyString(), isNull())).thenReturn(asList(
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 1"),
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 2"),
      UpdateLocalRolesUsingRemoteRolesTest.createPublicAgentRole("role 3")
    ));
    when(this.isAuthenticatedByRepository.findAll()).thenReturn(asList(this.authenticatedBy));
    when(this.authenticatedBy.getGuid()).thenReturn("guid");
    when(this.authenticatedBy.getPerson()).thenReturn(this.person);
    when(this.person.getId()).thenReturn(1L);
    when(this.workpackPermission.getRole()).thenReturn("role 3");
    when(this.workpackPermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>(singletonList(this.workpackPermission)));
    when(this.officePermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>());
    when(this.planPermissionRepository.findAllPermissionsOfPerson(1L)).thenReturn(new HashSet<>());
    doNothing().when(this.workpackPermissionRepository).deleteAll(anyCollection());
  }

}
