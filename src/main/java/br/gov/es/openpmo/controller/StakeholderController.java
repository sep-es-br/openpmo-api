package br.gov.es.openpmo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.service.StakeholderService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/stakeholders")
public class StakeholderController {

    @Autowired
    private StakeholderService stakeholderService;

    @GetMapping
    public ResponseEntity<ResponseBase<List<StakeholderDto>>> index(
        @RequestParam(name = "id-workpack") Long idWorkpack) {
        List<StakeholderDto> isStakeHolderIn = stakeholderService.findAll(idWorkpack);
        ResponseBase<List<StakeholderDto>> entity = new ResponseBase<List<StakeholderDto>>().setData(
            isStakeHolderIn).setSuccess(true).setMessage(ApplicationMessage.OPERATION_SUCCESS);
        return ResponseEntity.status(200).body(entity);
    }

}
