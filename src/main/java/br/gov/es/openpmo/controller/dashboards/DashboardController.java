package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardBaselineService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardIntervalService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.YearMonth;
import java.util.List;

@Api
@RestController
@RequestMapping("dashboards")
public class DashboardController implements IDashboardController {

  private final ResponseHandler responseHandler;
  private final IDashboardBaselineService baselineService;
  private final IDashboardIntervalService intervalService;
  private final IDashboardService dashboardService;
  private final IAsyncDashboardService asyncDashboardService;
  private final ICanAccessService canAccessService;

  public DashboardController(
      final ResponseHandler responseHandler,
      final IDashboardBaselineService baselineService,
      final IDashboardService dashboardService,
      final IDashboardIntervalService intervalService,
      final IAsyncDashboardService asyncDashboardService,
      final ICanAccessService canAccessService) {
    this.responseHandler = responseHandler;
    this.baselineService = baselineService;
    this.dashboardService = dashboardService;
    this.intervalService = intervalService;
    this.asyncDashboardService = asyncDashboardService;
    this.canAccessService = canAccessService;
  }

  @Override
  public Response<List<DashboardBaselineResponse>> getBaselines(final Long workpackId,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(workpackId, authorization);
    final List<DashboardBaselineResponse> baselines = this.baselineService.getBaselines(workpackId);
    return this.responseHandler.success(baselines);
  }

  @Override
  public Response<Interval> getInterval(final Long workpackId,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(workpackId, authorization);
    final Interval interval = this.intervalService.calculateFor(workpackId);
    return this.responseHandler.success(interval);
  }

  @Override
  public Response<DashboardResponse> getDashboard(
      final Boolean showHeader,
      final Long workpackId,
      final Long baselineId,
      final YearMonth yearMonth,
      final UriComponentsBuilder uriComponentsBuilder,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(workpackId, authorization);
    final DashboardParameters parameters = new DashboardParameters(showHeader, workpackId, baselineId, yearMonth,
        uriComponentsBuilder);

    final DashboardResponse response = this.dashboardService.build(parameters);
    return this.responseHandler.success(response);
  }

  @Override
  public Response<Void> calculate(final Long workpackId,
      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(workpackId, authorization);
    this.asyncDashboardService.calculate(workpackId);
    return this.responseHandler.success();
  }

}
