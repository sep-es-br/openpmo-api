package br.gov.es.openpmo.controller.issue;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.issue.response.IssueResponseCreateDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseDetailDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseUpdateDto;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.issue.IssueResponseService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/issue-response")
public class IssueResponseController {

  private final IssueResponseService service;

  private final TokenService tokenService;

  public IssueResponseController(final IssueResponseService service, final TokenService tokenService) {
    this.service = service;
    this.tokenService = tokenService;
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> create(
      @RequestBody final IssueResponseCreateDto request,
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final EntityDto response = this.service.create(request, idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @GetMapping("/{idIssueResponse}")
  public ResponseEntity<ResponseBase<IssueResponseDetailDto>> findById(@PathVariable("idIssueResponse") final Long id) {
    final IssueResponseDetailDto response = this.service.findByIdAsDetail(id);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<IssueResponseDetailDto>> update(
      @RequestBody final IssueResponseUpdateDto request,
      @RequestHeader(name = "Authorization") final String authorization
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final IssueResponseDetailDto response = this.service.update(request, idPerson);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @DeleteMapping("/{idIssueResponse}")
  public ResponseEntity<ResponseBase<Void>> delete(@PathVariable final Long idIssueResponse) {
    this.service.deleteById(idIssueResponse);
    return ResponseEntity.ok().build();
  }

}
