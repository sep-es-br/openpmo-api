package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrganizerDetailDto extends WorkpackDetailDto {

  public static OrganizerDetailDto of(final Workpack workpack) {
    final OrganizerDetailDto organizerDetailDto = new OrganizerDetailDto();
    organizerDetailDto.setId(workpack.getId());
    //    organizerDetailDto.setPlan(PlanDto.of(getPlan(workpack)));
    //    organizerDetailDto.setModel(OrganizerModelDetailDto.of(workpack.getWorkpackModelInstance()));
    //    organizerDetailDto.setChildren(getChildren(workpack));
    //    organizerDetailDto.setProperties(getProperties(workpack));
    //    organizerDetailDto.setCosts(getCosts(workpack));
    //    organizerDetailDto.setPermissions(new ArrayList<>());
    //TODO organizerDetailDto.setModelLinked(?);
    //    organizerDetailDto.setSharedWith(getSharedWith(workpack));
    //    organizerDetailDto.setLinked(null);
    //TODO organizerDetailDto.setLinkedModel(?);
    organizerDetailDto.setCancelable(workpack.isCancelable());
    organizerDetailDto.setCanceled(workpack.isCanceled());
    organizerDetailDto.setCanBeDeleted(workpack.isDeleted());
    //TODO organizerDetailDto.setHasActiveBaseline(?);
    //TODO organizerDetailDto.setPendingBaseline(?);
    //TODO organizerDetailDto.setCancelPropose(?);
    //TODO organizerDetailDto.setHasScheduleSectionActive(?);
    //TODO organizerDetailDto.setActiveBaselineName(?);
    organizerDetailDto.setEndManagementDate(workpack.getEndManagementDate());
    organizerDetailDto.setReason(workpack.getReason());
    organizerDetailDto.setCompleted(workpack.getCompleted());
    //TODO organizerDetailDto.setDashboard(?);
    return organizerDetailDto;
  }

  private static List<WorkpackSharedDto> getSharedWith(final Workpack workpack) {
    return Optional.ofNullable(workpack)
        .map(Workpack::getSharedWith)
        .map(isSharedWiths -> isSharedWiths
            .stream()
            .map(IsSharedWith::getWorkpack)
            .map(WorkpackSharedDto::of)
            .collect(Collectors.toList()))
        .orElseGet(ArrayList::new);
  }

  private static Set<CostAccountDto> getCosts(final Workpack workpack) {
    return Optional.ofNullable(workpack)
        .map(Workpack::getCosts)
        .map(accounts -> accounts
            .stream()
            .map(CostAccountDto::of)
            .collect(Collectors.toSet()))
        .orElseGet(HashSet::new);
  }

  private static List<PropertyDto> getProperties(final Workpack workpack) {
    return Optional.ofNullable(workpack)
        .map(Workpack::getProperties)
        .map(propertySet -> propertySet
            .stream()
            .map(PropertyDto::of)
            .collect(Collectors.toList()))
        .orElseGet(ArrayList::new);
  }

  private static Set<WorkpackDetailDto> getChildren(final Workpack workpack) {
    return Optional.ofNullable(workpack)
        .map(Workpack::getChildren)
        .map(workpackSet -> workpackSet.stream()
            .map(OrganizerDetailDto::of)
            .map(organizerDetailDto -> (WorkpackDetailDto) organizerDetailDto)
            .collect(Collectors.toSet()))
        .orElseGet(HashSet::new);
  }

  private static Plan getPlan(final Workpack workpack) {
    return workpack.getOriginalPlan().orElse(null);
  }
}
