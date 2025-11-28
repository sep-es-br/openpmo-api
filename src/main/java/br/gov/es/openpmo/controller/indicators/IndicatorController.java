package br.gov.es.openpmo.controller.indicators;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.indicators.IndicatorCardDto;
import br.gov.es.openpmo.dto.indicators.IndicatorCreateDto;
import br.gov.es.openpmo.dto.indicators.IndicatorDetailDto;
import br.gov.es.openpmo.dto.indicators.IndicatorUpdateDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.permission.WorkpackPermissionResponse;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.indicators.Indicator;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.indicators.IndicatorService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.GetWorkpackPermissions;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Api
@RestController
@RequestMapping("/indicators")
public class IndicatorController {

    private final IndicatorService service;
    private final WorkpackPermissionVerifier workpackPermissionVerifier;
    private final TokenService tokenService;
    private final ICanAccessService canAccessService;

    public IndicatorController(
        final IndicatorService service,
        final TokenService tokenService,
        final ICanAccessService canAccessService,
        final WorkpackPermissionVerifier workpackPermissionVerifier
    ) {
        this.service = service;
        this.tokenService = tokenService;
        this.canAccessService = canAccessService;
        this.workpackPermissionVerifier = workpackPermissionVerifier;
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<IndicatorCardDto>>> findAll(
            @RequestParam("id-workpack") final Long idWorkpack,
            @RequestParam(required = false) final Long idFilter,
            @RequestParam(required = false) final String term,
            @Authorization final String authorization) {

        this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
        final Long idPerson = this.tokenService.getUserId(authorization);
        final List<IndicatorCardDto> indicators = this.service.findAllAsCardDto(idWorkpack, idFilter, term, idPerson);
        final ResponseBase<List<IndicatorCardDto>> response = ResponseBase.of(indicators);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idWorkpack}/{idIndicator}")
    public ResponseEntity<ResponseBase<IndicatorDetailDto>> findById(
        @PathVariable final Long idWorkpack,
        @PathVariable final Long idIndicator,
        @Authorization final String authorization
    ) {
        this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
        final IndicatorDetailDto indicator = this.service.findByIdAsIndicatorDetail(idIndicator);
        final ResponseBase<IndicatorDetailDto> response = ResponseBase.of(indicator);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idIndicator}")
    public ResponseEntity<ResponseBase<Void>> delete(
            @PathVariable final Long idIndicator,
            @Authorization final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idIndicator, authorization);
        this.service.deleteById(idIndicator);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<ResponseBase<IndicatorDetailDto>> update(
            @Valid @RequestBody final IndicatorUpdateDto request,
            @Authorization final String authorization) {
    
        this.canAccessService.ensureCanUpdateResource(request.getIdWorkpack(), authorization);
        
        final Long idUser = this.tokenService.getUserId(authorization);

        final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchAccessPermissions(
            idUser,
            request.getIdWorkpack(),
            authorization
          );

        PermissionLevelEnum level = permissions.stream()
            .map(PermissionDto::getLevel) 
            .findFirst()
            .orElse(null);     

        final IndicatorDetailDto indicator;
        if (level == PermissionLevelEnum.UPDATE) {
            indicator = this.service.updateAchievedValues(request);
        } else {
            indicator = this.service.update(request);
        }
        final ResponseBase<IndicatorDetailDto> response = ResponseBase.of(indicator);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> create(
            @Valid @RequestBody final IndicatorCreateDto request,
            @Authorization final String authorization) {

        this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
        final Indicator indicator = this.service.create(request);
        final ResponseBase<EntityDto> response = ResponseBase.of(new EntityDto(indicator.getId()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/period/{idWorkpack}")
    public ResponseEntity<ResponseBase<List<Integer>>> findUniqueYearsByProjectId(
            @PathVariable Long idWorkpack
    ) {
        final List<Integer> periodList = this.service.findUniqueYearsByProjectId(idWorkpack);
        final ResponseBase<List<Integer>> response = ResponseBase.of(periodList);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/office/{idOffice}")
    public ResponseEntity<ResponseBase<List<String>>> findAllOrganizationFromOffice(
            @PathVariable Long idOffice
    ) {
        final List<String> orgazinationList = this.service.findAllOrganizationFromOffice(idOffice);
        final ResponseBase<List<String>> response = ResponseBase.of(orgazinationList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unitMeasure/{idOffice}")
    public ResponseEntity<ResponseBase<List<String>>> findAllUnitMeasureFromOffice(
            @PathVariable Long idOffice
    ) {
        final List<String> unitMeasures = this.service.findAllUnitMeasureFromOffice(idOffice);
        final ResponseBase<List<String>> response = ResponseBase.of(unitMeasures);

        return ResponseEntity.ok(response);
    }
}
