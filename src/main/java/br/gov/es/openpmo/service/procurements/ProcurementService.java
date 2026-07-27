package br.gov.es.openpmo.service.procurements;

import br.gov.es.openpmo.dto.procurements.ProcurementCreateDto;
import br.gov.es.openpmo.dto.procurements.ProcurementDto;
import br.gov.es.openpmo.dto.procurements.ProcurementUpdateDto;
import br.gov.es.openpmo.model.procurements.Procurement;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ProcurementRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROCUREMENT_NOT_FOUND;

@Service
public class ProcurementService {

    private final ProcurementRepository repository;
    private final WorkpackService workpackService;
    private final ProcurementProviderService providerService;

    public ProcurementService(
        final ProcurementRepository repository,
        final WorkpackService workpackService, final ProcurementProviderService providerService
    ) {
        this.repository = repository;
        this.workpackService = workpackService;
        this.providerService = providerService;
    }

    public Procurement create(final ProcurementCreateDto request) {
        if (request.getIdWorkpack() == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }

        final Workpack workpack = this.workpackService.findByIdDefault(request.getIdWorkpack());
        return this.repository.save(Procurement.of(request, workpack));
    }

    public ProcurementDto update(final ProcurementUpdateDto request) {
        final Procurement procurement = this.findByIdDefault(request.getId());
        procurement.update(request);
        this.repository.save(procurement);
        return ProcurementDto.of(procurement);
    }

    public ProcurementDto findById(final Long id) {
        ProcurementDto dto = ProcurementDto.of(this.findByIdDefault(id));
        br.gov.es.pmo.procurement_core.model.ProcurementDto detail = providerService.detail(dto.getProcessId());
        if (detail != null) { dto.setProcessNumber(detail.getProcessNumber()); dto.setOrganizationName(detail.getOrganizationName()); dto.setYear(detail.getYear()); dto.setObject(detail.getObject()); dto.setModality(detail.getModality()); dto.setStatus(detail.getStatus()); dto.setProtocol(detail.getProtocol()); }
        return dto;
    }

    public List<ProcurementDto> findAllAsCardDto(final Long idWorkpack) {
        if (idWorkpack == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }

        return this.repository.findAllByIdWorkpack(idWorkpack)
            .stream()
            .map(ProcurementDto::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(final Long id) {
        this.repository.delete(this.findByIdDefault(id));
    }

    private Procurement findByIdDefault(final Long id) {
        return this.repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(PROCUREMENT_NOT_FOUND));
    }
}
