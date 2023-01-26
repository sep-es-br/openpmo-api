package br.gov.es.openpmo.controller.issue;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.issue.IssueCardDto;
import br.gov.es.openpmo.dto.issue.IssueCreateDto;
import br.gov.es.openpmo.dto.issue.IssueDetailDto;
import br.gov.es.openpmo.dto.issue.IssueFromRiskDto;
import br.gov.es.openpmo.dto.issue.IssueUpdateDto;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.issue.IssueService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api
@RestController
@RequestMapping("/issues")
public class IssueController {

  private final IssueService service;

  private final TokenService tokenService;

  private final ICanAccessService canAccessService;

  public IssueController(
      final IssueService service,
      final TokenService tokenService,
      final ICanAccessService canAccessService) {
    this.service = service;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @PostMapping("/create-from-risk")
  public ResponseEntity<ResponseBase<EntityDto>> createIssueFromRisk(
      @RequestBody final IssueFromRiskDto request,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(request.getIdRisk(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final EntityDto response = this.service.createIssueFromRisk(request.getIdRisk(), idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(
      @Valid @RequestBody final IssueCreateDto request,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final EntityDto response = this.service.create(request, idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<IssueDetailDto>> findById(@PathVariable final Long id) {
    final IssueDetailDto response = this.service.findIssueDetailById(id);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<IssueCardDto>>> findAll(
      @RequestParam("id-workpack") final Long idWorkpack,
      @RequestParam(required = false) final Long idRisk,
      @RequestParam(required = false) final Long idFilter,
      @RequestHeader("Authorization") final String authorization) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final List<IssueCardDto> response = this.service.findAllAsCardDto(idWorkpack, idRisk, idFilter, idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<IssueDetailDto>> update(
      @Valid @RequestBody final IssueUpdateDto request,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(request.getId(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final IssueDetailDto response = this.service.update(request, idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @DeleteMapping("/{issueId}")
  public ResponseEntity<Void> deleteById(@PathVariable final Long issueId,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(issueId, authorization);
    this.service.deleteById(issueId);
    return ResponseEntity.ok().build();
  }

}
