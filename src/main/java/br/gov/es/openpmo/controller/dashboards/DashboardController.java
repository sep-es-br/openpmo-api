package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;
import br.gov.es.openpmo.service.dashboards.IDashboardBaselineFilter;
import br.gov.es.openpmo.service.dashboards.IGetDashboardData;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;
import java.util.List;

@Api
@RestController
@RequestMapping("/dashboards")
public class DashboardController implements IDashboardController {

  private final IGetDashboardData dashboardData;

  private final IDashboardBaselineFilter baselineFilter;

  @Autowired
  public DashboardController(
    final IGetDashboardData dashboardData,
    final IDashboardBaselineFilter baselineFilter
  ) {
    this.dashboardData = dashboardData;
    this.baselineFilter = baselineFilter;
  }

  @Override
  public ResponseEntity<List<DashboardBaselineResponse>> getBaselinesCombo(final Long idWorkpack) {
    return ResponseEntity.ok(this.baselineFilter.getBaselines(idWorkpack));
  }

  @Override
  public ResponseEntity<ResponseBase<DashboardDataResponse>> getDashboardData(
    final Boolean showHeader,
    final Long idWorkpack,
    final Long idBaseline,
    final YearMonth yearMonth,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final DashboardDataParameters parameters = new DashboardDataParameters(
      showHeader,
      idWorkpack,
      idBaseline,
      yearMonth,
      uriComponentsBuilder
    );
    final DashboardDataResponse dataResponse = this.dashboardData.get(parameters);
    return ResponseEntity.ok(ResponseBase.of(dataResponse));
  }

}
