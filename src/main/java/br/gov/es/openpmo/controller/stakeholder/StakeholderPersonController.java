package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static br.gov.es.openpmo.utils.ApplicationMessage.OPERATION_SUCCESS;

@Api
@RestController
@CrossOrigin
@RequestMapping("/stakeholders/person")
public class StakeholderPersonController {

  private final StakeholderService stakeholderService;

  @Autowired
  public StakeholderPersonController(final StakeholderService stakeholderService) {
    this.stakeholderService = stakeholderService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<StakeholderPersonDto>> index(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "idPerson", required = false) final Long personId
  ) {
    final StakeholderPersonDto personDto = this.stakeholderService.findPerson(idWorkpack, personId);
    if(personDto == null) {
      return ResponseEntity.noContent().build();
    }
    final ResponseBase<StakeholderPersonDto> response = new ResponseBase<StakeholderPersonDto>()
      .setData(personDto)
      .setSuccess(true)
      .setMessage(OPERATION_SUCCESS);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> storePerson(@RequestBody final StakeholderParamDto request) {
    final Person person = this.stakeholderService.storeStakeholderPerson(request);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>()
      .setData(new EntityDto(person.getId()))
      .setSuccess(true)
      .setMessage(OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> updatePerson(@RequestBody final StakeholderParamDto request) {
    this.stakeholderService.updateStakeholderPerson(request);
    final ResponseBase<Entity> entity = new ResponseBase<Entity>()
      .setSuccess(true)
      .setMessage(OPERATION_SUCCESS);
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping
  public ResponseEntity<Void> deletePerson(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "id-person") final Long idPerson,
    @RequestParam(name = "id-plan") final Long idPlan
  ) {
    this.stakeholderService.deletePerson(idWorkpack, idPerson, idPlan);
    return ResponseEntity.ok().build();
  }
}
