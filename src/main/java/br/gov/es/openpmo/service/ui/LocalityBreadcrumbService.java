package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.service.office.LocalityService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LocalityBreadcrumbService {

  private final LocalityService localityService;

  public LocalityBreadcrumbService(final LocalityService localityService) {
    this.localityService = localityService;
  }

  private static BreadcrumbDto fromLocality(final Locality locality) {
    return new BreadcrumbDto(
      locality.getId(),
      locality.getName(),
      locality.getFullName(),
      locality.getType() != null ? locality.getType().name() : null
    );
  }

  private static List<BreadcrumbDto> reverseBreadcrumbs(final List<BreadcrumbDto> breadcrumbs) {
    Collections.reverse(breadcrumbs);
    return breadcrumbs;
  }

  public List<BreadcrumbDto> buildLocalityHierarchyAsBreadcrumb(final Long id) {
    final List<BreadcrumbDto> breadcrumbs = new ArrayList<>(0);
    final Locality locality = this.localityService.findByIdWithParent(id);

    breadcrumbs.add(fromLocality(locality));

    if(locality.getParent() != null) {
      this.addLocalityParentBreadcrumbDto(locality.getParent(), breadcrumbs);
    }
    return reverseBreadcrumbs(breadcrumbs);
  }

  private void addLocalityParentBreadcrumbDto(final Locality locality, final List<BreadcrumbDto> breadcrumbs) {
    breadcrumbs.add(fromLocality(locality));
    if(locality.getParent() != null) {
      this.addLocalityParentBreadcrumbDto(locality.getParent(), breadcrumbs);
    }
  }

}

