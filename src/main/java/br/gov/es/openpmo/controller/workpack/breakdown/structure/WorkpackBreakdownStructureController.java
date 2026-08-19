package br.gov.es.openpmo.controller.workpack.breakdown.structure;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.PlanBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownStructure;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.workpack.breakdown.structure.GetWorkpackBreakdownStructure;
import br.gov.es.openpmo.service.workpack.breakdown.structure.GetPlanBreakdownStructure;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@RequestMapping("/eap")
public class WorkpackBreakdownStructureController {

  private final GetWorkpackBreakdownStructure getWorkpackBreakdownStructure;

  private final GetPlanBreakdownStructure getPlanBreakdownStructure;

  private final CanAccessService canAccessService;

  private final ResponseHandler responseHandler;

  public WorkpackBreakdownStructureController(
    final GetWorkpackBreakdownStructure getWorkpackBreakdownStructure,
    final GetPlanBreakdownStructure getPlanBreakdownStructure,
    final CanAccessService canAccessService,
    final ResponseHandler responseHandler
  ) {
    this.getWorkpackBreakdownStructure = getWorkpackBreakdownStructure;
    this.getPlanBreakdownStructure = getPlanBreakdownStructure;
    this.canAccessService = canAccessService;
    this.responseHandler = responseHandler;
  }

  @GetMapping("/plan/{idPlan}")
  public Response<PlanBreakdownStructure> getPlanBreakdownStructure(
    @PathVariable final Long idPlan,
    @RequestParam(required = false, defaultValue = "false") final Boolean allLevels,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idPlan, authorization);
    final PlanBreakdownStructure structure = this.getPlanBreakdownStructure.execute(
      idPlan,
      allLevels,
      authorization
    );
    return this.responseHandler.success(structure);
  }

  @GetMapping("/plan/{idPlan}/workpack/{idWorkpack}")
  public Response<WorkpackBreakdownStructure> getPlanWorkpackBreakdownStructure(
    @PathVariable final Long idPlan,
    @PathVariable final Long idWorkpack,
    @RequestParam(required = false, defaultValue = "false") final Boolean allLevels,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idPlan, authorization);
    final WorkpackBreakdownStructure structure = this.getWorkpackBreakdownStructure.execute(
      idWorkpack,
      allLevels,
      idPlan,
      authorization
    );
    return this.responseHandler.success(structure);
  }

  @GetMapping("/{idWorkpack}")
  public Response<WorkpackBreakdownStructure> getWorkpackBreakdownStructure(
    @PathVariable final Long idWorkpack,
    @RequestParam(required = false, defaultValue = "false") final Boolean allLevels,
    @RequestParam(value = "id-plan") final Long idPlan,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(
      idWorkpack,
      authorization
    );
    final WorkpackBreakdownStructure structure = this.getWorkpackBreakdownStructure.execute(
      idWorkpack,
      allLevels,
      idPlan,
      authorization
    );
    return this.responseHandler.success(structure);
  }

}
