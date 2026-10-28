package br.gov.es.openpmo.controller.indicators;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.indicators.StrategicPlanningProviderService;
import br.gov.es.pmo.strategic_planning_interface.model.IndicatorDto;
import br.gov.es.pmo.strategic_planning_interface.model.MissionDto;
import br.gov.es.pmo.strategic_planning_interface.model.StrategicObjectiveDto;
import br.gov.es.pmo.strategic_planning_interface.model.TargetDto;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/indicators/es500")
public class StrategicPlanningController {

    private final StrategicPlanningProviderService providerService;
    private final TokenService tokenService;

    public StrategicPlanningController(
        final StrategicPlanningProviderService providerService,
        final TokenService tokenService
    ) {
        this.providerService = providerService;
        this.tokenService = tokenService;
    }

    @GetMapping("/missions")
    public ResponseEntity<ResponseBase<List<MissionDto>>> getMissions(@Authorization final String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(providerService.getMissions()));
    }

    @GetMapping("/objectives")
    public ResponseEntity<ResponseBase<List<StrategicObjectiveDto>>> getObjectives(@Authorization final String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(providerService.getObjectives()));
    }

    @GetMapping("/targets")
    public ResponseEntity<ResponseBase<List<TargetDto>>> getTargets(@Authorization final String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(providerService.getTargets()));
    }

    @GetMapping("/indicators")
    public ResponseEntity<ResponseBase<List<IndicatorDto>>> getIndicators(@Authorization final String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(providerService.getIndicators()));
    }
}
