package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardDataResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;
import java.util.List;

public interface IDashboardController {

  @GetMapping("/baselines")
  ResponseEntity<List<DashboardBaselineResponse>> getBaselinesCombo(
      @RequestParam(name = "id-workpack") Long idWorkpack
  );

  @GetMapping
  ResponseEntity<ResponseBase<DashboardDataResponse>> getDashboardData(
    @RequestParam(name = "show-header", defaultValue = "true") Boolean showHeader,
    @RequestParam(name = "id-workpack") Long idWorkpack,
    @RequestParam(name = "id-baseline", required = false) Long idBaseline,
    @RequestParam(name = "date-reference") @DateTimeFormat(pattern = "MM/yyyy") YearMonth yearMonth,
    UriComponentsBuilder uriComponentsBuilder
  );

}
