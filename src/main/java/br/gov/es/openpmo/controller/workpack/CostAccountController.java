package br.gov.es.openpmo.controller.workpack;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountUpdateDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.CostAccountService;
import br.gov.es.openpmo.utils.RestTemplateUtils;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
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
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/cost-accounts")
public class CostAccountController {

  private final CostAccountService costAccountService;
  private final ICanAccessService canAccessService;

  @Value("${pentaho.api.uo.url}")
  private String uoUrl;

  @Value("${pentaho.api.po.url}")
  private String poUrl;

  @Value("${pentahoBI.userId}")
  private String pentahoUserId;

  @Value("${pentahoBI.password}")
  private String pentahoPassword;

  private final RestTemplateUtils restTemplateUtils = new RestTemplateUtils();

  private final Logger logger = LogManager.getLogger(CostAccountController.class);

  @Autowired
  public CostAccountController(
    final CostAccountService costAccountService,
    final ICanAccessService canAccessService
  ) {
    this.costAccountService = costAccountService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBaseItens<CostAccountDto>> indexBase(
    @RequestParam("id-workpack") final Long idWorkpack,
    @RequestParam(required = false) final Long idFilter,
    @RequestParam(required = false) final String term,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
    final List<CostAccountDto> costs = this.costAccountService.findAllByIdWorkpack(idWorkpack, idFilter, term);
    if (costs.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBaseItens.of(costs));
  }

  @GetMapping("/workpack")
  public ResponseEntity<ResponseBase<CostDto>> getCostsByWorkpack(
    @RequestParam(value = "id", required = false) final Long id,
    @RequestParam("id-workpack") final Long idWorkpack,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
    final CostDto costDto = this.costAccountService.getCost(id, idWorkpack);
    if (costDto == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(costDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<CostAccountDto>> findById(
    @PathVariable final Long id,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanReadResource(id, authorization);
    final CostAccountDto response = this.costAccountService.findByIdAsDto(id);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @Valid @RequestBody final CostAccountStoreDto costAccountStoreDto,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(costAccountStoreDto.getIdWorkpack(), authorization);
    final CostAccount costAccount = this.costAccountService.getCostAccount(costAccountStoreDto);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(costAccount.getId())));
  }

  @PutMapping
  public ResponseEntity<ResponseBase<EntityDto>> update(
    @RequestBody @Valid final CostAccountUpdateDto costAccountUpdateDto,
    @Authorization final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(costAccountUpdateDto.getIdWorkpack(), authorization);
    final CostAccount costAccount = this.costAccountService.getCostAccount(costAccountUpdateDto);
    return ResponseEntity.ok(ResponseBase.of(EntityDto.of(costAccount)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable final Long id, @Authorization final String authorization) {
    this.canAccessService.ensureCanEditResource(id, authorization);
    final CostAccount costAccount = this.costAccountService.findById(id);
    this.costAccountService.delete(costAccount);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/budgetUnit/{idCostAccount}")
  public ResponseEntity<Object> getUO(@PathVariable("idCostAccount") Long idCostAccount,
                                      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(idCostAccount, authorization);
    RestTemplate restTemplate;

    try {
      restTemplate = restTemplateUtils.createRestTemplateWithNoSSL();
    } catch (Exception e) {
      logger.error("Erro ao realizar requisição ao Pentaho: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao configurar o RestTemplate para a URL " + uoUrl);
    }
    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    return restTemplateUtils.createRequestWithAuth(restTemplate, uoUrl, pentahoUserId, pentahoPassword);
  }

  @GetMapping("/budgetPlan")
  public ResponseEntity<Object> getPO(@RequestParam("codUo") String codUo,
                                      @RequestParam("costAccountId") Long idCostAccount,
                                      @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResource(idCostAccount, authorization);
    RestTemplate restTemplate;

    try {
      restTemplate = restTemplateUtils.createRestTemplateWithNoSSL();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao configurar o RestTemplate para a URL " + poUrl);
    }
    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    String url = poUrl + codUo;

    return restTemplateUtils.createRequestWithAuth(restTemplate, url, pentahoUserId, pentahoPassword);
  }
}
