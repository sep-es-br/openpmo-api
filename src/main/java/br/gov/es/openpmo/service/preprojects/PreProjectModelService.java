package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.dto.preprojects.PreProjectModelDto;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_MODEL_NOT_FOUND;

@Service
public class PreProjectModelService {

  private final PreProjectModelRepository preProjectModelRepository;

  private final OfficeService officeService;

  private final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  @Autowired
  public PreProjectModelService(
    final PreProjectModelRepository preProjectModelRepository,
    final OfficeService officeService,
    final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities
  ) {
    this.preProjectModelRepository = preProjectModelRepository;
    this.officeService = officeService;
    this.getPropertyModelDtosFromEntities = getPropertyModelDtosFromEntities;
  }

  @Transactional
  public PreProjectModelDto findOrCreateByOfficeId(final Long idOffice) {
    final PreProjectModel preProjectModel;
    final Optional<Long> idPreProjectModel =
      this.preProjectModelRepository.findIdByOfficeId(idOffice);
    if (idPreProjectModel.isPresent()) {
      preProjectModel = this.preProjectModelRepository.findById(idPreProjectModel.get())
        .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));
    } else {
      preProjectModel = this.createForOffice(idOffice);
    }

    return this.toDto(preProjectModel);
  }

  @Transactional
  public PreProjectModelDto update(final Long id, final UpdatePreProjectModelRequest request) {
    final PreProjectModel preProjectModel = this.preProjectModelRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));

    preProjectModel.setActive(request.getActive());
    preProjectModel.setOperation(request.getOperation());

    return this.toDto(this.preProjectModelRepository.save(preProjectModel));
  }

  private PreProjectModel createForOffice(final Long idOffice) {
    return this.preProjectModelRepository.save(this.newForOffice(idOffice));
  }

  private PreProjectModel newForOffice(final Long idOffice) {
    final Office office = this.officeService.findByIdThin(idOffice);
    final PreProjectModel preProjectModel = new PreProjectModel();
    preProjectModel.setOffice(office);
    return preProjectModel;
  }

  private PreProjectModelDto toDto(final PreProjectModel preProjectModel) {
    final List<? extends PropertyModelDto> properties =
      this.getPropertyModelDtosFromEntities.execute(preProjectModel.getProperties());
    return new PreProjectModelDto(preProjectModel, properties);
  }

}
