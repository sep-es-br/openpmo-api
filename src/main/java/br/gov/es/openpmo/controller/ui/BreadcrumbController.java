package br.gov.es.openpmo.controller.ui;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.menu.BreadcrumbDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.ui.LocalityBreadcrumbService;
import br.gov.es.openpmo.service.ui.WorkpackBreadcrumbService;
import br.gov.es.openpmo.service.ui.WorkpackModelBreadcrumbService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/breadcrumbs")
public class BreadcrumbController {

  private final WorkpackBreadcrumbService workpackBreadcrumbService;
  private final LocalityBreadcrumbService localityBreadcrumbService;
  private final WorkpackModelBreadcrumbService workpackModelBreadcrumbService;
  private final ICanAccessService canAccessService;

  @Autowired
  public BreadcrumbController(
      final WorkpackBreadcrumbService workpackBreadcrumbService,
      final LocalityBreadcrumbService localityBreadcrumbService,
      final WorkpackModelBreadcrumbService workpackModelBreadcrumbService,
      final ICanAccessService canAccessService) {
    this.workpackBreadcrumbService = workpackBreadcrumbService;
    this.localityBreadcrumbService = localityBreadcrumbService;
    this.workpackModelBreadcrumbService = workpackModelBreadcrumbService;
    this.canAccessService = canAccessService;
  }

  @GetMapping("/locality/{id-locality}")
  public ResponseEntity<ResponseBaseItens<BreadcrumbDto>> locality(@PathVariable("id-locality") final Long idLocality,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(idLocality, authorization);
    final List<BreadcrumbDto> breadcrumbItens = this.localityBreadcrumbService
        .buildLocalityHierarchyAsBreadcrumb(idLocality);
    return ResponseEntity.ok(ResponseBaseItens.of(breadcrumbItens));
  }

  @GetMapping("/workpack/{id-workpack}")
  public ResponseEntity<ResponseBaseItens<BreadcrumbDto>> workpack(
      @PathVariable("id-workpack") final Long idWorkpack,
      @RequestParam("id-plan") final Long idPlan) {
    final Collection<BreadcrumbDto> breadcrumbItens = this.workpackBreadcrumbService.buildWorkpackHierarchyAsBreadcrumb(
        idWorkpack,
        idPlan);
    return ResponseEntity.ok(ResponseBaseItens.of(breadcrumbItens));
  }

  @GetMapping("/model/{id-workpack-model}")
  public ResponseEntity<ResponseBaseItens<BreadcrumbDto>> model(
      @PathVariable("id-workpack-model") final Long idWorkpackModel) {
    final List<BreadcrumbDto> breadcrumbItens = this.workpackModelBreadcrumbService
        .buildWorkpackModelHierarchyAsBreadcrumb(
            idWorkpackModel);
    return ResponseEntity.ok(ResponseBaseItens.of(breadcrumbItens));
  }

}
