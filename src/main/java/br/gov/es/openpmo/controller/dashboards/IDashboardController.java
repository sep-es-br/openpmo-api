package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;
import java.util.List;

public interface IDashboardController {

  @GetMapping("/baselines")
  Response<List<DashboardBaselineResponse>> getBaselines(
      @RequestParam("id-workpack") final Long workpackId,
      @Authorization final String authorization);

  @GetMapping("/schedule-interval")
  Response<Interval> getInterval(
      @RequestParam("id-workpack") final Long workpackId,
      @Authorization final String authorization);

  @GetMapping
  Response<DashboardResponse> getDashboard(
      @RequestParam(name = "show-header", defaultValue = "true") Boolean showHeader,
      @RequestParam(name = "id-plan") Long planId,
      @RequestParam(name = "id-workpack") Long workpackId,
      @RequestParam(name = "id-workpack-model") Long workpackModelId,
      @RequestParam(name = "id-workpack-model-linked", required = false) Long workpackModelLinkedId,
      @RequestParam(name = "linked", required = false, defaultValue = "false") boolean linked,
      @RequestParam(name = "id-baseline", required = false) Long baselineId,
      @RequestParam(name = "date-reference", required = false) @DateTimeFormat(pattern = "MM/yyyy") YearMonth yearMonth,
      UriComponentsBuilder uriComponentsBuilder,
      @Authorization final String authorization);

  @GetMapping("/calculate")
  Response<Void> calculate(
      @RequestParam("id-workpack") final Long workpackId,
      @RequestParam(value = "calculate-interval", required = false) Boolean calculateInterval,
      @Authorization final String authorization
  );

  @PostMapping("/purge")
  Response<Void> purge(@Authorization final String authorization);

}
