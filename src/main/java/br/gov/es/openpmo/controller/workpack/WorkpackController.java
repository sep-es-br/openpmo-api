package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskResultDto;
import br.gov.es.openpmo.dto.dashboards.RiskWorkpackDto;
import br.gov.es.openpmo.dto.dashboards.RiskDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.dto.workpack.ResponseBaseWorkpack;
import br.gov.es.openpmo.dto.workpack.ResponseBaseWorkpackDetail;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailParentDto;
import br.gov.es.openpmo.dto.workpack.WorkpackHasChildrenResponse;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import br.gov.es.openpmo.service.actors.IsFavoritedByService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import br.gov.es.openpmo.service.completed.IDeliverableEndManagementService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.WorkpackHasChildren;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack")
public class WorkpackController {

  private static final String SUCESSO = "Success";

  private final ResponseHandler responseHandler;

  private final WorkpackService workpackService;

  private final TokenService tokenService;

  private final WorkpackPermissionVerifier workpackPermissionVerifier;

  private final JournalCreator journalCreator;

  private final ICompleteWorkpackService completeDeliverableService;

  private final IDeliverableEndManagementService deliverableEndManagementService;

  private final ICanAccessService canAccessService;

  private final ICanAccessData canAccessData;

  private final WorkpackHasChildren workpackHasChildren;

  private final IsFavoritedByService isFavoritedByService;

  private final DashboardMilestoneRepository dashboardMilestoneRepository;

  private final RiskRepository riskRepository;

  @Autowired
  public WorkpackController(
    final ResponseHandler responseHandler,
    final WorkpackService workpackService,
    final TokenService tokenService,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final JournalCreator journalCreator,
    final ICompleteWorkpackService completeDeliverableService,
    final IDeliverableEndManagementService deliverableEndManagementService,
    final ICanAccessService canAccessService,
    final ICanAccessData canAccessData,
    final WorkpackHasChildren workpackHasChildren,
    final IsFavoritedByService isFavoritedByService,
    final DashboardMilestoneRepository dashboardMilestoneRepository,
    final RiskRepository riskRepository
  ) {
    this.responseHandler = responseHandler;
    this.workpackService = workpackService;
    this.tokenService = tokenService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.journalCreator = journalCreator;
    this.completeDeliverableService = completeDeliverableService;
    this.deliverableEndManagementService = deliverableEndManagementService;
    this.canAccessService = canAccessService;
    this.canAccessData = canAccessData;
    this.workpackHasChildren = workpackHasChildren;
    this.isFavoritedByService = isFavoritedByService;
    this.dashboardMilestoneRepository = dashboardMilestoneRepository;
    this.riskRepository = riskRepository;
  }

