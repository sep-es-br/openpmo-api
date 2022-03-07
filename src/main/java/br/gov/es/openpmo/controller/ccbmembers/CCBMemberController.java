package br.gov.es.openpmo.controller.ccbmembers;

import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;
import br.gov.es.openpmo.dto.ccbmembers.CCBMemberResponse;
import br.gov.es.openpmo.service.ccbmembers.*;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/ccb-members")
public class CCBMemberController implements ICCBMemberController {

  private final IGetAllCCBMemberService getAllService;

  private final ICreateCCBMemberRelationshipService createRelationshipService;

  private final IUpdateCCBMemberRelationshipService updateCCBMemberRelationshipService;

  private final IGetByIdCCBMemberService getByIdCCBMemberService;

  private final IDeleteCCBMemberService deleteCCBMemberService;

  private final ResponseHandler controllerHelper;

  @Autowired
  public CCBMemberController(
      final IGetAllCCBMemberService getAllService,
      final ICreateCCBMemberRelationshipService createRelationshipService,
      final IUpdateCCBMemberRelationshipService updateCCBMemberRelationshipService,
      final IGetByIdCCBMemberService getByIdCCBMemberService,
      final IDeleteCCBMemberService deleteCCBMemberService,
      final ResponseHandler controllerHelper
  ) {
    this.getAllService = getAllService;
    this.createRelationshipService = createRelationshipService;
    this.updateCCBMemberRelationshipService = updateCCBMemberRelationshipService;
    this.getByIdCCBMemberService = getByIdCCBMemberService;
    this.deleteCCBMemberService = deleteCCBMemberService;
    this.controllerHelper = controllerHelper;
  }

  @Override
  public Response<List<CCBMemberResponse>> getAll(final Long workpackId) {
    final List<CCBMemberResponse> ccbMemberResponses = this.getAllService.getAll(workpackId);
    return this.controllerHelper.success(ccbMemberResponses);
  }

  public Response<CCBMemberResponse> getCCBMember(
      final Long idPerson,
      final Long idWorkpack,
      final Long idPlan
  ) {
    final CCBMemberResponse ccbMemberResponse = this.getByIdCCBMemberService.getById(idPerson, idWorkpack, idPlan);
    return this.controllerHelper.success(ccbMemberResponse);
  }

  @Override
  public Response<Void> createRelationship(final CCBMemberRequest request) {
    this.createRelationshipService.createRelationship(request);
    return this.controllerHelper.success();
  }

  @Override
  public Response<Void> updateRelationship(final CCBMemberRequest request) {
    this.updateCCBMemberRelationshipService.updateRelationship(request);
    return this.controllerHelper.success();
  }

  @Override
  public Response<Void> delete(final Long idPerson, final Long idWorkpack) {
    this.deleteCCBMemberService.delete(idPerson, idWorkpack);
    return this.controllerHelper.success();
  }

}
