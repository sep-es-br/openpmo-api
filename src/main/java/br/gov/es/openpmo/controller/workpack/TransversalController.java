package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpackmodel.EligibleProjectDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProjectParticipationDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.TransversalProjectService;
import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@CrossOrigin
@RequestMapping("/transversal")
public class TransversalController {

  private final TransversalProjectService transversalProjectService;
  private final ICanAccessService canAccessService;

  @Autowired
  public TransversalController(
    final TransversalProjectService transversalProjectService,
    final ICanAccessService canAccessService
  ) {
    this.transversalProjectService = transversalProjectService;
    this.canAccessService = canAccessService;
  }

  @GetMapping("/programs/{idTransversalProgram}/eligible-projects")
  public ResponseEntity<ResponseBase<List<EligibleProjectDto>>> getEligibleProjects(
    @PathVariable final Long idTransversalProgram,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findEligibleProjects(idTransversalProgram)
    ));
  }

  @GetMapping("/programs/{idTransversalProgram}/projects")
  public ResponseEntity<ResponseBase<List<EligibleProjectDto>>> getIncludedProjects(
    @PathVariable final Long idTransversalProgram,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.findIncludedProjects(idTransversalProgram)
    ));
  }

  @PostMapping("/programs/{idTransversalProgram}/projects/{idProject}")
  public ResponseEntity<ResponseBase<TransversalProjectParticipationDto>> includeProject(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idProject,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.includeProject(idTransversalProgram, idProject)
    ));
  }

  @DeleteMapping("/programs/{idTransversalProgram}/projects/{idProject}")
  public ResponseEntity<ResponseBase<TransversalProjectParticipationDto>> removeProject(
    @PathVariable final Long idTransversalProgram,
    @PathVariable final Long idProject,
    @RequestHeader("Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idTransversalProgram, authorization);
    return ResponseEntity.ok(ResponseBase.of(
      this.transversalProjectService.removeProject(idTransversalProgram, idProject)
    ));
  }
}
