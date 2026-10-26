package br.gov.es.openpmo.controller.strategicplanning;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.strategicplanning.StrategicPlanningProviderService;
import br.gov.es.pmo.strategic_planning_interface.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/strategic-planning")
public class StrategicPlanningController {

    private final StrategicPlanningProviderService service;
    private final TokenService tokenService;

    public StrategicPlanningController(
            StrategicPlanningProviderService service,
            TokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping("/missions")
    public ResponseEntity<ResponseBase<List<MissionDto>>> getMissions(
            @Authorization String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(service.getMissions()));
    }

    @GetMapping("/objectives")
    public ResponseEntity<ResponseBase<List<StrategicObjectiveDto>>> getObjectives(
            @Authorization String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(service.getObjectives()));
    }

    @GetMapping("/targets")
    public ResponseEntity<ResponseBase<List<TargetDto>>> getTargets(
            @Authorization String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(service.getTargets()));
    }

    @GetMapping("/indicators")
    public ResponseEntity<ResponseBase<List<IndicatorDto>>> getIndicators(
            @Authorization String authorization) {
        tokenService.getUserId(authorization);
        return ResponseEntity.ok(ResponseBase.of(service.getIndicators()));
    }
}