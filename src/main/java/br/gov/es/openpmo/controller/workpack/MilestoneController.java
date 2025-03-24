package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpack.ChangeMilestoneDateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.workpack.ChangeMilestoneData;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack/milestone")
public class MilestoneController {

  private final ChangeMilestoneData changeMilestoneData;

  private final CanAccessService canAccessService;

  private final TokenService tokenService;

  private final JournalCreator journalCreator;

  private final WorkpackService workpackService;

  public MilestoneController(
    ChangeMilestoneData changeMilestoneData,
    CanAccessService canAccessService,
    TokenService tokenService,
    JournalCreator journalCreator,
    WorkpackService workpackService
  ) {
    this.changeMilestoneData = changeMilestoneData;
    this.canAccessService = canAccessService;
    this.tokenService = tokenService;
    this.journalCreator = journalCreator;
    this.workpackService = workpackService;
  }

  @PatchMapping("/{idMilestone}")
  public ResponseEntity<ResponseBase<Void>> changeMilestoneData(
    @PathVariable Long idMilestone,
    @RequestBody ChangeMilestoneDateRequest request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      idMilestone,
      authorization
    );
    final Long idPerson = this.tokenService.getUserId(authorization);
    final Milestone milestone = this.changeMilestoneData.execute(
      idMilestone,
      request
    );
    this.journalCreator.edition(
      milestone,
      JournalAction.EDITED,
      idPerson
    );
    if (milestone.isReasonRequired()) {
      final String justificativa = request.getReason();
      if (justificativa == null || justificativa.trim().isEmpty()) {
        throw new NegocioException(ApplicationMessage.REASON_NOT_PRESENT);
      }
      this.journalCreator.dateChanged(
        milestone,
        JournalAction.EDITED,
        justificativa,
        milestone.getNewDate(),
        milestone.getPreviousDate(),
        idPerson
      );
    }
    this.workpackService.calculateDashboard();
    return ResponseEntity.ok(ResponseBase.success());
  }

}
