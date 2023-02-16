package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.workpack.ResponseBaseWorkpackDetail;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.WorkpackLinkService;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackSharedService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.OPERATION_SUCCESS;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack")
public class WorkpackLinkController {

  private final WorkpackSharedService workpackSharedService;
  private final WorkpackLinkService workpackLinkService;
  private final TokenService tokenService;
  private final WorkpackPermissionVerifier workpackPermissionVerifier;
  private final ICanAccessService canAccessService;

  @Autowired
  public WorkpackLinkController(
      final WorkpackSharedService workpackSharedService,
      final WorkpackLinkService workpackLinkService,
      final TokenService tokenService,
      final WorkpackPermissionVerifier workpackPermissionVerifier,
      final ICanAccessService canAccessService) {
    this.workpackSharedService = workpackSharedService;
    this.workpackLinkService = workpackLinkService;
    this.tokenService = tokenService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.canAccessService = canAccessService;
  }

  @GetMapping("/can-be-linked")
  public ResponseEntity<ResponseBaseItens<ComboDto>> getAllWorkpackCanBeLinked(
      @RequestParam("id-workpack-model") final Long idworkpackModel,
      @RequestHeader("Authorization") final String authorization) {

    this.canAccessService.ensureCanReadResource(idworkpackModel.longValue(), authorization);
    final List<ComboDto> response = this.workpackSharedService.getSharedWorkpacks(idworkpackModel);
    return ResponseEntity.ok(ResponseBaseItens.of(response));
  }

  @PostMapping("/{id-workpack}/link/to/workpackModel/{id-workpack-model}")
  public ResponseEntity<ResponseBaseItens<ComboDto>> createLink(
      @PathVariable("id-workpack") final Long idWorkpack,
      @PathVariable("id-workpack-model") final Long idworkpackModel,
      @RequestParam("id-plan") final Long idPlan,
      @RequestParam(value = "id-parent", required = false) final Long idParent,
      @RequestHeader("Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(idParent, authorization);
    this.workpackLinkService.linkWorkpackToWorkpackModel(idWorkpack, idworkpackModel, idPlan, idParent);
    return ResponseEntity.ok(ResponseBaseItens.of(null));
  }

  @PostMapping("/{id-workpack}/unlink/to/workpackModel/{id-workpack-model}")
  public ResponseEntity<ResponseBaseItens<Void>> unlinkWorkpack(
      @PathVariable("id-workpack") final Long idWorkpack,
      @PathVariable("id-workpack-model") final Long idWorkpackModel,
      @RequestParam("id-plan") final Long idPlan,
      @RequestHeader("Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
    this.workpackLinkService.unlink(idWorkpack, idWorkpackModel, idPlan);
    return ResponseEntity.ok(ResponseBaseItens.empty());
  }

  @GetMapping("/linked/{id-workpack}")
  public ResponseEntity<ResponseBaseWorkpackDetail> getById(
      @PathVariable("id-workpack") final Long idWorkpack,
      @RequestParam("id-workpack-model") final Long idWorpackModelLinked,
      @RequestParam("id-plan") final Long idPlan,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);

    final Long idUser = this.tokenService.getUserId(authorization);

    final WorkpackDetailDto response = this.workpackLinkService.getByIdWorkpack(
        idWorkpack,
        idWorpackModelLinked);

    response.setPermissions(this.workpackPermissionVerifier.fetchPermissions(
        idUser,
        idPlan,
        response.getId()));

    return ResponseEntity.ok(
        new ResponseBaseWorkpackDetail()
            .setData(response)
            .setMessage(OPERATION_SUCCESS)
            .setSuccess(true));
  }

}
