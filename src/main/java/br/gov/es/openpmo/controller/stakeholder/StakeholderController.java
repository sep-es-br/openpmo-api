package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Api
@RestController
@CrossOrigin
@RequestMapping("/stakeholders")
public class StakeholderController {

  private final StakeholderService stakeholderService;

  @Autowired
  public StakeholderController(final StakeholderService stakeholderService) {
    this.stakeholderService = stakeholderService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<StakeholderDto>>> index(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(required = false) final Long idFilter
  ) {
    final List<StakeholderDto> isStakeHolderIn = this.stakeholderService.findAll(idWorkpack, idFilter);

    final ResponseBase<List<StakeholderDto>> response = new ResponseBase<List<StakeholderDto>>()
      .setData(isStakeHolderIn).setSuccess(true)
      .setMessage(ApplicationMessage.OPERATION_SUCCESS);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/responsibles")
  public ResponseEntity<ResponseBase<Set<StakeholderCardViewDto>>> findAllPersonStakeholderByIdWorkpack(
    @RequestParam("id-workpack") final Long idWorkpack
  ) {
    final Set<StakeholderCardViewDto> stakeholders = this.stakeholderService
      .findAllPersonStakeholderByWorkpackId(idWorkpack);
    final ResponseBase<Set<StakeholderCardViewDto>> response = ResponseBase.of(stakeholders);
    return ResponseEntity.ok(response);
  }

}
