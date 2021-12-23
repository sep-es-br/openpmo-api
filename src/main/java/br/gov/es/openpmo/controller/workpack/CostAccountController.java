package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountUpdateDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api
@RestController
@CrossOrigin
@RequestMapping("/cost-accounts")
public class CostAccountController {

  private final CostAccountService costAccountService;
  private final WorkpackService workpackService;
  private final ModelMapper modelMapper;

  @Autowired
  public CostAccountController(
    final CostAccountService costAccountService, final WorkpackService workpackService,
    final ModelMapper modelMapper
  ) {
    this.costAccountService = costAccountService;
    this.workpackService = workpackService;
    this.modelMapper = modelMapper;
  }

  @GetMapping
  public ResponseEntity<ResponseBaseItens<CostAccountDto>> indexBase(
    @RequestParam("id-workpack") final Long idWorkpack,
    @RequestParam(required = false) final Long idFilter
  ) {
    final List<CostAccountDto> costs = this.costAccountService.findAllByIdWorkpack(
        idWorkpack,
        idFilter
      )
      .stream()
      .map(this::mapToDto)
      .collect(Collectors.toList());
    if(costs.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBaseItens.of(costs));
  }

  private CostAccountDto mapToDto(final CostAccount costAccount) {
    final CostAccountDto dto = this.modelMapper.map(costAccount, CostAccountDto.class);
    if(costAccount.getWorkpack() != null) {
      dto.setIdWorkpack(costAccount.getWorkpackId());
    }
    if(!CollectionUtils.isEmpty(costAccount.getProperties())) {
      dto.setProperties(this.costAccountService.getPropertiesDto(costAccount.getProperties(), dto));
    }
    return dto;
  }

  @GetMapping("/workpack")
  public ResponseEntity<ResponseBase<CostDto>> getCostsByWorkpack(
    @RequestParam(value = "id", required = false) final Long id, @RequestParam("id-workpack") final Long idWorkpack
  ) {
    final CostDto costDto = this.costAccountService.getCost(id, idWorkpack);
    if(costDto == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(costDto));
  }

  @GetMapping("{id}")
  public ResponseEntity<ResponseBase<CostAccountDto>> findById(@PathVariable final Long id) {
    final CostAccount costAccount = this.costAccountService.findById(id);
    final CostAccountDto costAccountDto = this.modelMapper.map(costAccount, CostAccountDto.class);
    if(!CollectionUtils.isEmpty(costAccount.getProperties())) {
      costAccountDto.setProperties(
        this.costAccountService.getPropertiesDto(costAccount.getProperties(), costAccountDto));
    }
    if(costAccount.getWorkpack() != null) {
      costAccountDto.setIdWorkpack(costAccount.getWorkpack().getId());
    }
    return ResponseEntity.ok(ResponseBase.of(costAccountDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(@Valid @RequestBody final CostAccountStoreDto costAccountStoreDto) {
    final CostAccount costAccount = this.getCostAccount(costAccountStoreDto);
    final Workpack workpack = this.workpackService.findById(costAccountStoreDto.getIdWorkpack());
    if(workpack.getCosts() == null) {
      workpack.setCosts(new HashSet<>());
    }
    workpack.getCosts().add(costAccount);
    this.workpackService.update(workpack);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(costAccount.getId())));
  }

  private CostAccount getCostAccount(final Object cost) {
    Set<Property> properties = null;
    if(cost instanceof CostAccountStoreDto) {
      if(!CollectionUtils.isEmpty(((CostAccountStoreDto) cost).getProperties())) {
        final CostAccountStoreDto store = (CostAccountStoreDto) cost;
        properties = this.workpackService.getProperties(store.getProperties());
        store.setProperties(null);
      }
    }
    else {
      if(!CollectionUtils.isEmpty(((CostAccountUpdateDto) cost).getProperties())) {
        final CostAccountUpdateDto update = (CostAccountUpdateDto) cost;
        properties = this.workpackService.getProperties(update.getProperties());
        update.setProperties(null);
      }
    }
    final CostAccount costAccount = this.modelMapper.map(cost, CostAccount.class);
    costAccount.setProperties(properties);
    return costAccount;
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final CostAccountUpdateDto costAccountUpdateDto
  ) {
    final CostAccount costAccount = this.getCostAccount(costAccountUpdateDto);
    this.costAccountService.save(costAccount);
    final ResponseBase<EntityDto> entity = new ResponseBase<EntityDto>().setData(
      new EntityDto(costAccount.getId())).setSuccess(true);
    return ResponseEntity.status(200).body(entity);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id) {
    final CostAccount costAccount = this.costAccountService.findById(id);
    this.costAccountService.delete(costAccount);
    return ResponseEntity.ok().build();
  }

}
