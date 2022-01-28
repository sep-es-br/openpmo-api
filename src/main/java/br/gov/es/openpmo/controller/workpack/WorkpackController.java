package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.dto.workpack.ResponseBaseWorkpack;
import br.gov.es.openpmo.dto.workpack.ResponseBaseWorkpackDetail;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.workpack.IDeliverableEndManagement;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.service.workpack.WorkpackService.compare;
import static br.gov.es.openpmo.service.workpack.WorkpackService.getValueProperty;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack")
public class WorkpackController {

  private static final String SUCESSO = "Success";

  private final WorkpackService workpackService;

  private final TokenService tokenService;

  private final WorkpackPermissionVerifier workpackPermissionVerifier;

  private final JournalCreator journalCreator;

  private final IDeliverableEndManagement deliverableEndManagement;

  @Autowired
  public WorkpackController(
    final WorkpackService workpackService,
    final TokenService tokenService,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final JournalCreator journalCreator,
    final IDeliverableEndManagement deliverableEndManagement
  ) {
    this.workpackService = workpackService;
    this.tokenService = tokenService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.journalCreator = journalCreator;
    this.deliverableEndManagement = deliverableEndManagement;
  }

  @GetMapping
  public ResponseEntity<ResponseBaseWorkpack> indexBase(
    @RequestParam("id-plan") final Long idPlan,
    @RequestParam(value = "id-plan-model", required = false) final Long idPlanModel,
    @RequestParam(value = "id-workpack-model", required = false) final Long idWorkpackModel,
    @RequestParam(value = "idFilter", required = false) final Long idFilter,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idUser = this.tokenService.getUserId(authorization);

    final List<Workpack> workpacks = this.findAllWorkpacks(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idFilter
    );

    if(workpacks.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    final List<WorkpackDetailDto> workpackDetailDtos = workpacks.stream()
      .map(workpack -> this.mapToWorkpackDetailDto(workpack, idWorkpackModel))
      .collect(Collectors.toList());
    final List<WorkpackDetailDto> verify = this.workpackPermissionVerifier.verify(workpackDetailDtos, idUser, idPlan);

    return verify.isEmpty()
      ? ResponseEntity.noContent().build()
      : ResponseEntity.ok(new ResponseBaseWorkpack().setData(verify).setMessage(SUCESSO).setSuccess(true));
  }

  private List<Workpack> findAllWorkpacks(final Long idPlan, final Long idPlanModel, final Long idWorkPackModel, final Long idFilter) {
    return this.workpackService.findAll(idPlan, idPlanModel, idWorkPackModel, idFilter);
  }

  private WorkpackDetailDto mapToWorkpackDetailDto(final Workpack workpack, final Long idWorkpackModel) {
    final WorkpackDetailDto itemDetail = this.workpackService.getWorkpackDetailDto(workpack);
    itemDetail.applyLinkedStatus(workpack, idWorkpackModel);
    return itemDetail;
  }

  @GetMapping("/parent")
  public ResponseEntity<ResponseBaseWorkpack> indexBase(
    @RequestParam("id-plan") final Long idPlan,
    @RequestParam("id-workpack-parent") final Long idWorkpackParent,
    @RequestParam(value = "id-plan-model", required = false) final Long idPlanModel,
    @RequestParam(value = "id-workpack-model", required = false) final Long idWorkpackModel,
    @RequestParam(value = "idFilter", required = false) final Long idFilter,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idUser = this.tokenService.getUserId(authorization);

    final List<Workpack> workpacks = this.workpackService.findAllUsingParent(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idWorkpackParent,
      idFilter
    );

    if(isNotNull(idWorkpackModel)) {
      final WorkpackModel workpackModel = this.workpackService.getWorkpackModelById(idWorkpackModel);
      if(workpackModelHasSortBy(workpackModel)) {
        sortWorkpacks(workpacks, workpackModel);
      }
    }

    final List<WorkpackDetailDto> workpackList = workpacks.stream()
      .map(workpack -> this.mapToWorkpackDetailDto(workpack, idWorkpackModel))
      .collect(Collectors.toList());

    if(workpackList.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    final List<WorkpackDetailDto> verify = this.workpackPermissionVerifier.verify(workpackList, idUser, idPlan);

    return verify.isEmpty()
      ? ResponseEntity.noContent().build()
      : ResponseEntity.ok(new ResponseBaseWorkpack().setData(verify).setMessage(SUCESSO).setSuccess(true));
  }

  private static <T> boolean isNotNull(final T obj) {
    return Objects.nonNull(obj);
  }

  private static boolean workpackModelHasSortBy(final WorkpackModel workpackModel) {
    return workpackModel != null && workpackModel.getSortBy() != null;
  }

  private static void sortWorkpacks(final List<? extends Workpack> workpacks, final WorkpackModel workpackModel) {
    workpacks.sort((a, b) -> compare(
      getValueProperty(a, workpackModel.getSortBy()),
      getValueProperty(b, workpackModel.getSortBy())
    ));
  }

  @GetMapping("/{idWorkpack}")
  public ResponseEntity<ResponseBaseWorkpackDetail> find(
    @PathVariable final Long idWorkpack,
    @RequestParam(value = "id-plan", required = false) final Long idPlan,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idUser = this.tokenService.getUserId(authorization);
    final Workpack workpack = this.workpackService.findByIdWithParent(idWorkpack);

    final WorkpackDetailDto workpackDetailDto = this.workpackService.getWorkpackDetailDto(workpack, idPlan);

    final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchPermissions(
      workpackDetailDto,
      idUser,
      idPlan
    );

    workpackDetailDto.setPermissions(permissions);

    final ResponseBaseWorkpackDetail success = new ResponseBaseWorkpackDetail()
      .setData(workpackDetailDto)
      .setMessage(SUCESSO)
      .setSuccess(true);

    return ResponseEntity.ok(success);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @RequestBody @Valid final WorkpackParamDto request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Workpack workpack = this.workpackService.getWorkpack(request);
    final EntityDto response = this.workpackService.save(workpack, request.getIdPlan(), request.getIdParent());

    final Long idPerson = this.tokenService.getUserId(authorization);
    this.journalCreator.edition(workpack, JournalAction.CREATED, idPerson);

    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final WorkpackParamDto workpackParamDto,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Workpack workpack = this.workpackService.update(this.workpackService.getWorkpack(workpackParamDto));
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.journalCreator.edition(workpack, JournalAction.EDITED, idPerson);
    return ResponseEntity.ok(ResponseBase.of(EntityDto.of(workpack)));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<Void> cancel(
    @PathVariable("id") final Long idWorkpack,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Workpack workpack = this.workpackService.cancel(idWorkpack);
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.journalCreator.edition(workpack, JournalAction.CANCELLED, idPerson);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<Void> restore(@PathVariable("id") final Long idWorkpack) {
    this.workpackService.restore(idWorkpack);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    final Workpack workpack = this.workpackService.findById(id);
    this.workpackService.delete(workpack);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/end-deliverable-management/{id-deliverable}")
  public ResponseEntity<ResponseBase<Void>> endDeliverableManagement(
    @PathVariable("id-deliverable") final Long idDeliverable,
    @RequestBody final EndDeliverableManagementRequest request
  ) {

    this.deliverableEndManagement.execute(
      idDeliverable,
      request
    );

    return ResponseEntity.ok(ResponseBase.success());
  }

}
