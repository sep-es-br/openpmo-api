package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
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
  private final ICanAccessService canAccessService;

  @Autowired
  public StakeholderController(
    final StakeholderService stakeholderService,
    final ICanAccessService canAccessService
  ) {
    this.stakeholderService = stakeholderService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<StakeholderDto>>> index(
    @Authorization final String authorization,
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(required = false) final Long idFilter
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final List<StakeholderDto> isStakeHolderIn = this.stakeholderService.findAll(idWorkpack, idFilter);
    return ResponseEntity.ok(ResponseBase.of(isStakeHolderIn));
  }

  @GetMapping("/responsibles")
  public ResponseEntity<ResponseBase<Set<StakeholderCardViewDto>>> findAllPersonStakeholderByIdWorkpack(
    @RequestParam("id-workpack") final Long idWorkpack,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final Set<StakeholderCardViewDto> stakeholders = this.stakeholderService
      .findAllPersonStakeholderByWorkpackId(idWorkpack);
    final ResponseBase<Set<StakeholderCardViewDto>> response = ResponseBase.of(stakeholders);
    return ResponseEntity.ok(response);
  }

}