  @GetMapping
  public ResponseEntity<ResponseBaseWorkpack> indexBase(
    @RequestParam("id-plan") final Long idPlan,
    @RequestParam(value = "id-plan-model", required = false) final Long idPlanModel,
    @RequestParam(value = "id-workpack-model", required = false) final Long idWorkpackModel,
    @RequestParam(value = "idFilter", required = false) final Long idFilter,
    @RequestParam(required = false) final String term,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(
      idPlan,
      authorization
    );

    final List<Workpack> workpacks = this.workpackService.findAll(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idFilter,
      term
    );

    final List<WorkpackDetailParentDto> response = this.getResponseWorkpackDetailParentDto(workpacks, authorization, idWorkpackModel);

    if (response.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(new ResponseBaseWorkpack().setData(response).setMessage(SUCESSO).setSuccess(true));
  }

  private List<WorkpackDetailParentDto> getResponseWorkpackDetailParentDto(final List<Workpack> workpacks, final String authorization
      , final Long idWorkpackModel) {
    List<Long> ids = workpacks.stream().map(Workpack::getId).collect(Collectors.toList());
    final List<MilestoneDateDto> milestoneDates = this.dashboardMilestoneRepository.findByParentIds(ids);
    final List<RiskWorkpackDto> risks = this.riskRepository.findByWorkpackIds(ids);
    return  workpacks.stream()
                     .filter(workpack -> this.canAccessData.execute(workpack.getId(), authorization).canReadResource())
                     .map(workpack -> this.mapToWorkpackDetailParentDto(workpack, idWorkpackModel, milestoneDates, risks))
                     .collect(Collectors.toList());
  }

  @GetMapping("/parent")
  public ResponseEntity<ResponseBaseWorkpack> indexBase(
    @RequestParam("id-plan") final Long idPlan,
    @RequestParam("id-workpack-parent") final Long idWorkpackParent,
    @RequestParam(value = "id-plan-model", required = false) final Long idPlanModel,
    @RequestParam(value = "id-workpack-model", required = false) final Long idWorkpackModel,
    @RequestParam(value = "idFilter", required = false) final Long idFilter,
    @RequestParam(required = false) final String term,
    @RequestParam(value = "workpackLinked", required = false) final boolean workpackLinked,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(
      idPlan,
      authorization
    );
    final List<Workpack> workpacks = this.workpackService.findAllUsingParent(
      idPlan,
      idPlanModel,
      idWorkpackModel,
      idWorkpackParent,
      idFilter,
      term,
      workpackLinked
    );

    final List<WorkpackDetailParentDto> verify = getResponseWorkpackDetailParentDto(workpacks, authorization, idWorkpackModel);

    if (verify.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(new ResponseBaseWorkpack().setData(verify).setMessage(SUCESSO).setSuccess(true));
  }

  @GetMapping("/{idWorkpack}")
  public ResponseEntity<ResponseBaseWorkpackDetail> find(
    @PathVariable final Long idWorkpack,
    @RequestParam(value = "id-plan", required = false) final Long idPlan,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(
      idWorkpack,
      authorization
    );
    final Long idPerson = this.tokenService.getUserId(authorization);

    final Optional<Workpack> maybeWorkpack = this.workpackService.maybeFindByIdWithParent(idWorkpack);

    if (!maybeWorkpack.isPresent()) {
      return ResponseEntity.ok(ResponseBaseWorkpackDetail.of(null));
    }

    final Workpack workpack = maybeWorkpack.get();

    final WorkpackDetailDto workpackDetailDto = this.workpackService.getWorkpackDetailDto(workpack, idPlan);

    final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchPermissions(
      idPerson,
      idPlan,
      idWorkpack
    );

    final boolean isFavoritedBy = this.isFavoritedByService.isFavoritedBy(
      idWorkpack,
      idPlan,
      idPerson
    );

    workpackDetailDto.setPermissions(permissions);
    workpackDetailDto.setFavoritedBy(isFavoritedBy);
    return ResponseEntity.ok(ResponseBaseWorkpackDetail.of(workpackDetailDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @RequestBody @Valid final WorkpackParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      Arrays.asList(request.getIdPlan(), request.getIdParent()),
      authorization
    );

    final Workpack workpack = this.workpackService.criarWorkpack(request);

    final EntityDto response = EntityDto.of(workpack);

    final Long idPerson = this.tokenService.getUserId(authorization);

    this.journalCreator.edition(
      workpack,
      JournalAction.CREATED,
      idPerson
    );

    if (workpack instanceof Milestone) {
      this.workpackService.calculateDashboard();
    }
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PutMapping
  @Transactional
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final WorkpackParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      request.getId(),
      authorization
    );
    final Long idPerson = this.tokenService.getUserId(authorization);
    final String justificativa = request.getReason();
    final Workpack workpack = this.workpackService.update(this.workpackService.getWorkpack(request));
    this.journalCreator.edition(
      workpack,
      JournalAction.EDITED,
      idPerson
    );
    final boolean isMilestone = workpack instanceof Milestone;
    if (isMilestone && workpack.isReasonRequired()) {
      if (justificativa == null || justificativa.trim().isEmpty()) {
        throw new NegocioException(ApplicationMessage.REASON_NOT_PRESENT);
      }
      this.journalCreator.dateChanged(
        workpack,
        JournalAction.EDITED,
        justificativa,
        workpack.getNewDate(),
        workpack.getPreviousDate(),
        idPerson
      );
    }
    if (isMilestone) {
      this.workpackService.calculateDashboard();
    }
    return ResponseEntity.ok(ResponseBase.of(EntityDto.of(workpack)));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<Void> cancel(
    @PathVariable("id") final Long idWorkpack,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idWorkpack,
      authorization
    );
    final Workpack workpack = this.workpackService.cancel(idWorkpack);
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.journalCreator.edition(
      workpack,
      JournalAction.CANCELLED,
      idPerson
    );
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{id}/restore")
  public ResponseEntity<Void> restore(
    @PathVariable("id") final Long idWorkpack,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idWorkpack,
      authorization
    );
    this.workpackService.restore(idWorkpack);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{idWorkpack}")
  public ResponseEntity<Void> delete(
    @PathVariable final Long idWorkpack,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idWorkpack,
      authorization
    );
    final Workpack workpack = this.workpackService.findById(idWorkpack);
    this.workpackService.delete(workpack);
    if (workpack instanceof Milestone) {
      this.workpackService.calculateDashboard();
    }
    return ResponseEntity.ok().build();
  }

  @Transactional
  @PatchMapping("/complete-deliverable/{id-deliverable}")
  public Response<Void> completeDeliverable(
    @PathVariable("id-deliverable") final Long idDeliverable,
    @RequestBody final CompleteWorkpackRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idDeliverable,
      authorization
    );
    this.completeDeliverableService.apply(
      idDeliverable,
      request
    );
    return this.responseHandler.success();
  }

  @Transactional
  @PatchMapping("/end-deliverable-management/{id-deliverable}")
  public Response<Void> endDeliverableManagement(
    @PathVariable("id-deliverable") final Long idDeliverable,
    @RequestBody final EndDeliverableManagementRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idDeliverable,
      authorization
    );
    this.deliverableEndManagementService.apply(
      idDeliverable,
      request
    );
    return this.responseHandler.success();
  }

