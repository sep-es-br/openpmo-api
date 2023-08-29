package br.gov.es.openpmo.controller.baselines;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.baselines.*;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineDetailCCBMemberResponse;
import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.baselines.*;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/baselines")
public class BaselineController implements IBaselineController {

  private final IGetAllBaselinesService getAllBaselinesService;

  private final IGetBaselineUpdatesService getBaselineUpdatesService;

  private final ICreateBaselineService createBaselineService;

  private final ISubmitBaselineService submitBaselineService;

  private final IEditBaselineService editBaselineService;

  private final IDeleteBaselineService deleteBaselineService;

  private final ICancelBaselineService cancelBaselineService;

  private final IGetBaselineService getBaselineService;

  private final IGetBaselineAsCCBMemberViewService getBaselineAsCCBMemberViewService;

  private final IEvaluateBaselineService evaluateBaselineService;

  private final ResponseHandler responseHandler;

  private final TokenService tokenService;

  private final ICanAccessService canAccessService;

  @Autowired
  public BaselineController(
    final IGetAllBaselinesService getAllBaselinesService,
    final IGetBaselineUpdatesService getBaselineUpdatesService,
    final ICreateBaselineService createBaselineService,
    final ISubmitBaselineService submitBaselineService,
    final IEditBaselineService editBaselineService,
    final IDeleteBaselineService deleteBaselineService,
    final ICancelBaselineService cancelBaselineService,
    final IGetBaselineService getBaselineService,
    final IGetBaselineAsCCBMemberViewService getBaselineAsCCBMemberViewService,
    final IEvaluateBaselineService evaluateBaselineService,
    final ResponseHandler responseHandler,
    final TokenService tokenService,
    final ICanAccessService canAccessService
  ) {
    this.getAllBaselinesService = getAllBaselinesService;
    this.getBaselineUpdatesService = getBaselineUpdatesService;
    this.createBaselineService = createBaselineService;
    this.submitBaselineService = submitBaselineService;
    this.editBaselineService = editBaselineService;
    this.deleteBaselineService = deleteBaselineService;
    this.cancelBaselineService = cancelBaselineService;
    this.getBaselineService = getBaselineService;
    this.getBaselineAsCCBMemberViewService = getBaselineAsCCBMemberViewService;
    this.evaluateBaselineService = evaluateBaselineService;
    this.responseHandler = responseHandler;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @Override
  public Response<List<GetAllBaselinesResponse>> getAllByWorkpackId(final Long idWorkpack) {
    final List<GetAllBaselinesResponse> baselines = this.getAllBaselinesService.getAllByWorkpackId(idWorkpack);
    return this.responseHandler.success(baselines);
  }

  public Response<List<GetAllBaselinesResponse>> getAllByPersonId(
    final BaselineViewStatus status,
    final Long idFilter,
    final String term,
    final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<GetAllBaselinesResponse> baselines = this.getAllBaselinesService.getAllByPersonIdAndStatus(
      idPerson,
      idFilter,
      term,
      status
    );
    return this.responseHandler.success(baselines);
  }

  @Override
  public Response<List<UpdateResponse>> getUpdates(final Long idWorkpack) {
    final List<UpdateResponse> updates = this.getBaselineUpdatesService.getUpdates(idWorkpack);
    return this.responseHandler.success(updates);
  }

  @Override
  public Response<EntityDto> create(
    final IncludeBaselineRequest request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final Long idBaseline = this.createBaselineService.create(
      request,
      idPerson
    );
    return this.responseHandler.success(new EntityDto(idBaseline));
  }

  @Override
  public Response<Void> submit(
    final Long idBaseline,
    final SubmitBaselineRequest request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.submitBaselineService.submit(
      idBaseline,
      request,
      idPerson
    );
    return this.responseHandler.success();
  }

  @Override
  public Response<Void> edit(
    final Long idBaseline,
    final EditDraftBaselineRequest request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(
      idBaseline,
      authorization
    );
    this.editBaselineService.edit(
      idBaseline,
      request
    );
    return this.responseHandler.success();
  }

  @Override
  public Response<Void> delete(
    final Long idBaseline,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idBaseline,
      authorization
    );
    this.deleteBaselineService.delete(idBaseline);
    return this.responseHandler.success();
  }

  @Override
  public Response<EntityDto> submitCancelling(
    final SubmitCancellingRequest request,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long personId = this.tokenService.getUserId(authorization);
    final EntityDto baselineProposed = this.cancelBaselineService.submit(
      request,
      personId
    );
    return this.responseHandler.success(baselineProposed);
  }

  @Override
  public Response<BaselineDetailResponse> getById(
    final Long idBaseline,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(
      idBaseline,
      authorization
    );
    final BaselineDetailResponse response = this.getBaselineService.getById(idBaseline);
    return this.responseHandler.success(response);
  }

  @Override
  public Response<BaselineDetailCCBMemberResponse> getBaselineByIdAsCCBMemberView(
    @RequestHeader(name = "Authorization") final String authorization,
    final Long idBaseline
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final BaselineDetailCCBMemberResponse response = this.getBaselineAsCCBMemberViewService.getById(
      idBaseline,
      idPerson
    );
    return this.responseHandler.success(response);
  }

  @Override
  public Response<Void> evaluateBaseline(
    @RequestHeader(name = "Authorization") final String authorization,
    final Long idBaseline,
    final BaselineEvaluationRequest request
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    this.evaluateBaselineService.evaluate(
      idPerson,
      idBaseline,
      request
    );
    return this.responseHandler.success();
  }

}
