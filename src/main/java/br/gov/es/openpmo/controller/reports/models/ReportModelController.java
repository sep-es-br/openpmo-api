package br.gov.es.openpmo.controller.reports.models;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.reports.models.CreateReportModelRequest;
import br.gov.es.openpmo.dto.reports.models.CreateReportModelResponse;
import br.gov.es.openpmo.dto.reports.models.GetAllReportModelsResponse;
import br.gov.es.openpmo.dto.reports.models.GetReportModelByIdResponse;
import br.gov.es.openpmo.dto.reports.models.UpdateReportModelRequest;
import br.gov.es.openpmo.service.reports.CompileReportComponent;
import br.gov.es.openpmo.service.reports.models.CreateReportModel;
import br.gov.es.openpmo.service.reports.models.DeleteReportModelById;
import br.gov.es.openpmo.service.reports.models.GetActiveReportModels;
import br.gov.es.openpmo.service.reports.models.GetAllReportModels;
import br.gov.es.openpmo.service.reports.models.GetReportModelDetailById;
import br.gov.es.openpmo.service.reports.models.SwitchActiveReportModel;
import br.gov.es.openpmo.service.reports.models.UpdateReportModel;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/report-model")
public class ReportModelController {

  private static final Logger log = LoggerFactory.getLogger(CreateReportModel.class);

  private final CreateReportModel createReportModel;

  private final UpdateReportModel updateReportModel;

  private final GetAllReportModels getAllReportModels;

  private final GetReportModelDetailById getReportModelDetailById;

  private final DeleteReportModelById deleteReportModelById;

  private final GetActiveReportModels getActiveReportModels;

  private final SwitchActiveReportModel switchActiveReportModel;

  private final CompileReportComponent compileReportComponent;

  public ReportModelController(
    final CreateReportModel createReportModel,
    final UpdateReportModel updateReportModel,
    final GetAllReportModels getAllReportModels,
    final GetReportModelDetailById getReportModelDetailById,
    final DeleteReportModelById deleteReportModelById,
    final GetActiveReportModels getActiveReportModels,
    final SwitchActiveReportModel switchActiveReportModel,
    final CompileReportComponent compileReportComponent
  ) {
    this.createReportModel = createReportModel;
    this.updateReportModel = updateReportModel;
    this.getAllReportModels = getAllReportModels;
    this.getReportModelDetailById = getReportModelDetailById;
    this.deleteReportModelById = deleteReportModelById;
    this.getActiveReportModels = getActiveReportModels;
    this.switchActiveReportModel = switchActiveReportModel;
    this.compileReportComponent = compileReportComponent;
  }

  @PostMapping
  @Transactional
  public ResponseEntity<ResponseBase<CreateReportModelResponse>> createReportModel(@RequestBody @Valid final CreateReportModelRequest request) {
    log.info("Chamando service CreateReportModel.");
    final CreateReportModelResponse response = this.createReportModel.execute(request);
    log.info("Retornando com sucesso.");
    return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBase.of(response));
  }

  @PutMapping
  @Transactional
  public ResponseEntity<ResponseBase<Void>> updateReportModel(@RequestBody @Valid final UpdateReportModelRequest request) {
    log.info("Chamando service CreateReportModel.");
    this.updateReportModel.execute(request);
    log.info("Retornando com sucesso.");
    return ResponseEntity.ok(ResponseBase.of());
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<GetAllReportModelsResponse>>> getAllReportModels(@RequestParam final Long idPlanModel) {
    log.info("Chamando service GetAllReportModels.");
    final List<GetAllReportModelsResponse> response = this.getAllReportModels.execute(idPlanModel);
    log.info("Retornando com sucesso.");
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<GetReportModelByIdResponse>> getReportModelById(@PathVariable final Long id) {
    log.info("Chamando service GetReportModelDetailById.");
    final GetReportModelByIdResponse response = this.getReportModelDetailById.execute(id);
    log.info("Retornando com sucesso.");
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/active")
  public ResponseEntity<ResponseBaseItens<GetAllReportModelsResponse>> getActiveReportsFromModel(@RequestParam final Long idPlanModel) {
    final List<GetAllReportModelsResponse> response = this.getActiveReportModels.execute(idPlanModel);
    return ResponseEntity.ok(ResponseBaseItens.of(response));
  }

  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseBase<Void>> deleteReportModelById(@PathVariable final Long id) {
    log.info("Chamando service DeleteReportModelById.");
    this.deleteReportModelById.execute(id);
    log.info("Retornando com sucesso.");
    return ResponseEntity.ok(ResponseBase.of());
  }

  @PatchMapping("/{idReportModel}/active")
  public ResponseEntity<ResponseBase<Boolean>> alterMainFile(
    @PathVariable final Long idReportModel
  ) {
    this.switchActiveReportModel.execute(idReportModel);
    return ResponseEntity.ok(ResponseBase.of(Boolean.TRUE));
  }

  @Transactional
  @PatchMapping("/compile/{idReport}")
  public ResponseEntity<ResponseBase<Boolean>> compileReport(@PathVariable final Long idReport) {
    this.compileReportComponent.execute(idReport);
    return ResponseEntity.ok(ResponseBase.of(Boolean.TRUE));
  }

}
