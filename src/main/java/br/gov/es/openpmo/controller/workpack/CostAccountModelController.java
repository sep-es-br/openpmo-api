package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpackmodel.params.CreateCostAccountModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.GetCostAccountModelResponse;
import br.gov.es.openpmo.dto.workpackmodel.params.UpdateCostAccountModelRequest;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.workpack.*;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api
@RestController
@CrossOrigin
@RequestMapping("/cost-account-model")
public class CostAccountModelController {

  private final GetCostAccountModelByIdPlanModel getCostAccountModelByIdPlanModel;
  private final GetCostAccountModel getCostAccountModel;
  private final CreateCostAccountModel createCostAccountModel;
  private final UpdateCostAccountModel updateCostAccountModel;
  private final DeleteCostAccountModel deleteCostAccountModel;
  private final DeletePropertyModel deletePropertyModel;
  private final CanAccessService canAccessService;

  public CostAccountModelController(
    GetCostAccountModelByIdPlanModel getCostAccountModelByIdPlanModel,
    GetCostAccountModel getCostAccountModel,
    CreateCostAccountModel createCostAccountModel,
    UpdateCostAccountModel updateCostAccountModel,
    DeleteCostAccountModel deleteCostAccountModel,
    DeletePropertyModel deletePropertyModel,
    CanAccessService canAccessService
  ) {
    this.getCostAccountModelByIdPlanModel = getCostAccountModelByIdPlanModel;
    this.getCostAccountModel = getCostAccountModel;
    this.createCostAccountModel = createCostAccountModel;
    this.updateCostAccountModel = updateCostAccountModel;
    this.deleteCostAccountModel = deleteCostAccountModel;
    this.deletePropertyModel = deletePropertyModel;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<GetCostAccountModelResponse>> getCostAccountModelByIdPlanModel(
    @RequestParam("id-plan-model") final Long idPlanModel
  ) {
    final GetCostAccountModelResponse response = this.getCostAccountModelByIdPlanModel.execute(idPlanModel);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<GetCostAccountModelResponse>> getCostAccountModel(
    @PathVariable final Long id
  ) {
    final GetCostAccountModelResponse response = this.getCostAccountModel.execute(id);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> createCostAccountModel(
    @RequestBody @Valid final CreateCostAccountModelRequest request,
    @RequestHeader("Authorization") final String authorization
  ) {
    final CostAccountModel costAccountModel = this.createCostAccountModel.execute(request, authorization);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(costAccountModel.getId())));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> updateCostAccountModel(
    @RequestBody @Valid final UpdateCostAccountModelRequest request,
    @RequestHeader("Authorization") final String authorization
  ) {
    final CostAccountModel costAccountModel = this.updateCostAccountModel.execute(request, authorization);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(costAccountModel.getId())));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseBase<Void>> deleteCostAccountModel(
    @PathVariable final Long id,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.deleteCostAccountModel.execute(id, authorization);
    return ResponseEntity.ok(ResponseBase.success());
  }

  @GetMapping("/can-delete-property/{id}")
  public ResponseEntity<ResponseBase<Boolean>> canDeletePropertyModel(
    @PathVariable final Long id,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    final boolean canDelete = this.deletePropertyModel.canDeletePropertyModel(id);
    return ResponseEntity.ok(ResponseBase.of(canDelete));
  }

  @DeleteMapping("/delete-property/{id}")
  public ResponseEntity<ResponseBase<Void>> deletePropertyModel(
    @PathVariable final Long id,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    this.deletePropertyModel.execute(id);
    return ResponseEntity.ok(ResponseBase.success());
  }

}
