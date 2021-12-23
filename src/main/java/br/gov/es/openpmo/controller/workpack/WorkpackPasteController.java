package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.workpack.WorkpackPasteResponse;
import br.gov.es.openpmo.service.workpack.CheckPasteWorkpackService;
import br.gov.es.openpmo.service.workpack.PasteToWorkpackService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack")
public class WorkpackPasteController {

  private final CheckPasteWorkpackService checkPasteWorkpackService;

  private final PasteToWorkpackService pasteToWorkpackService;

  private final ResponseHandler responseHandler;

  @Autowired
  public WorkpackPasteController(
    final CheckPasteWorkpackService checkPasteWorkpackService,
    final PasteToWorkpackService pasteToWorkpackService,
    final ResponseHandler responseHandler
  ) {
    this.checkPasteWorkpackService = checkPasteWorkpackService;
    this.pasteToWorkpackService = pasteToWorkpackService;
    this.responseHandler = responseHandler;
  }

  @GetMapping("/{idWorkpack}/check-paste/{idWorkpackModelTo}")
  public Response<WorkpackPasteResponse> checksIfCanPasteWorkpack(
    @PathVariable final Long idWorkpack,
    @PathVariable final Long idWorkpackModelTo,
    @RequestParam final Long idWorkpackModelFrom
  ) {
    final WorkpackPasteResponse response = this.checkPasteWorkpackService.checksIfCanPasteWorkpack(
      idWorkpack,
      idWorkpackModelTo,
      idWorkpackModelFrom
    );

    return this.responseHandler.success(response);
  }

  @Transactional
  @PostMapping("/{idWorkpack}/paste-to/{idWorkpackModelTo}")
  public Response<Void> pastesWorkpackTo(
    @PathVariable final Long idWorkpack,
    @RequestParam final Long idPlanFrom,
    @RequestParam final Long idPlanTo,
    @RequestParam final Long idWorkpackModelFrom,
    @PathVariable final Long idWorkpackModelTo,
    @RequestParam(required = false) final Long idParentFrom,
    @RequestParam(required = false) final Long idParentTo
  ) {
    this.pasteToWorkpackService.pastesWorkpackTo(
      idWorkpack,
      idPlanFrom,
      idParentFrom,
      idWorkpackModelFrom,
      idPlanTo,
      idParentTo,
      idWorkpackModelTo
    );

    return this.responseHandler.success();
  }

}
