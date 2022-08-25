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

public class ProgramDetailDto extends WorkpackDetailDto {

  public static ProgramDetailDto of(final Workpack workpack) {
    return (ProgramDetailDto) WorkpackDetailDto.of(workpack, ProgramDetailDto::new);
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
        .map(ProgramDetailDto::of)
        .map(WorkpackDetailDto.class::cast)
        .collect(Collectors.toSet()))
      .orElseGet(HashSet::new);
  }

  private static Plan getPlan(final Workpack workpack) {
    return workpack.getOriginalPlan().orElse(null);
  }

}
