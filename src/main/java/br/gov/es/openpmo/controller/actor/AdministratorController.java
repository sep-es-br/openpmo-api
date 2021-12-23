package br.gov.es.openpmo.controller.actor;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.administrator.AdministratorDto;
import br.gov.es.openpmo.dto.administrator.AdministratorStatusRequest;
import br.gov.es.openpmo.service.actors.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/persons/administrator")
public class AdministratorController {

  private final AdministratorService administratorService;


  @Autowired
  public AdministratorController(final AdministratorService administratorService) {
    this.administratorService = administratorService;
  }


  @PatchMapping("/{personId}")
  public ResponseEntity<ResponseBase<AdministratorDto>> updateAdministratorStatus(
    @Valid @RequestBody final AdministratorStatusRequest request,
    @PathVariable final Long personId
  ) {
    final AdministratorDto dto = this.administratorService.updateAdministratorStatus(request, personId);

    final ResponseBase<AdministratorDto> response = new ResponseBase<AdministratorDto>()
      .setData(dto)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<AdministratorDto>>> findAllAdministrators() {
    final List<AdministratorDto> administrators = this.administratorService.findAllAdministrators();
    final ResponseBase<List<AdministratorDto>> response = new ResponseBase<List<AdministratorDto>>()
      .setData(administrators)
      .setSuccess(true);

    return ResponseEntity.ok(response);
  }
}
