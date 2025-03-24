package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.permissions.PermissionRepository;
import br.gov.es.openpmo.service.actors.IGetPersonFromAuthorization;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetReportScope {
  private final PlanRepository planRepository;
  private final PermissionRepository permissionRepository;
  private final IGetPersonFromAuthorization getPersonFromAuthorization;
  private final WorkpackRepository workpackRepository;

  public GetReportScope(
          final PlanRepository planRepository,
          final PermissionRepository permissionRepository,
          IGetPersonFromAuthorization getPersonFromAuthorization, WorkpackRepository workpackRepository) {
    this.planRepository = planRepository;
    this.permissionRepository = permissionRepository;
    this.getPersonFromAuthorization = getPersonFromAuthorization;
    this.workpackRepository = workpackRepository;
  }

  public List<Long> execute(final List<Long> scope, final Long idPlan, final String authorization) {
    final List<Long> scopeResponse = new ArrayList<>();
    final IGetPersonFromAuthorization.PersonDataResponse personData = this.getPersonFromAuthorization.execute(authorization);
    final boolean isAdministrator = personData.getPerson().getAdministrator();
    boolean hasPermissionOfficeOrPlan = this.permissionRepository.hasPermissionOfficeOrPlanByIdPlan(idPlan, personData.getKey());
    if (isAdministrator || hasPermissionOfficeOrPlan) {
      if (scope.size() == 1 && scope.get(0).equals(idPlan)) {
        final List<Long> workpacksIds = this.planRepository.findWorkpacksBelongsToPlan(idPlan);
        if (workpacksIds != null && !workpacksIds.isEmpty()) {
          scopeResponse.addAll(workpacksIds);
        }
        return scopeResponse;
      }
      scopeResponse.addAll(scope);
      final List<Long> workpacksChildren = this.workpackRepository.idsWorkpacksChildren(scope);
      if (workpacksChildren != null && !workpacksChildren.isEmpty()) {
        scopeResponse.addAll(workpacksChildren);
      }
      return scopeResponse;
    }
    if (scope.size() == 1 && scope.get(0).equals(idPlan)) {
      final List<Long> workpacksWithPermission = this.planRepository.findWorkpacksBelongsToPlanWithPermission(idPlan, personData.getKey());
      if (workpacksWithPermission != null && !workpacksWithPermission.isEmpty()) {
        scopeResponse.addAll(workpacksWithPermission);
      }
      final List<Long> workpacksChildren = this.workpackRepository.idsWorkpacksChildren(workpacksWithPermission);
      if (workpacksChildren != null && !workpacksChildren.isEmpty()) {
        scopeResponse.addAll(workpacksChildren);
      }
      return scopeResponse;
    }

    for (final Long scopeId : scope) {
      final List<Long> workpacksIds = new ArrayList<>();
      workpacksIds.add(scopeId);
      boolean hasPermissionWorkpackSelfOrParents = this.permissionRepository.hasPermissionWorkpackSelfOrParents(scopeId, personData.getKey());
      if (hasPermissionWorkpackSelfOrParents) {
        scopeResponse.add(scopeId);
        final List<Long> workpacksChildren = this.workpackRepository.idsWorkpacksChildren(workpacksIds);
        if (workpacksChildren != null && !workpacksChildren.isEmpty()) {
          scopeResponse.addAll(workpacksChildren);
        }
      } else {
        final List<Long> workpacksChildrenWithPermission = this.permissionRepository.idsWorkpacksChildrenWithPermission(workpacksIds, personData.getKey());
        if (workpacksChildrenWithPermission != null && !workpacksChildrenWithPermission.isEmpty()) {
          scopeResponse.addAll(workpacksChildrenWithPermission);
          final List<Long> workpacksChildren = this.workpackRepository.idsWorkpacksChildren(workpacksChildrenWithPermission);
          if (workpacksChildren != null && !workpacksChildren.isEmpty()) {
            scopeResponse.addAll(workpacksChildren);
          }
        }
      }
    }
    return scopeResponse;
  }

}
