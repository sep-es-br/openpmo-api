package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.workpackmodel.details.PortfolioModelDetailDto;
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

public class PortfolioDetailDto extends WorkpackDetailDto {

  public static PortfolioDetailDto of(final Workpack workpack) {
    final PortfolioDetailDto portfolioDetailDto = new PortfolioDetailDto();
    portfolioDetailDto.setId(workpack.getId());
    //    portfolioDetailDto.setPlan(PlanDto.of(getPlan(workpack)));
    portfolioDetailDto.setModel(PortfolioModelDetailDto.of(workpack.getWorkpackModelInstance()));
    //    portfolioDetailDto.setChildren(getChildren(workpack));
    //    portfolioDetailDto.setProperties(getProperties(workpack));
    //    portfolioDetailDto.setCosts(getCosts(workpack));
    portfolioDetailDto.setPermissions(new ArrayList<>());
    //TODO portfolioDetailDto.setModelLinked(?);
    //    portfolioDetailDto.setSharedWith(workpack.getSharedWith()
    //                                       .stream()
    //                                       .map(IsSharedWith::getWorkpack)
    //                                       .map(WorkpackSharedDto::of)
    //                                       .collect(Collectors.toList()));
    portfolioDetailDto.setLinked(null);
    //TODO portfolioDetailDto.setLinkedModel(?);
    portfolioDetailDto.setCancelable(workpack.isCancelable());
    portfolioDetailDto.setCanceled(workpack.isCanceled());
    portfolioDetailDto.setCanBeDeleted(workpack.isDeleted());
    //TODO portfolioDetailDto.setHasActiveBaseline(?);
    //TODO portfolioDetailDto.setPendingBaseline(?);
    //TODO portfolioDetailDto.setCancelPropose(?);
    //TODO portfolioDetailDto.setHasScheduleSectionActive(?);
    //TODO portfolioDetailDto.setActiveBaselineName(?);
    portfolioDetailDto.setEndManagementDate(workpack.getEndManagementDate());
    portfolioDetailDto.setReason(workpack.getReason());
    portfolioDetailDto.setCompleted(workpack.getCompleted());
    //TODO portfolioDetailDto.setDashboard(?);
    return portfolioDetailDto;
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
        .orElse(null);
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
          .map(PortfolioDetailDto::of)
          .map(WorkpackDetailDto.class::cast)
            .collect(Collectors.toSet()))
        .orElseGet(HashSet::new);
  }

  private static Plan getPlan(final Workpack workpack) {
    return workpack.getOriginalPlan().orElse(null);
  }
}
