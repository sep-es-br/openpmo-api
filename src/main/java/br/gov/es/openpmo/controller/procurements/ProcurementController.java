package br.gov.es.openpmo.controller.procurements;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.procurements.ProcurementCreateDto;
import br.gov.es.openpmo.dto.procurements.ProcurementDto;
import br.gov.es.openpmo.dto.procurements.ProcurementUpdateDto;
import br.gov.es.openpmo.model.procurements.Procurement;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.procurements.ProcurementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/procurements")
public class ProcurementController {

    private final ProcurementService procurementService;
    private final CanAccessService canAccessService;

    public ProcurementController(
        final ProcurementService procurementService,
        final CanAccessService canAccessService
    ) {
        this.procurementService = procurementService;
        this.canAccessService = canAccessService;
    }

    @PostMapping
    public ResponseEntity<ResponseBase<EntityDto>> create(
        @RequestBody @Valid final ProcurementCreateDto request,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
        final Procurement procurement = this.procurementService.create(request);
        return ResponseEntity.ok(ResponseBase.of(new EntityDto(procurement.getId())));
    }

    @PutMapping
    public ResponseEntity<ResponseBase<EntityDto>> update(
        @RequestBody @Valid final ProcurementUpdateDto request,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(request.getIdWorkpack(), authorization);
        final ProcurementDto procurement = this.procurementService.update(request);
        return ResponseEntity.ok(ResponseBase.of(new EntityDto(procurement.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBase<ProcurementDto>> findById(
        @PathVariable("id") final Long idProcurement
    ) {
        return ResponseEntity.ok(ResponseBase.of(this.procurementService.findById(idProcurement)));
    }

    @GetMapping
    public ResponseEntity<ResponseBase<List<ProcurementDto>>> findAll(
        @RequestParam("id-workpack") final Long idWorkpack
    ) {
        return ResponseEntity.ok(ResponseBase.of(this.procurementService.findAllAsCardDto(idWorkpack)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable("id") final Long idProcurement,
        @RequestHeader("Authorization") final String authorization
    ) {
        this.canAccessService.ensureCanEditResource(idProcurement, authorization);
        this.procurementService.deleteById(idProcurement);
        return ResponseEntity.noContent().build();
    }
}
