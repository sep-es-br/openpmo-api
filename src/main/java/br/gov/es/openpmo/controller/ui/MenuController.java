package br.gov.es.openpmo.controller.ui;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanModelMenuResponse;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.PortfolioParentsResponse;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.menu.WorkpackModelParentsResponse;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.properties.GetSorterProperty;
import br.gov.es.openpmo.service.ui.IGetPortfolioParents;
import br.gov.es.openpmo.service.ui.IGetWorkpackModelParents;
import br.gov.es.openpmo.service.ui.MenuService;
import br.gov.es.openpmo.service.ui.WorkpackModelMenuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Api
@RestController
@CrossOrigin
@RequestMapping("/menus")
public class MenuController {

  private final MenuService menuService;

  private final TokenService tokenService;

  private final IGetPortfolioParents getPortfolioParents;

  private final IGetWorkpackModelParents getWorkpackModelParents;

  private final WorkpackModelMenuService workpackModelMenuService;

  @Autowired
  public MenuController(
    final MenuService menuService,
    final TokenService tokenService,
    final IGetPortfolioParents getPortfolioParents,
    final IGetWorkpackModelParents getWorkpackModelParents,
    final WorkpackModelMenuService workpackModelMenuService
  ) {
    this.menuService = menuService;
    this.tokenService = tokenService;
    this.getPortfolioParents = getPortfolioParents;
    this.getWorkpackModelParents = getWorkpackModelParents;
    this.workpackModelMenuService = workpackModelMenuService;
  }

  @GetMapping("/office")
  public ResponseEntity<ResponseBaseItens<MenuOfficeDto>> indexOffice(
    @RequestHeader(name = "Authorization") final String authorization
  ) {

    final Long idUser = this.tokenService.getUserId(authorization);
    final List<MenuOfficeDto> offices = this.menuService.findAllOffice(idUser);
    GetSorterProperty.clear();

    if (offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(ResponseBaseItens.of(offices));
  }

  @GetMapping("/portfolio")
  public ResponseEntity<ResponseBaseItens<WorkpackMenuDto>> indexPortfolio(
    @RequestParam("id-office") final Long idOffice,
    @RequestParam(value = "id-plan", required = false) final Long idPlan,
    @RequestHeader(name = "Authorization") final String authorization
  ) {

    final Long idUser = this.tokenService.getUserId(authorization);
    final Set<WorkpackMenuDto> portfolios = this.menuService
      .findAllPortfolio(new PortfolioMenuRequest(idOffice, idPlan, idUser));
    GetSorterProperty.clear();

    if (portfolios.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(ResponseBaseItens.of(portfolios));
  }

  @GetMapping("/planModels")
  public ResponseEntity<ResponseBaseItens<PlanModelMenuResponse>> indexWorkpackModels(
    @RequestParam("id-office") final Long idOffice
  ) {
    final List<PlanModelMenuResponse> responses = this.workpackModelMenuService.getResponses(idOffice);
    if (responses.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBaseItens.of(responses));
  }

  @GetMapping("/portfolios/parents")
  public ResponseEntity<ResponseBase<PortfolioParentsResponse>> parentHierarchy(
    @RequestParam("id-plan") final Long idPlan,
    @RequestParam("id-workpack") final Long idWorkpack
  ) {
    final PortfolioParentsResponse response = this.getPortfolioParents.execute(idPlan, idWorkpack);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/planModels/parents")
  public ResponseEntity<ResponseBase<?>> getWorkpackModelHierarchy(
    @RequestParam("id-workpack-model") final Long idWorkpackModel
  ) {
    final WorkpackModelParentsResponse response = this.getWorkpackModelParents.execute(idWorkpackModel);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

}
