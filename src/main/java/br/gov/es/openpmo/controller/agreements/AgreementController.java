package br.gov.es.openpmo.controller.agreements;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.agreements.AgreementCreateDto;
import br.gov.es.openpmo.dto.agreements.AgreementDto;
import br.gov.es.openpmo.dto.agreements.AgreementUpdateDto;
import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.service.agreements.AgreementService;
import br.gov.es.openpmo.service.agreements.AgreementProviderService;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.pmo.agreement_core.model.AgreementOrganizationDto;
import br.gov.es.pmo.agreement_core.model.AgreementType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/agreements")
public class AgreementController {

    private final AgreementService agreementService;
    private final AgreementProviderService agreementProviderService;
    private final CanAccessService canAccessService;

    public AgreementController(
        final AgreementService agreementService,
        final AgreementProviderService agreementProviderService,
        final CanAccessService canAccessService
    ) {
        this.agreementService = agreementService;
        this.agreementProviderService = agreementProviderService;
        this.canAccessService = canAccessService;
    }

    @GetMapping("/years")
    public ResponseEntity<ResponseBase<List<Long>>> getProviderYears(
        @RequestParam("type") final AgreementType type
    ) {
        return ResponseEntity.ok(
            ResponseBase.of(
                this.agreementProviderService.getYears(type)
            )
        );
    }

    @GetMapping("/organizations")
    public ResponseEntity<ResponseBase<List<AgreementOrganizationDto>>>
        getProviderOrganizations(
            @RequestParam("type") final AgreementType type,
            @RequestParam("year") final Long year
        ) {
        return ResponseEntity.ok(
            ResponseBase.of(
                this.agreementProviderService.getOrganizations(
                    type,
                    year
                )
            )
        );
    }

    @GetMapping("/processes")
    public ResponseEntity<
        ResponseBase<
            List<br.gov.es.pmo.agreement_core.model.AgreementDto>
        >
    > getProviderAgreements(
        @RequestParam("type") final AgreementType type,
        @RequestParam("year") final Long year,
        @RequestParam("organization-identifier")
        final String organizationIdentifier,
        @RequestParam("organization-name")
        final String organizationName
    ) {
        return ResponseEntity.ok(
            ResponseBase.of(
                this.agreementProviderService.getAgreements(
                    type,
                    year,
                    organizationIdentifier,
                    organizationName
                )
            )
        );
    }

    @GetMapping("/processes/{processId}")
    public ResponseEntity<
        ResponseBase<br.gov.es.pmo.agreement_core.model.AgreementDto>
    > getProviderAgreement(
        @RequestParam("type") final AgreementType type,
        @PathVariable("processId") final Long processId
    ) {
        return ResponseEntity.ok(
            ResponseBase.of(
                this.agreementProviderService.getAgreement(type, processId)
            )
        );
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
