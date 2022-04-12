package br.gov.es.openpmo.controller.ui;

import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.ui.MenuService;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
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

@Api
@RestController
@CrossOrigin
@RequestMapping("/menus")
public class MenuController {

  private final MenuService menuService;
  private final TokenService tokenService;

  @Autowired
  public MenuController(final MenuService menuService, final TokenService tokenService) {
    this.menuService = menuService;
    this.tokenService = tokenService;
  }

  @GetMapping("/office")
  public ResponseEntity<ResponseBaseItens<MenuOfficeDto>> indexOffice(
    @RequestHeader(name = "Authorization") final String autorization
  ) {
    final Long idUser = this.tokenService.getUserId(autorization);
    final List<MenuOfficeDto> offices = this.menuService.findAllOffice(idUser);
    if(offices.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBaseItens.of(offices));
  }

  @GetMapping("/portfolio")
  public ResponseEntity<ResponseBaseItens<WorkpackMenuDto>> indexPortfolio(
    @RequestParam("id-office") final Long idOffice,
    @RequestHeader(name = "Authorization") final String autorization
  ) {
    final Long idUser = this.tokenService.getUserId(autorization);
    final List<WorkpackMenuDto> portfolios = this.menuService.findAllPortfolio(new PortfolioMenuRequest(
      idOffice,
      idUser
    ));
    if(portfolios.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBaseItens.of(portfolios));
  }

}