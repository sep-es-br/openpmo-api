package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import br.gov.es.openpmo.service.actors.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

  private static final String CITIZEN = "citizen";
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

  private final AcessoCidadaoApi acessoCidadaoApi;

  private final PersonService personService;

  private final OfficePermissionRepository officePermissionRepository;

  private final PlanPermissionRepository planPermissionRepository;

  private final WorkpackPermissionRepository workpackPermissionRepository;

  @Autowired
  public RoleService(
    final AcessoCidadaoApi acessoCidadaoApi,
    final PersonService personService,
    final OfficePermissionRepository officePermissionRepository,
    final PlanPermissionRepository planPermissionRepository,
    final WorkpackPermissionRepository workpackPermissionRepository
  ) {
    this.acessoCidadaoApi = acessoCidadaoApi;
    this.personService = personService;
    this.officePermissionRepository = officePermissionRepository;
    this.planPermissionRepository = planPermissionRepository;
    this.workpackPermissionRepository = workpackPermissionRepository;
  }

  private static int sortByRoleIgnoreCase(
    final HasRole first,
    final HasRole second
  ) {
    return first.getRole().compareToIgnoreCase(second.getRole());
  }

  @Transactional
  public List<RoleResource> getRolesByKey(
    final Long idPerson,
    final String key
  ) {
    return this.getRolesOfPublicAgent(idPerson, key);
  }

  @Transactional
  public List<RoleResource> getRolesBySub(
    final Long idPerson,
    final String sub
  ) {
    return this.getRolesOfPublicAgent(idPerson, sub);
  }

  private List<RoleResource> getRolesOfPublicAgent(
    final Long idPerson,
    final String key
  ) {
    final List<RoleResource> roles = this.getRolesSorted(idPerson, key);
    final Person person = this.getPersonByEmail(key);
    if(person != null) {
      this.removeOldPermissions(roles, person);
    }
    return roles;
  }

  private void removeOldPermissions(
    final Collection<RoleResource> roles,
    final Person person
  ) {
    this.removeOldOfficePermissions(roles, person);
    this.removeOldPlanPermissions(roles, person);
    this.removeOldWorkpackPermissions(roles, person);
  }

  private List<RoleResource> getRolesSorted(
    final Long idPerson,
    final String key
  ) {
    final List<RoleResource> roles = new ArrayList<>();
    final List<PublicAgentRoleResponse> agentRoles = this.getAgentRolesResponseByKey(idPerson, key);

    if(ObjectUtils.isEmpty(agentRoles)) return Collections.emptyList();

    for(final PublicAgentRoleResponse roleResponse : agentRoles) {
      roles.add(this.getRoleResource(roleResponse, idPerson));
    }
    roles.sort(RoleService::sortByRoleIgnoreCase);
    return roles;
  }

  private void removeOldWorkpackPermissions(
    final Collection<? extends RoleResource> roles,
    final Person person
  ) {
    final Set<CanAccessWorkpack> removable = this.findWorkpackPermissions(person).stream()
      .filter(permission -> this.canRemovePermission(permission, roles))
      .collect(Collectors.toSet());
    this.workpackPermissionRepository.deleteAll(removable);
  }

  private void removeOldPlanPermissions(
    final Collection<? extends RoleResource> roles,
    final Person person
  ) {
    final Set<CanAccessPlan> removable = this.findPlanPermissions(person).stream()
      .filter(permission -> this.canRemovePermission(permission, roles))
      .collect(Collectors.toSet());
    this.planPermissionRepository.deleteAll(removable);
  }

  private void removeOldOfficePermissions(
    final Collection<? extends RoleResource> roles,
    final Person person
  ) {
    final Set<CanAccessOffice> collect = this.findOfficePermissions(person).stream()
      .filter(permission -> this.canRemovePermission(permission, roles))
      .collect(Collectors.toSet());
    this.officePermissionRepository.deleteAll(collect);
  }

  private boolean canRemovePermission(
    final HasRole permission,
    final Collection<? extends HasRole> roles
  ) {
    return roles.stream().noneMatch(role -> this.isRoleEquals(role.getRole(), permission.getRole()));
  }

  private boolean isRoleEquals(
    final String role1,
    final String role2
  ) {
    return Objects.equals(role1, role2) || Objects.equals(role2, CITIZEN);
  }

  private List<PublicAgentRoleResponse> getAgentRolesResponseByKey(
    final Long idPerson,
    final String key
  ) {
    return this.acessoCidadaoApi.findRoles(key, idPerson);
  }

  private PublicAgentResponse getPublicAgentResponseByKey(
    final Long idPerson,
    final String key
  ) {
    return this.acessoCidadaoApi.findAllPublicAgents(idPerson)
      .parallelStream()
      .filter(publicAgent -> Objects.equals(publicAgent.getSub(), key))
      .findFirst()
      .orElse(null);
  }

  private PublicAgentResponse getPublicAgentResponseBySub(
    final Long idPerson,
    final String sub
  ) {
    return this.acessoCidadaoApi.findPublicAgentBySub(sub, idPerson).orElse(null);
  }

  private RoleResource getRoleResource(
    final PublicAgentRoleResponse publicAgentRole,
    final Long idPerson
  ) {
    return new RoleResource(publicAgentRole.getName(), this.getWorkLocation(publicAgentRole, idPerson));
  }

  private String getWorkLocation(
    final PublicAgentRoleResponse publicAgentRole,
    final Long idPerson
  ) {
    return this.acessoCidadaoApi.getWorkLocation(publicAgentRole.getOrganizationGuid(), idPerson);
  }

  private Person getPersonByEmail(final String key) {
    return this.personService.findByKey(key).orElse(null);
  }

  private Set<CanAccessOffice> findOfficePermissions(final Person person) {
    return this.officePermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

  private Set<CanAccessPlan> findPlanPermissions(final Person person) {
    return this.planPermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

  private Set<CanAccessWorkpack> findWorkpackPermissions(final Person person) {
    return this.workpackPermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

}
