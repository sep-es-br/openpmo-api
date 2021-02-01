package br.gov.es.openpmo.controller;

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

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.PersonStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.service.StakeholderService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/stakeholders/person")
public class StakeholderPersonController {

    @Autowired
    private StakeholderService stakeholderService;

    @GetMapping
    public ResponseEntity<ResponseBase<StakeholderPersonDto>> index(@RequestParam(name = "id-workpack") Long idWorkpack,
                                                                    @RequestParam(name = "email", required = false)
                                                                        String email) {
        StakeholderPersonDto personDto = stakeholderService.findPerson(idWorkpack, email);
        if (personDto == null) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<StakeholderPersonDto> entity = new ResponseBase<StakeholderPersonDto>().setData(
            personDto).setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.status(200).body(entity);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<Entity>> storePerson(@RequestBody PersonStakeholderParamDto request) {
        stakeholderService.storeStakeholderPerson(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(
            ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<Entity>> updatePerson(@RequestBody PersonStakeholderParamDto request) {
        stakeholderService.updateStakeholderPerson(request);
        ResponseBase<Entity> entity = new ResponseBase<Entity>().setSuccess(true).setMessage(
            ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePerson(@RequestParam(name = "id-workpack", required = true) Long idWorkpack,
                                             @RequestParam(name = "id-person", required = true) Long idPerson) {
        stakeholderService.deletePerson(idWorkpack, idPerson);
        return ResponseEntity.ok().build();
    }
}
