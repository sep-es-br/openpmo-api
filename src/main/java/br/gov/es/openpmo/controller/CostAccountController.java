package br.gov.es.openpmo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountUpdateDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.service.CostAccountService;
import br.gov.es.openpmo.service.WorkpackService;
import io.swagger.annotations.Api;

@Api
@RestController
@CrossOrigin
@RequestMapping(value = "/cost-accounts")
public class CostAccountController {

    private final CostAccountService costAccountService;
    private final WorkpackService workpackService;
    private final ModelMapper modelMapper;

    @Autowired
    public CostAccountController(CostAccountService costAccountService, WorkpackService workpackService,
                                 ModelMapper modelMapper) {
        this.costAccountService = costAccountService;
        this.workpackService = workpackService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<CostAccountDto>>> indexBase(@RequestParam("id-workpack") Long idWorkpack) {
        List<CostAccountDto> costs = costAccountService.findAllByIdWorkpack(idWorkpack).stream().map(o -> {
            CostAccountDto dto = modelMapper.map(o, CostAccountDto.class);
            if (o.getWorkpack() != null) {
                dto.setIdWorkpack(o.getWorkpack().getId());
            }
            if (!CollectionUtils.isEmpty(o.getProperties())) {
                dto.setProperties(costAccountService.getPropertiesDto(o.getProperties(), dto));
            }
            return dto;
        }).collect(Collectors.toList());
        if (costs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<List<CostAccountDto>> response = new ResponseBase<List<CostAccountDto>>().setData(
            costs).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/workpack")
    public ResponseEntity<ResponseBase<CostDto>> getCostsByWorkpack(
        @RequestParam(value = "id", required = false) Long id, @RequestParam("id-workpack") Long idWorkpack) {
        CostDto costDto = costAccountService.getCost(id, idWorkpack);
        if (costDto == null) {
            return ResponseEntity.noContent().build();
        }
        ResponseBase<CostDto> response = new ResponseBase<CostDto>().setData(costDto).setSuccess(true);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ResponseBase<CostAccountDto>> findById(@PathVariable Long id) {
        CostAccount costAccount = costAccountService.findById(id);
        CostAccountDto costAccountDto = modelMapper.map(costAccount, CostAccountDto.class);
        if (!CollectionUtils.isEmpty(costAccount.getProperties())) {
            costAccountDto.setProperties(
                costAccountService.getPropertiesDto(costAccount.getProperties(), costAccountDto));
        }
        if (costAccount.getWorkpack() != null) {
            costAccountDto.setIdWorkpack(costAccount.getWorkpack().getId());
        }
        ResponseBase<CostAccountDto> response = new ResponseBase<CostAccountDto>().setData(costAccountDto).setSuccess(
            true);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody CostAccountStoreDto costAccountStoreDto) {
        CostAccount costAccount = getCostAccount(costAccountStoreDto);
        Workpack workpack = workpackService.findById(costAccountStoreDto.getIdWorkpack());
        if (workpack.getCosts() == null) {
            workpack.setCosts(new HashSet<>());
        }
        workpack.getCosts().add(costAccount);
        workpackService.update(workpack);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(
            new EntityDto(costAccount.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(
        @RequestBody @Valid CostAccountUpdateDto costAccountUpdateDto) {
        CostAccount costAccount = getCostAccount(costAccountUpdateDto);
        costAccountService.save(costAccount);
        ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(
            new EntityDto(costAccount.getId())).setSuccess(true);
        return ResponseEntity.status(200).body(entity);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CostAccount costAccount = costAccountService.findById(id);
        costAccountService.delete(costAccount);
        return ResponseEntity.ok().build();
    }

    private CostAccount getCostAccount(Object cost) {
        Set<Property> properties = null;
        if (cost instanceof CostAccountStoreDto) {
            if (!CollectionUtils.isEmpty(((CostAccountStoreDto) cost).getProperties())) {
                CostAccountStoreDto store = (CostAccountStoreDto) cost;
                properties = workpackService.getProperties(store.getProperties());
                store.setProperties(null);
            }
        } else {
            if (!CollectionUtils.isEmpty(((CostAccountUpdateDto) cost).getProperties())) {
                CostAccountUpdateDto update = (CostAccountUpdateDto) cost;
                properties = workpackService.getProperties(update.getProperties());
                update.setProperties(null);
            }
        }
        CostAccount costAccount = modelMapper.map(cost, CostAccount.class);
        costAccount.setProperties(properties);
        return costAccount;
    }

}
