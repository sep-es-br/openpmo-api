package br.gov.es.openpmo.controller.stakeholder;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.stakeholder.StakeholderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static br.gov.es.openpmo.utils.ApplicationMessage.OPERATION_SUCCESS;

@Api
@RestController
@CrossOrigin
@RequestMapping("/stakeholders/person")
public class StakeholderPersonController {

    private final StakeholderService stakeholderService;
    private final TokenService tokenService;

    @Autowired
    public StakeholderPersonController(final StakeholderService stakeholderService, TokenService tokenService) {
        this.stakeholderService = stakeholderService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<StakeholderPersonDto>> index(
            @RequestParam(name = "id-workpack") final Long idWorkpack,
            @RequestParam(name = "idPerson", required = false) final Long personId,
            @RequestHeader(name = "Authorization") final String authorization
    ) {
        final Long idPerson = this.tokenService.getUserId(authorization);
        final StakeholderPersonDto personDto = this.stakeholderService.findPerson(idWorkpack, personId, idPerson);
        if (personDto == null) {
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
            @RequestParam(name = "id-person") final Long idPerson
    ) {
        this.stakeholderService.deletePerson(idWorkpack, idPerson);
        return ResponseEntity.ok().build();
    }
}
