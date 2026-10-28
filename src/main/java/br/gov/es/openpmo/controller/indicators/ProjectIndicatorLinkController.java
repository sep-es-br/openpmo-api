package br.gov.es.openpmo.controller.indicators;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.indicators.ProjectIndicatorLinkDto;
import br.gov.es.openpmo.dto.indicators.ProjectIndicatorLinkRequest;
import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.indicators.ProjectIndicatorLinkService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api
@RestController
@RequestMapping("/indicators/associations")
public class ProjectIndicatorLinkController {

    private final ProjectIndicatorLinkService service;
    private final ICanAccessService canAccessService;
    private final TokenService tokenService;

    public ProjectIndicatorLinkController(
        final ProjectIndicatorLinkService service,
        final ICanAccessService canAccessService,
        final TokenService tokenService
    ) {
        this.service = service;
        this.canAccessService = canAccessService;
        this.tokenService = tokenService;
    }

    @GetMapping("/{idWorkpack}")
    public ResponseEntity<ResponseBase<List<ProjectIndicatorLinkDto>>> findAll(
        @PathVariable final Long idWorkpack,
        @RequestParam(required = false) final IndicatorLinkCategory category,
        @Authorization final String authorization
    ) {
        this.tokenService.getUserId(authorization);
        this.canAccessService.ensureCanReadResourceWorkpack(idWorkpack, authorization);
        return ResponseEntity.ok(ResponseBase.of(service.findAll(idWorkpack, category)));
    }

    @PostMapping("/{idWorkpack}")
    public ResponseEntity<ResponseBase<ProjectIndicatorLinkDto>> create(
        @PathVariable final Long idWorkpack,
        @Valid @RequestBody final ProjectIndicatorLinkRequest request,
        @Authorization final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
        return ResponseEntity.ok(ResponseBase.of(service.create(idWorkpack, request)));
    }

    @PatchMapping("/{idWorkpack}/{idLink}/active")
    public ResponseEntity<ResponseBase<ProjectIndicatorLinkDto>> updateActive(
        @PathVariable final Long idWorkpack,
        @PathVariable final Long idLink,
        @RequestBody final boolean active,
        @Authorization final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
        return ResponseEntity.ok(ResponseBase.of(service.updateActive(idWorkpack, idLink, active)));
    }

    @PatchMapping("/{idWorkpack}/{idLink}/project-goals")
    public ResponseEntity<ResponseBase<ProjectIndicatorLinkDto>> updateProjectGoals(
        @PathVariable final Long idWorkpack,
        @PathVariable final Long idLink,
        @RequestBody final Map<String, BigDecimal> projectGoals,
        @Authorization final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idWorkpack, authorization);
        return ResponseEntity.ok(ResponseBase.of(service.updateProjectGoals(idWorkpack, idLink, projectGoals)));
    }
}
