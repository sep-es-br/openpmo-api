package br.gov.es.openpmo.scheduler.updateroles;

import br.gov.es.openpmo.apis.acessocidadao.AcessoCidadaoApi;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.repository.IsAuthenticatedByRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.PlanPermissionRepository;
import br.gov.es.openpmo.repository.WorkpackPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class UpdateLocalRolesUsingRemoteRoles {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLocalRolesUsingRemoteRoles.class);

  private final AcessoCidadaoApi acessoCidadaoApi;

  private final IsAuthenticatedByRepository isAuthenticatedByRepository;

  private final PlanPermissionRepository planPermissionRepository;

  private final WorkpackPermissionRepository workpackPermissionRepository;

  private final OfficePermissionRepository officePermissionRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public UpdateLocalRolesUsingRemoteRoles(
    final AcessoCidadaoApi acessoCidadaoApi,
    final IsAuthenticatedByRepository isAuthenticatedByRepository,
    final PlanPermissionRepository planPermissionRepository,
    final WorkpackPermissionRepository workpackPermissionRepository,
    final OfficePermissionRepository officePermissionRepository,
    final IsCCBMemberRepository ccbMemberRepository
  ) {
    this.acessoCidadaoApi = acessoCidadaoApi;
    this.isAuthenticatedByRepository = isAuthenticatedByRepository;
    this.planPermissionRepository = planPermissionRepository;
    this.workpackPermissionRepository = workpackPermissionRepository;
    this.officePermissionRepository = officePermissionRepository;
    this.ccbMemberRepository = ccbMemberRepository;
  }

  @Transactional
  public void updatePersonRoles() {

    LOGGER.info("Initializing search for remote roles");

    final List<IsAuthenticatedBy> allUserAuthentications = this.findAllAuthentications();

    LOGGER.info("Found {} users", allUserAuthentications.size());

    for(final IsAuthenticatedBy userAuth : allUserAuthentications) {
      if(userAuth.getGuid() == null) continue;

      final List<String> roles = this.fetchRoles(userAuth);

      LOGGER.info("Found {} roles for user {}", roles.size(), userAuth.getIdPerson());

      final Person person = userAuth.getPerson();

      deleteRolesNotFound(
        roles,
        this.workpackPermissionRepository::deleteAll,
        this.findAllWorkpackPermissions(person)
      );
      deleteRolesNotFound(
        roles,
        this.planPermissionRepository::deleteAll,
        this.findAllPlanPermissions(person)
      );

      deleteRolesNotFound(
        roles,
        this.officePermissionRepository::deleteAll,
        this.findAllOfficePermissions(person)
      );

      deleteRolesNotFound(
        roles,
        this.ccbMemberRepository::deleteAll,
        this.findAllCCBMemberRelations(person)
      );
    }
    LOGGER.info("Finalizing update using remote roles");
  }

  private Set<IsCCBMemberFor> findAllCCBMemberRelations(final Person person) {
    return this.ccbMemberRepository.findAllCCBMemberOfPerson(person.getId());
  }

  private static <T extends HasRole> void deleteRolesNotFound(
    final List<String> roles,
    final Consumer<? super List<T>> deleteMethod,
    final Collection<? extends T> permissions
  ) {
    final List<T> removedPermissions = extractRemovedPermission(
      roles,
      permissions
    );

    LOGGER.info("Found {} roles to delete", removedPermissions.size());

    deleteMethod.accept(removedPermissions);
  }

  private static <T extends HasRole> List<T> extractRemovedPermission(
    final List<String> roles,
    final Collection<? extends T> permissions
  ) {
    return permissions.stream()
      .filter(permission -> !roles.contains(permission.getRole()))
      .collect(Collectors.toList());
  }

  private Set<CanAccessWorkpack> findAllWorkpackPermissions(final Person person) {
    return this.workpackPermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

  private List<String> fetchRoles(final IsAuthenticatedBy userAuth) {
    return this.acessoCidadaoApi.findRoles(userAuth.getGuid(), null).stream()
      .map(PublicAgentRoleResponse::getName)
      .collect(Collectors.toList());
  }

  private Set<CanAccessOffice> findAllOfficePermissions(final Person person) {
    return this.officePermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

  private List<IsAuthenticatedBy> findAllAuthentications() {
    final Spliterator<IsAuthenticatedBy> spliterator = this.isAuthenticatedByRepository.findAll().spliterator();
    return StreamSupport.stream(spliterator, false).collect(Collectors.toList());

  }

  private Set<CanAccessPlan> findAllPlanPermissions(final Person person) {
    return this.planPermissionRepository.findAllPermissionsOfPerson(person.getId());
  }

}
