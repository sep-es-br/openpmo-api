package br.gov.es.openpmo.service.agreements;

import br.gov.es.openpmo.dto.agreements.AgreementCreateDto;
import br.gov.es.openpmo.dto.agreements.AgreementDto;
import br.gov.es.openpmo.dto.agreements.AgreementUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.AgreementRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.AGREEMENT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.AGREEMENT_ALREADY_EXISTS;
import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;

@Service
public class AgreementService {

    private final AgreementRepository repository;
    private final WorkpackService workpackService;

    public AgreementService(
        final AgreementRepository repository,
        final WorkpackService workpackService
    ) {
        this.repository = repository;
        this.workpackService = workpackService;
    }

    public Agreement create(final AgreementCreateDto request) {
        if (request.getIdWorkpack() == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }
        if (this.repository.existsByWorkpackAndProcessIdAndType(
            request.getIdWorkpack(),
            request.getProcessId(),
            request.getType().name()
        )) {
            throw new NegocioException(AGREEMENT_ALREADY_EXISTS);
        }
        final Workpack workpack = this.workpackService.findByIdDefault(request.getIdWorkpack());
        return this.repository.save(Agreement.of(request, workpack));
    }

    public AgreementDto update(final AgreementUpdateDto request) {
        final Agreement agreement = this.findByIdDefault(request.getId());
        agreement.update(request);
        this.repository.save(agreement);
        return AgreementDto.of(agreement);
    }

    public AgreementDto findById(final Long id) { return AgreementDto.of(this.findByIdDefault(id)); }

    public List<AgreementDto> findAllAsCardDto(final Long idWorkpack) {
        if (idWorkpack == null) {
            throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
        }
        return this.repository.findAllByIdWorkpack(idWorkpack)
            .stream().map(AgreementDto::of).collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(final Long id) { this.repository.delete(this.findByIdDefault(id)); }

    private Agreement findByIdDefault(final Long id) {
        return this.repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(AGREEMENT_NOT_FOUND));
    }
}
