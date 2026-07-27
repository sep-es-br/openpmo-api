package br.gov.es.openpmo.service.obligations;

import br.gov.es.openpmo.dto.obligations.ObligationCreateDto;
import br.gov.es.openpmo.dto.obligations.ObligationDto;
import br.gov.es.openpmo.dto.obligations.ObligationUpdateDto;
import br.gov.es.openpmo.model.obligations.Obligation;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ObligationRepository;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

import static br.gov.es.openpmo.utils.ApplicationMessage.OBLIGATION_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ObligationService {

    private final ObligationRepository repository;

    private final WorkpackService workpackService;
    private final ObligationProviderService providerService;

    public ObligationService(
        final ObligationRepository repository,
        final WorkpackService workpackService, final ObligationProviderService providerService
    ) {
        this.repository = repository;
        this.workpackService = workpackService;
        this.providerService = providerService;
    }

    public Obligation create(
        final ObligationCreateDto request
    ) {
        if (request.getIdWorkpack() == null) {
            throw new IllegalArgumentException(
                ID_WORKPACK_NOT_NULL
            );
        }

        final Workpack workpack =
            this.workpackService.findByIdDefault(
                request.getIdWorkpack()
            );

        final Obligation obligation =
            Obligation.of(
                request,
                workpack
            );

        return this.repository.save(obligation);
    }

    public ObligationDto update(
        final @Valid ObligationUpdateDto request
    ) {
        final Obligation obligation = 
            this.repository.findById(request.getId())
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        OBLIGATION_NOT_FOUND
                    )
                );

        obligation.update(request);

        this.repository.save(obligation);

        return ObligationDto.of(obligation);
    }


    public ObligationDto findById(
        final Long id
    ) {
        final Obligation obligation =
            this.repository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        OBLIGATION_NOT_FOUND
                    )
                );
    
        ObligationDto dto = new ObligationDto();
    
        /*
         * Dados do banco
         */
        dto.setId(obligation.getId());
        dto.setIdWorkpack(obligation.getIdWorkpack());
        dto.setObligationNumber(
            obligation.getObligationNumber()
        );
        dto.setDescription(
            obligation.getDescription()
        );
    
        dto.setManagementUnitCode(obligation.getManagementUnitCode());
        br.gov.es.pmo.obligation_core.model.ObligationDto detail = providerService.detail(obligation.getObligationNumber(), obligation.getManagementUnitCode());
        if (detail != null) { dto.setManagementUnitName(detail.getManagementUnitName()); dto.setYear(detail.getYear()); dto.setSupplierCnpj(detail.getSupplierCnpj()); dto.setAmount(detail.getAmount()); dto.setProtocol(detail.getProtocol()); dto.setDescription(detail.getDescription()); }
    
        return dto;
    }

    public List<ObligationDto> findAllAsCardDto(
        final Long idWorkpack
    ) {
        if (idWorkpack == null) {
            throw new IllegalArgumentException(
                ID_WORKPACK_NOT_NULL
            );
        }

        return this.repository
            .findAllByIdWorkpack(idWorkpack)
            .stream()
            .map(ObligationDto::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(
        final Long id
    ) {
        final Obligation obligation =
            this.repository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        OBLIGATION_NOT_FOUND
                    )
                );

        this.repository.delete(obligation);
    }
}
