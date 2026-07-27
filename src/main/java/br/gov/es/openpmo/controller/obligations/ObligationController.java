package br.gov.es.openpmo.controller.obligations;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.obligations.ObligationCreateDto;
import br.gov.es.openpmo.dto.obligations.ObligationDto;
import br.gov.es.openpmo.dto.obligations.ObligationUpdateDto;
import br.gov.es.openpmo.model.obligations.Obligation;
import br.gov.es.openpmo.service.obligations.ObligationService;
import br.gov.es.openpmo.service.obligations.ObligationProviderService;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/obligations")
public class ObligationController {

    private final ObligationService obligationService;

    private final CanAccessService canAccessService;
    private final ObligationProviderService providerService;

    public ObligationController(
        final ObligationService obligationService,
        final CanAccessService canAccessService, final ObligationProviderService providerService
    ) {
        this.obligationService = obligationService;
        this.canAccessService = canAccessService;
        this.providerService = providerService;
    }
    @GetMapping("/years") public ResponseEntity<ResponseBase<List<Long>>> years(){return ResponseEntity.ok(ResponseBase.of(providerService.years()));}
    @GetMapping("/management-units") public ResponseEntity<ResponseBase<?>> units(@RequestParam Long year){return ResponseEntity.ok(ResponseBase.of(providerService.units(year)));}
    @GetMapping("/processes") public ResponseEntity<ResponseBase<?>> processes(@RequestParam Long year,@RequestParam("management-unit-code") String code){return ResponseEntity.ok(ResponseBase.of(providerService.processes(year,code)));}
    @GetMapping("/processes/{processId}") public ResponseEntity<ResponseBase<?>> detail(@PathVariable String processId,@RequestParam("management-unit-code") String code){return ResponseEntity.ok(ResponseBase.of(providerService.detail(processId,code)));}

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> create(
        @RequestBody @Valid
        final ObligationCreateDto request,
        @RequestHeader("Authorization")
        final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(
            request.getIdWorkpack(),
            authorization
        );

        final Obligation obligation =
            this.obligationService.create(request);

        return ResponseEntity.ok(
            ResponseBase.of(
                new EntityDto(obligation.getId())
            )
        );
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(
        @RequestBody @Valid
        final ObligationUpdateDto request,
        @RequestHeader("Authorization")
        final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(
            request.getIdWorkpack(),
            authorization
        );

        final ObligationDto obligation =
            this.obligationService.update(request);

        return ResponseEntity.ok(
            ResponseBase.of(
                new EntityDto(obligation.getId())
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<ObligationDto>> findById(
        @PathVariable("id")
        final Long idObligation
    ) {
        final ObligationDto obligation =
            this.obligationService.findById(
                idObligation
            );

        return ResponseEntity.ok(
            ResponseBase.of(obligation)
        );
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<ObligationDto>>> findAll(
        @RequestParam("id-workpack")
        final Long idWorkpack
    ) {
        final List<ObligationDto> obligations =
            this.obligationService
                .findAllAsCardDto(idWorkpack);

        return ResponseEntity.ok(
            ResponseBase.of(obligations)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable("id")
        final Long idObligation,
        @RequestHeader("Authorization")
        final String authorization
    ) {

        this.canAccessService.ensureCanEditResource(
            idObligation,
            authorization
        );

        this.obligationService.deleteById(idObligation);

        return ResponseEntity.noContent().build();
    }
}
