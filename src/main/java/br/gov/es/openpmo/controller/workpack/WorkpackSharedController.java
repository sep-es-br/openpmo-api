package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedParamDto;
import br.gov.es.openpmo.service.workpack.WorkpackSharedService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.OPERATION_SUCCESS;

@Api
@RestController
@CrossOrigin
@RequestMapping("/workpack/{idWorkpack}/shared")
public class WorkpackSharedController {

  private final WorkpackSharedService service;

  @Autowired
  public WorkpackSharedController(final WorkpackSharedService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<WorkpackSharedDto>>> getAll(@PathVariable("idWorkpack") final Long idWorkpack) {
    final List<WorkpackSharedDto> listDto = this.service.getAll(idWorkpack);
    final ResponseBase<List<WorkpackSharedDto>> entity = new ResponseBase<List<WorkpackSharedDto>>()
      .setSuccess(true)
      .setData(listDto)
      .setMessage(OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<WorkpackSharedDto>> getById(
    @PathVariable("idWorkpack") final Long ignored,
    @PathVariable("id") final Long id
  ) {
    final WorkpackSharedDto dto = this.service.getById(id);
    final ResponseBase<WorkpackSharedDto> entity = new ResponseBase<WorkpackSharedDto>()
      .setSuccess(true)
      .setData(dto)
      .setMessage(OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<List<WorkpackSharedDto>>> store(
    @PathVariable("idWorkpack") final Long idWorkpack,
    @Valid @RequestBody final List<WorkpackSharedParamDto> request
  ) {
    this.service.store(idWorkpack, request);
    final List<WorkpackSharedDto> response = this.service.getAll(idWorkpack);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @DeleteMapping
  public ResponseEntity<Void> delete(
    @PathVariable("idWorkpack") final Long idWorkpack,
    @RequestParam(value = "id-shared-with", required = false) final Long idSharedWith
  ) {
    this.service.revokeShare(idSharedWith, idWorkpack);
    return ResponseEntity.ok().build();
  }

}
