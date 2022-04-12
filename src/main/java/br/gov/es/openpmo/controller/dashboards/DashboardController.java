package br.gov.es.openpmo.controller.dashboards;

import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.v2.DashboardResponse;
import br.gov.es.openpmo.dto.dashboards.v2.Interval;
import br.gov.es.openpmo.service.dashboards.v2.IAsyncDashboardService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardBaselineService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardIntervalService;
import br.gov.es.openpmo.service.dashboards.v2.IDashboardService;
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

    public DashboardController(
            ResponseHandler responseHandler,
            IDashboardBaselineService baselineService,
            IDashboardService dashboardService,
            IDashboardIntervalService intervalService,
            IAsyncDashboardService asyncDashboardService
    ) {
        this.responseHandler = responseHandler;
        this.baselineService = baselineService;
        this.dashboardService = dashboardService;
        this.intervalService = intervalService;
        this.asyncDashboardService = asyncDashboardService;
    }

    @Override
    public Response<List<DashboardBaselineResponse>> getBaselines(Long workpackId) {
        final List<DashboardBaselineResponse> baselines = this.baselineService.getBaselines(workpackId);
        return responseHandler.success(baselines);
    }

    @Override
    public Response<Interval> getInterval(Long workpackId) {
        final Interval interval = this.intervalService.calculateFor(workpackId);
        return this.responseHandler.success(interval);
    }

    @Override
    public Response<DashboardResponse> getDashboard(
            Boolean showHeader,
            Long workpackId,
            Long baselineId,
            YearMonth yearMonth,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        final DashboardParameters parameters =
                new DashboardParameters(showHeader, workpackId, baselineId, yearMonth, uriComponentsBuilder);

        DashboardResponse response = this.dashboardService.build(parameters);
        return this.responseHandler.success(response);
    }

    @Override
    public Response<Void> calculate(Long workpackId) {
        this.asyncDashboardService.calculate(workpackId);
        return this.responseHandler.success();
    }

}