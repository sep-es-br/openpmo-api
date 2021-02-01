package br.gov.es.openpmo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Office;
import br.gov.es.openpmo.model.UnitMeasure;
import br.gov.es.openpmo.repository.UnitMeasureRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class UnitMeasureService {

    private final UnitMeasureRepository repository;
    private final OfficeService officeService;

    @Autowired
    public UnitMeasureService(UnitMeasureRepository repository, OfficeService officeService) {
        this.repository = repository;
        this.officeService = officeService;
    }

    public List<UnitMeasure> findAll(Long idOffice) {
        return repository.findByOffice(idOffice);
    }

    public UnitMeasure save(UnitMeasure unitMeasure) {
        unitMeasure.setOffice(getOfficeById(unitMeasure.getOffice().getId()));
        return repository.save(unitMeasure);
    }

    public UnitMeasure findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NegocioException(ApplicationMessage.UNITMEASURE_NOT_FOUND));
    }

    public void delete(UnitMeasure unitMeasure) {
        repository.delete(unitMeasure);
    }

    private Office getOfficeById(Long idOffice) {
        return officeService.findById(idOffice);
    }

    public UnitMeasure getUnitMeasure(UnitMeasureUpdateDto unitMeasureUpdateDto) {
        UnitMeasure unitMeasure = findById(unitMeasureUpdateDto.getId());
        unitMeasure.setName(unitMeasureUpdateDto.getName());
        unitMeasure.setFullName(unitMeasureUpdateDto.getFullName());
        return unitMeasure;
    }

}
