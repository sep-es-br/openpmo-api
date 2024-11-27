package br.gov.es.openpmo.controller.schedule;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.schedule.ScheduleService;
import br.gov.es.openpmo.utils.RestTemplateUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Api
@RestController
@CrossOrigin
@RequestMapping("/schedules")
public class ScheduleController {

  private final ScheduleService scheduleService;

  private final ICanAccessService canAccessService;

  @Value("${pentaho.api.po_liquidado.url}")
  private String poLiquidatedUrl;

  @Value("${pentahoBI.userId}")
  private String pentahoUserId;

  @Value("${pentahoBI.password}")
  private String pentahoPassword;

  private final RestTemplateUtils restTemplateUtils = new RestTemplateUtils();

  @Autowired
  public ScheduleController(
    final ScheduleService scheduleService,
    final ICanAccessService canAccessService
  ) {
    this.scheduleService = scheduleService;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public ResponseEntity<ResponseBase<List<ScheduleDto>>> findAll(
    @RequestParam("id-workpack") final Long idWorkpack,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanReadResourceWorkpack(
      idWorkpack,
      authorization
    );
    final List<ScheduleDto> schedules = this.scheduleService.findAll(idWorkpack);
    if (schedules.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(ResponseBase.of(schedules));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResponseBase<ScheduleDto>> findById(
    @PathVariable final Long id,
    @RequestHeader(name = "Authorization") final String authorization
  ) {

    this.canAccessService.ensureCanReadResource(
      id,
      authorization
    );
    final Schedule schedule = this.scheduleService.findById(id);
    final ScheduleDto scheduleDto = this.scheduleService.mapsToScheduleDto(schedule);
    return ResponseEntity.ok(ResponseBase.of(scheduleDto));
  }

  @PostMapping
  public ResponseEntity<ResponseBase<EntityDto>> save(
    @Valid @RequestBody final ScheduleParamDto scheduleParamDto,
    @RequestHeader(name = "Authorization") final String authorization
  ) {
    this.canAccessService.ensureCanEditResource(
      scheduleParamDto.getIdWorkpack(),
      authorization
    );
    final Schedule schedule = this.scheduleService.save(scheduleParamDto);
    return ResponseEntity.ok(ResponseBase.of(new EntityDto(schedule.getId())));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> delete(
    @PathVariable final Long id,
    @RequestHeader(name = "Authorization") final String authorization
  ) {

    this.canAccessService.ensureCanEditResource(
      id,
      authorization
    );
    this.scheduleService.delete(id);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/baseline/{workpackId}")
  public ResponseEntity<ResponseBase<Boolean>> getCurrentBaseline(@PathVariable long workpackId, @Authorization final String authorization) {

    this.canAccessService.ensureCanReadResourceWorkpack(workpackId, authorization);
    final Boolean response = this.scheduleService.getCurrentBaseline(workpackId);
    return ResponseEntity.ok(ResponseBase.of(response));
  }

  /**
   * Método controlador responsável pela consulta dos valores liquidados do PO
   *
   * @param codPo codigo do PO para consulta dos valores liquidados
   * @return o JSON Object da consulta
   */
  @GetMapping("/po/liquidated/{codPo}")
  public ResponseEntity<Object> getPoLiquidated(@PathVariable("codPo") String codPo) {
    RestTemplate restTemplate;
    try {
      restTemplate = restTemplateUtils.createRestTemplateWithNoSSL();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
    restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

    String url = poLiquidatedUrl + codPo;

    return restTemplateUtils.createRequestWithAuth(restTemplate, url, pentahoUserId, pentahoPassword);
  }
}
