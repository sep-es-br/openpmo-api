package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
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

@Api
@RestController
@CrossOrigin
@RequestMapping("/stakeholders/person")
public class StakeholderPersonController {

  private final StakeholderService stakeholderService;
  private final TokenService tokenService;
  private final ICanAccessService canAccessService;

  @Autowired
  public StakeholderPersonController(
    final StakeholderService stakeholderService,
    final TokenService tokenService,
    final ICanAccessService canAccessService
  ) {
    this.stakeholderService = stakeholderService;
    this.tokenService = tokenService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<StakeholderPersonDto>> index(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "idPerson", required = false) final Long personId,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(idWorkpack, authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final StakeholderPersonDto personDto = this.stakeholderService.findPerson(idWorkpack, personId, idPerson);
    return personDto == null ?
      ResponseEntity.noContent().build() :
      ResponseEntity.ok(ResponseBase.of(personDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> storePerson(
    @RequestBody final StakeholderParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    final Person person = this.stakeholderService.storeStakeholderPerson(request);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(person.getId())));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<Entity>> updatePerson(
    @RequestBody final StakeholderParamDto request,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
    this.stakeholderService.updateStakeholderPerson(request);
    return ResponseEntity.ok(ResponseBase.of());
  }

  @DeleteMapping
  public ResponseEntity<Void> deletePerson(
    @RequestParam(name = "id-workpack") final Long idWorkpack,
    @RequestParam(name = "id-person") final Long idPerson,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
    this.stakeholderService.deletePerson(idWorkpack, idPerson);
    return ResponseEntity.ok().build();
  }

}
