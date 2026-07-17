package br.gov.es.openpmo.controller.agreements;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.agreements.AgreementCreateDto;
import br.gov.es.openpmo.dto.agreements.AgreementDto;
import br.gov.es.openpmo.dto.agreements.AgreementUpdateDto;
import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.service.agreements.AgreementService;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/agreements")
public class AgreementController {

    private final AgreementService agreementService;
    private final CanAccessService canAccessService;

    public AgreementController(
        final AgreementService agreementService,
        final CanAccessService canAccessService
    ) {
        this.agreementService = agreementService;
        this.canAccessService = canAccessService;
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> create(
        @RequestBody @Valid final AgreementCreateDto request,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
        final Agreement agreement = this.agreementService.create(request);
        return ResponseEntity.ok(ResponseBase.of(new EntityDto(agreement.getId())));
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(
        @RequestBody @Valid final AgreementUpdateDto request,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
        final AgreementDto agreement = this.agreementService.update(request);
        return ResponseEntity.ok(ResponseBase.of(new EntityDto(agreement.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<AgreementDto>> findById(
        @PathVariable("id") final Long idAgreement
    ) {
        return ResponseEntity.ok(ResponseBase.of(this.agreementService.findById(idAgreement)));
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<AgreementDto>>> findAll(
        @RequestParam("id-workpack") final Long idWorkpack
    ) {
        return ResponseEntity.ok(ResponseBase.of(this.agreementService.findAllAsCardDto(idWorkpack)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable("id") final Long idAgreement,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idAgreement, authorization);
        this.agreementService.deleteById(idAgreement);
        return ResponseEntity.noContent().build();
    }
}
