package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.dashboards.DashboardMenuResponse;
import br.gov.es.openpmo.service.dashboards.v2.GetDashboardMenu;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/dashboards/workpack-model/menu")
public class DashboardMenuController {

  private final GetDashboardMenu getDashboardMenu;

  public DashboardMenuController(GetDashboardMenu getDashboardMenu) {
    this.getDashboardMenu = getDashboardMenu;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<DashboardMenuResponse>>> getDashboardMenu(
    @RequestParam Long idWorkpackActual,
    @RequestParam Long idWorkpackModel,
    @RequestParam Long menuLevel
  ) {
    final List<DashboardMenuResponse> response = this.getDashboardMenu.execute(idWorkpackActual, idWorkpackModel, menuLevel);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

}
