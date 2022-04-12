package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpackmodel.ResponseBaseWorkpackModel;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelCompletedUpdateRequest;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDto;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.dto.workpackreuse.ReusableWorkpackModelHierarchyDto;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.workpack.ParentWorkpackTypeVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackModelDeleteService;
import br.gov.es.openpmo.service.workpack.WorkpackModelPatchCompletedStatus;
import br.gov.es.openpmo.service.workpack.WorkpackModelReuseService;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail.success;
import static br.gov.es.openpmo.utils.WorkpackModelInstanceType.TYPE_NAME_MODEL_PROGRAM;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack-model")
public class WorkpackModelController {

  private final WorkpackModelService workpackModelService;

  private final WorkpackModelReuseService workpackModelReuseService;

  private final ParentWorkpackTypeVerifier projectParentVerifier;

  private final WorkpackModelDeleteService deleteService;
  private final WorkpackModelPatchCompletedStatus patchCompletedStatus;

  @Autowired
  public WorkpackModelController(
    final WorkpackModelService workpackModelService,
    final WorkpackModelReuseService workpackModelReuseService,
    final ParentWorkpackTypeVerifier projectParentVerifier,
    final WorkpackModelDeleteService deleteService,
    final WorkpackModelPatchCompletedStatus patchCompletedStatus
  ) {
    this.workpackModelService = workpackModelService;
    this.workpackModelReuseService = workpackModelReuseService;
    this.projectParentVerifier = projectParentVerifier;
    this.deleteService = deleteService;
    this.patchCompletedStatus = patchCompletedStatus;
  }

  @GetMapping
  public ResponseEntity<ResponseBaseWorkpackModel> indexBase(
    @RequestParam("id-plan-model") final Long idPlanModel
  ) {
    final List<WorkpackModelDto> worList = new ArrayList<>();
    final List<WorkpackModel> list = this.workpackModelService.findAll(idPlanModel);

    list.forEach(w -> worList.add(this.workpackModelService.getWorkpackModelDto(w)));

    if(worList.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBaseWorkpackModel base = new ResponseBaseWorkpackModel()
      .setData(worList)
      .setMessage("Sucesso")
      .setSuccess(true);
    return ResponseEntity.status(200).body(base);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBaseWorkpackModelDetail> find(@PathVariable final Long id) {
    final WorkpackModel workpackModel = this.workpackModelService.findById(id);
    if(!CollectionUtils.isEmpty(workpackModel.getProperties())) {
      workpackModel.setProperties(new LinkedHashSet<>(workpackModel.getProperties().stream().sorted(Comparator.comparing(
        PropertyModel::getSortIndex)).collect(
        Collectors.toCollection(LinkedHashSet::new))));
    }
    final WorkpackModelDetailDto modelDetailDto = this.workpackModelService.getWorkpackModelDetailDto(workpackModel);
    return ResponseEntity.ok(success(modelDetailDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @RequestBody @Valid final WorkpackModelParamDto workpackModelParamDto
  ) {
    WorkpackModel workpackModel = this.workpackModelService.getWorkpackModel(workpackModelParamDto);
    workpackModel = this.workpackModelService.save(workpackModel, workpackModelParamDto.getIdParent());
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(workpackModel.getId())));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final WorkpackModelParamDto workpackModelParamDto
  ) {
    WorkpackModel workpackModel = this.workpackModelService.getWorkpackModel(workpackModelParamDto);
    workpackModel = this.workpackModelService.update(workpackModel);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(workpackModel.getId())));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id, @RequestParam(required = false) final Long idParent) {
    final WorkpackModel workpackModel = this.workpackModelService.findById(id);
    final WorkpackModel workpackModelParent = idParent != null ? this.workpackModelService.findById(idParent) : null;
    this.deleteService.delete(workpackModel, workpackModelParent);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}/parent-project")
  public ResponseEntity<ResponseBase<Boolean>> parentProject(@PathVariable final Long id) {
    final Boolean isParentProject = this.projectParentVerifier.verify(
      id,
      TYPE_NAME_MODEL_PROGRAM::isTypeOf
    );
    final ResponseBase<Boolean> response = new ResponseBase<Boolean>().setData(isParentProject).setSuccess(true);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/can-delete-property/{id}")
  public ResponseEntity<ResponseBase<Boolean>> canDelete(@PathVariable final Long id) {
    final Boolean canDelete = this.workpackModelService.isCanDeleteProperty(id);
    final ResponseBase<Boolean> response = new ResponseBase<Boolean>().setData(canDelete).setSuccess(true);
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/delete-property/{id}")
  public ResponseEntity<Void> deleteProperty(@PathVariable final Long id) {
    this.workpackModelService.deleteProperty(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{idWorkpackModelParent}/reuse/{idWorkpackModel}")
  public ResponseEntity<ResponseBase<WorkpackModelDetailDto>> reuseWorkpack(
    @PathVariable final Long idWorkpackModelParent,
    @PathVariable final Long idWorkpackModel
  ) {
    final WorkpackModel workpackReused = this.workpackModelReuseService.reuse(
      idWorkpackModelParent,
      idWorkpackModel
    );
    final WorkpackModelDetailDto workpackDetail = this.workpackModelService
      .getWorkpackModelDetailDto(workpackReused);
    return ResponseEntity.ok(ResponseBase.of(workpackDetail));
  }

  @GetMapping("/{idWorkpackModel}/reusable")
  public ResponseEntity<ResponseBase<List<ReusableWorkpackModelHierarchyDto>>> findReusable(
    @PathVariable final Long idWorkpackModel,
    @RequestParam("id-plan-model") final Long idPlanModel
  ) {
    final List<ReusableWorkpackModelHierarchyDto> workpacks = this.workpackModelReuseService.findWorkpackModelReusable(
      idWorkpackModel,
      idPlanModel
    );
    return ResponseEntity.ok(ResponseBase.of(workpacks));
  }

  @PatchMapping("/{id-workpack-model}")
  public ResponseEntity<ResponseBase<Void>> patchCompletedStatus(
    @PathVariable("id-workpack-model") final Long idWorkpackModel,
    @Valid @RequestBody final WorkpackModelCompletedUpdateRequest request
  ) {
    this.patchCompletedStatus.patch(request, idWorkpackModel);
    return ResponseEntity.ok(ResponseBase.success());
  }
}