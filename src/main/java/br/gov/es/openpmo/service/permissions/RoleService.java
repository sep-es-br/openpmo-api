package br.gov.es.openpmo.service.permissions;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.dto.person.RoleResource;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {

    public static final String CITIZEN = "citizen";

    private final AcessoCidadaoApi acessoCidadaoApi;

    private final PersonService personService;

    private final OfficePermissionRepository officePermissionRepository;

    private final PlanPermissionRepository planPermissionRepository;

    private final WorkpackPermissionRepository workpackPermissionRepository;

    @Autowired
    public RoleService(
            AcessoCidadaoApi acessoCidadaoApi,
            PersonService personService,
            OfficePermissionRepository officePermissionRepository,
            PlanPermissionRepository planPermissionRepository,
            WorkpackPermissionRepository workpackPermissionRepository
    ) {
        this.acessoCidadaoApi = acessoCidadaoApi;
        this.personService = personService;
        this.officePermissionRepository = officePermissionRepository;
        this.planPermissionRepository = planPermissionRepository;
        this.workpackPermissionRepository = workpackPermissionRepository;
    }

    @Transactional
    public List<RoleResource> getRolesByEmail(Long idPerson, String email) {
        return getRolesOfPublicAgent(idPerson, getPublicAgentResponseByEmail(idPerson, email));
    }

    @Transactional
    public List<RoleResource> getRolesBySub(Long idPerson, String sub) {
        return getRolesOfPublicAgent(idPerson, getPublicAgentResponseBySub(idPerson, sub));
    }

    private List<RoleResource> getRolesOfPublicAgent(Long idPerson, PublicAgentResponse publicAgent) {
        if (publicAgent == null) {
            return Collections.emptyList();
        }
        List<RoleResource> roles = getRolesSorted(idPerson, publicAgent);
        removeOldPermissions(roles, getPersonByEmail(publicAgent));
        return roles;
    }

    private void removeOldPermissions(Collection<RoleResource> roles, Person person) {
        removeOldOfficePermissions(roles, person);
        removeOldPlanPermissions(roles, person);
        removeOldWorkpackPermissions(roles, person);
    }

    private List<RoleResource> getRolesSorted(Long idPerson, PublicAgentResponse publicAgent) {
        List<RoleResource> roles = new ArrayList<>();
        for (PublicAgentRoleResponse roleResponse : getRolesByEmail(publicAgent, idPerson)) {
            roles.add(this.getRoleResource(roleResponse, idPerson));
        }
        roles.sort(this::sortByRoleIgnoreCase);
        return roles;
    }

    private int sortByRoleIgnoreCase(HasRole first, HasRole second) {
        return first.getRole().compareToIgnoreCase(second.getRole());
    }

    private void removeOldWorkpackPermissions(Collection<RoleResource> roles, Person person) {
        Set<CanAccessWorkpack> removable = findWorkpackPermissions(person).stream()
                .filter(permission -> canRemovePermission(permission, roles))
                .collect(Collectors.toSet());
        this.workpackPermissionRepository.deleteAll(removable);
    }

    private void removeOldPlanPermissions(Collection<RoleResource> roles, Person person) {
        Set<CanAccessPlan> removable = findPlanPermissions(person).stream()
                .filter(permission -> canRemovePermission(permission, roles))
                .collect(Collectors.toSet());
        this.planPermissionRepository.deleteAll(removable);
    }

    private void removeOldOfficePermissions(Collection<RoleResource> roles, Person person) {
        Set<CanAccessOffice> collect = findOfficePermissions(person).stream()
                .filter(permission -> canRemovePermission(permission, roles))
                .collect(Collectors.toSet());
        this.officePermissionRepository.deleteAll(collect);
    }

    private boolean canRemovePermission(HasRole permission, Collection<? extends HasRole> roles) {
        return roles.stream().noneMatch(role -> isRoleEquals(role.getRole(), permission.getRole()));
    }

    private boolean isRoleEquals(String role1, String role2) {
        return Objects.equals(role1, role2) || Objects.equals(role2, CITIZEN);
    }

    private List<PublicAgentRoleResponse> getRolesByEmail(PublicAgentResponse publicAgent, Long idPerson) {
        return this.acessoCidadaoApi.findRoles(publicAgent.getSub(), idPerson);
    }

    private PublicAgentResponse getPublicAgentResponseByEmail(Long idPerson, String email) {
        return this.acessoCidadaoApi.findAllPublicAgents(idPerson).stream()
                .filter(publicAgent -> Objects.equals(publicAgent.getEmail(), email))
                .findFirst().orElse(null);
    }

    private PublicAgentResponse getPublicAgentResponseBySub(Long idPerson, String sub) {
        return this.acessoCidadaoApi.findPublicAgentBySub(sub, idPerson).orElse(null);
    }

    private RoleResource getRoleResource(final PublicAgentRoleResponse publicAgentRole, final Long idPerson) {
        return new RoleResource(publicAgentRole.getName(), getWorkLocation(publicAgentRole, idPerson));
    }

    private String getWorkLocation(PublicAgentRoleResponse publicAgentRole, Long idPerson) {
        return this.acessoCidadaoApi.getWorkLocation(publicAgentRole.getOrganizationGuid(), idPerson);
    }

    private Person getPersonByEmail(PublicAgentResponse publicAgent) {
        return this.personService.findByEmail(publicAgent.getEmail())
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
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
