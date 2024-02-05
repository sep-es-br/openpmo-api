package br.gov.es.openpmo.service.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

@Service
public class WorkpackBreadcrumbService {

  private final ApplicationCacheUtil cacheUtil;


  @Autowired
  public WorkpackBreadcrumbService(
    final ApplicationCacheUtil cacheUtil
  ) {
    this.cacheUtil = cacheUtil;
  }

  private static List<BreadcrumbDto> reverseBreadcrumbs(final List<BreadcrumbDto> breadcrumbs) {
    Collections.reverse(breadcrumbs);
    return breadcrumbs;
  }


  private static BreadcrumbDto fromWorkpackMenuResult(WorkpackResultDto workpack) {
    BreadcrumbDto dto = new BreadcrumbDto(
        workpack.getId(),
        workpack.getName(),
        workpack.getFullName(),
        workpack.getType());
    dto.setModelName(workpack.getModelName());
    return dto;
  }

  public Collection<BreadcrumbDto> buildWorkpackHierarchyAsBreadcrumb(
    final Long idWorkpack,
    final Long idPlan
  ) {
    List<BreadcrumbDto> result = new ArrayList<>(0);
    List<WorkpackResultDto> list = cacheUtil.getListWorkpackResultDtoByPlan(idPlan);

    WorkpackResultDto actual = list.stream().filter(w -> w.getId().equals(idWorkpack)).findFirst().orElse(null);
    if (actual != null) {
      addBreadcrumb(actual, list, result);
    }
    return reverseBreadcrumbs(result);
  }

  private void addBreadcrumb(WorkpackResultDto actual, List<WorkpackResultDto> list, List<BreadcrumbDto> result) {
    result.add(fromWorkpackMenuResult(actual));
    if (actual.getIdParent() != null) {
      addParent(actual.getIdParent(), list, result);
    }
  }

  private void addParent(Long idParent, List<WorkpackResultDto> list, List<BreadcrumbDto> result) {
    WorkpackResultDto parent = list.stream().filter(w -> w.getId().equals(idParent)).findFirst().orElse(null);
    if (parent != null) {
      addBreadcrumb(parent, list, result);
    }
  }

}