  // TODO: revisar url desse endpoint
  @GetMapping("/{id-workpack}/has-children")
  public ResponseEntity<ResponseBase<WorkpackHasChildrenResponse>> hasChildren(
    @Authorization final String authorization,
    @PathVariable("id-workpack") final Long idWorkpack
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);

    final WorkpackHasChildrenResponse response = this.workpackHasChildren.execute(idWorkpack, authorization);

    return ResponseEntity.ok(ResponseBase.of(response));
  }

  private WorkpackDetailParentDto mapToWorkpackDetailParentDto(final Workpack workpack, final Long idWorkpackModel,
                                                               List<MilestoneDateDto> milestoneDates, List<RiskWorkpackDto> risks) {
    final WorkpackDetailParentDto itemDetail = this.workpackService.getWorkpackDetailParentDto(workpack);
    itemDetail.applyLinkedStatus(workpack, idWorkpackModel);
    DashboardMonthDto monthDto = workpackService.getDashboardMonthDto(workpack);
    itemDetail.setDashboard(monthDto);
    final List<MilestoneDateDto> milestones = milestoneDates.stream()
          .filter(m -> m.getIdWorkpack().equals(workpack.getId())).collect(Collectors.toList());
    final List<MilestoneDto> milestoneDtos = MilestoneDto.setMilestonesOfMiletonesDate(milestones);
    itemDetail.setMilestone(MilestoneResultDto.of(milestoneDtos));
    final List<Risk> risksWorkpack = risks.stream().filter(r -> workpack.getId().equals(r.getIdWorkpack()))
                                          .map(RiskWorkpackDto::getRisk).collect(Collectors.toList());
    final List<RiskDto> riskDtos = RiskDto.of(risksWorkpack);
    itemDetail.setRisk(RiskResultDto.of(riskDtos));
    return itemDetail;
  }

}
