package br.gov.es.openpmo.controller.reports;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.reports.ActiveReportItem;
import br.gov.es.openpmo.dto.reports.GeneratedReport;
import br.gov.es.openpmo.dto.reports.ReportRequest;
import br.gov.es.openpmo.dto.reports.ReportScope;
import br.gov.es.openpmo.dto.reports.models.GetReportModelFileItem;
import br.gov.es.openpmo.service.reports.GenerateReportComponent;
import br.gov.es.openpmo.service.reports.GetActiveReports;
import br.gov.es.openpmo.service.reports.GetCompiledReport;
import br.gov.es.openpmo.service.reports.GetReportScope;
import br.gov.es.openpmo.service.reports.HasModelsFromActiveReports;
import io.swagger.annotations.Api;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/report")
public class ReportController {

  private final GetReportScope getReportScope;

  private final GetActiveReports getActiveReports;

  private final HasModelsFromActiveReports hasModelsFromActiveReports;

  private final GetCompiledReport getCompiledReport;

  private final GenerateReportComponent generateReportComponent;

  public ReportController(
    final GetReportScope getReportScope,
    final GetActiveReports getActiveReports,
    final HasModelsFromActiveReports hasModelsFromActiveReports,
    final GetCompiledReport getCompiledReport,
    final GenerateReportComponent generateReportComponent
  ) {
    this.getReportScope = getReportScope;
    this.getActiveReports = getActiveReports;
    this.hasModelsFromActiveReports = hasModelsFromActiveReports;
    this.getCompiledReport = getCompiledReport;
    this.generateReportComponent = generateReportComponent;
  }

  @GetMapping("/scope")
  public ResponseEntity<ResponseBase<ReportScope>> getReportScope(
    @RequestParam("id-plan") final Long idPlan,
    @Authorization final String authorization
  ) {

    final ReportScope response = this.getReportScope.execute(idPlan, authorization);

    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping
  public ResponseEntity<ResponseBaseItens<ActiveReportItem>> getActiveReports(@RequestParam("id-plan") final Long idPlan) {

    final List<ActiveReportItem> response = this.getActiveReports.execute(idPlan);

    return ResponseEntity.ok(ResponseBaseItens.of(response));
  }

  @GetMapping("/active/has-model")
  public ResponseEntity<ResponseBase<Boolean>> hasModelFromActiveReports(@RequestParam("id-plan") final Long idPlan) {
    final Boolean response = this.hasModelsFromActiveReports.execute(idPlan);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{idReport}/compiled")
  public ResponseEntity<ResponseBaseItens<GetReportModelFileItem>> compiledReport(@PathVariable final Long idReport) {
    final List<GetReportModelFileItem> response = this.getCompiledReport.execute(idReport);
    return ResponseEntity.ok(ResponseBaseItens.of(response));
  }

  @PostMapping("/generate")
  public ResponseEntity<ByteArrayResource> generateReport(
    @Authorization String authorization,
    @RequestBody @Valid final ReportRequest request
  ) {

    final GeneratedReport response = this.generateReportComponent.execute(request, authorization);

    if (response.getResource() == null) {
      return ResponseEntity.noContent().build();
    }

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Access-Control-Expose-Headers", "Content-Disposition");
    headers.add("Content-Disposition", "attachment; filename=".concat(response.getFilename()));
    return ResponseEntity.ok().headers(headers).contentLength(response.getResource().contentLength()).contentType(
        response.getContentType())
      .body(response.getResource());
  }

}
