package br.gov.es.openpmo.controller.ui;

import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanModelMenuResponse;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.ui.MenuService;
import br.gov.es.openpmo.service.ui.WorkpackModelMenuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;
    private final TokenService tokenService;
    private final WorkpackModelMenuService workpackModelMenuService;

    @Autowired
    public MenuController(
            final MenuService menuService,
            final TokenService tokenService,
            final WorkpackModelMenuService workpackModelMenuService
    ) {
        this.menuService = menuService;
        this.tokenService = tokenService;
        this.workpackModelMenuService = workpackModelMenuService;
    }

    @GetMapping("/office")
    public ResponseEntity<ResponseBaseItens<MenuOfficeDto>> indexOffice(
            @RequestHeader(name = "Authorization") final String autorization
    ) {
        final Long idUser = this.tokenService.getUserId(autorization);
        final List<MenuOfficeDto> offices = this.menuService.findAllOffice(idUser);

        if (offices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ResponseBaseItens.of(offices));
    }

    @GetMapping("/portfolio")
    public ResponseEntity<ResponseBaseItens<WorkpackMenuDto>> indexPortfolio(
      @RequestParam("id-office") final Long idOffice,
      @RequestParam(value = "id-plan", required = false) final Long idPlan,
      @RequestHeader(name = "Authorization") final String autorization
    ) {
        final Long idUser = this.tokenService.getUserId(autorization);
        final List<WorkpackMenuDto> portfolios =
          this.menuService.findAllPortfolio(new PortfolioMenuRequest(idOffice, idPlan, idUser));

        if (portfolios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ResponseBaseItens.of(portfolios));
    }

    @GetMapping("/planModels")
    public ResponseEntity<ResponseBaseItens<PlanModelMenuResponse>> indexWorkpackModels(
            @RequestParam("id-office") final Long idOffice
    ) {
        List<PlanModelMenuResponse> responses = workpackModelMenuService.getResponses(idOffice);

        if (responses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ResponseBaseItens.of(responses));
    }

}
