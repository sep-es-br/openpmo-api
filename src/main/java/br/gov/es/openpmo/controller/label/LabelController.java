package br.gov.es.openpmo.controller.label;

import br.gov.es.openpmo.configuration.Authorization;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.service.label.LabelService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Api
@RestController
@RequestMapping("/label")
public class LabelController {

    private final LabelService labelService;

    private final ICanAccessService canAccessService;

    @Autowired
    public LabelController(
            final LabelService labelService,
            ICanAccessService canAccessService) {
        this.labelService = labelService;
        this.canAccessService = canAccessService;
    }

    @GetMapping("/{workpackId}")
    public List<ResponseEntity<ResponseBase<String>>> getLabel(@PathVariable Long workpackId,
                                                               @Authorization final String authorization) {

        this.canAccessService.ensureCanReadResourceWorkpack(workpackId, authorization);

        try {
            final Boolean response = this.labelService.getLabel(workpackId);
            return Arrays.asList(
                    ResponseEntity.ok(ResponseBase.of(response ? "reprogrammed" : "foreseen")),
                    ResponseEntity.ok(ResponseBase.of(response ? "abbreviatedReprogrammed" : "abbreviatedForeseen"))
            );
        } catch (Exception e) {
            return List.of(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }
}