package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.dto.preprojects.PreProjectModelDto;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TabModel;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtoFromEntity;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelFromDto;
import br.gov.es.openpmo.service.workpack.UpdatePropertyModels;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_MODEL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR;

@Service
public class PreProjectModelService {

  private final PreProjectModelRepository preProjectModelRepository;

  private final OfficeService officeService;

  private final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  private final GetPropertyModelFromDto getPropertyModelFromDto;

  private final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  private final PropertyModelService propertyModelService;

  private final UpdatePropertyModels updatePropertyModels;

  @Autowired
  public PreProjectModelService(
    final PreProjectModelRepository preProjectModelRepository,
    final OfficeService officeService,
    final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities,
    final GetPropertyModelFromDto getPropertyModelFromDto,
    final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity,
    final PropertyModelService propertyModelService,
    final UpdatePropertyModels updatePropertyModels
  ) {
    this.preProjectModelRepository = preProjectModelRepository;
    this.officeService = officeService;
    this.getPropertyModelDtosFromEntities = getPropertyModelDtosFromEntities;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
    this.getPropertyModelDtoFromEntity = getPropertyModelDtoFromEntity;
    this.propertyModelService = propertyModelService;
    this.updatePropertyModels = updatePropertyModels;
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

  @Transactional(readOnly = true)
  public PreProjectModelDto findById(final Long id) {
    final PreProjectModel preProjectModel = this.preProjectModelRepository.findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));
    if (preProjectModel.getProperties() != null) {
      final List<PropertyModel> properties = preProjectModel.getProperties().stream()
        .filter(property -> property.getId() != null)
        .map(property -> this.propertyModelService.findByIdWithChildren(property.getId()))
        .sorted(Comparator.comparing(
          PropertyModel::getSortIndex,
          Comparator.nullsLast(Comparator.naturalOrder())
        ))
        .collect(java.util.stream.Collectors.toList());
      preProjectModel.setProperties(new LinkedHashSet<>(properties));
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

  @Transactional
  public CriteriaTabModelDto createCriteriaTab(final Long id, final CriteriaTabModelDto request) {
    final PreProjectModel preProjectModel = this.preProjectModelRepository.findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));

    final CriteriaTabModel criteriaTabModel = this.extractCriteriaTab(request);

    if (preProjectModel.getProperties() == null) {
      preProjectModel.setProperties(new HashSet<>());
    }
    preProjectModel.getProperties().add(criteriaTabModel);

    this.preProjectModelRepository.save(preProjectModel);
    return this.toCriteriaTabDto(criteriaTabModel);
  }

  public CriteriaTabModelDto findCriteriaTabById(final Long id) {
    return this.toCriteriaTabDto(this.getCriteriaTabById(id));
  }

  @Transactional
  public CriteriaTabModelDto updateCriteriaTab(final Long id, final CriteriaTabModelDto request) {
    final CriteriaTabModel criteriaTabModel = this.getCriteriaTabById(id);
    final CriteriaTabModel requestedCriteriaTab = this.extractCriteriaTab(request);
    requestedCriteriaTab.setId(id);

    this.updatePropertyModels.execute(
      Collections.singleton(requestedCriteriaTab),
      Collections.singleton(criteriaTabModel)
    );

    return this.toCriteriaTabDto(this.propertyModelService.save(criteriaTabModel));
  }

  @Transactional
  public void deleteCriteriaTab(final Long id) {
    final CriteriaTabModel criteriaTabModel = this.getCriteriaTabById(id);
    final Set<PropertyModel> propertiesToDelete = new HashSet<>();
    this.collectProperties(criteriaTabModel, propertiesToDelete);

    propertiesToDelete.forEach(propertyModel -> {
      if (!this.propertyModelService.canDeleteProperty(propertyModel.getId())) {
        throw new NegocioException(PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR);
      }
    });

    final List<Long> propertyModelIds = new ArrayList<>();
    propertiesToDelete.stream()
      .map(PropertyModel::getId)
      .filter(java.util.Objects::nonNull)
      .forEach(propertyModelIds::add);
    if (!propertyModelIds.isEmpty()) {
      this.propertyModelService.deleteSelectionOptions(propertyModelIds);
    }
    this.propertyModelService.delete(propertiesToDelete);
  }

  private PreProjectModel createForOffice(final Long idOffice) {
    return this.preProjectModelRepository.save(this.newForOffice(idOffice));
  }

  private CriteriaTabModel getCriteriaTabById(final Long id) {
    final PropertyModel propertyModel = this.propertyModelService.findByIdWithChildren(id);
    if (!(propertyModel instanceof CriteriaTabModel)) {
      throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
    }
    return (CriteriaTabModel) propertyModel;
  }

  private CriteriaTabModel extractCriteriaTab(final CriteriaTabModelDto request) {
    final Set<PropertyModel> extractedProperties =
      this.getPropertyModelFromDto.execute(Collections.singletonList(request));
    return extractedProperties.stream()
      .filter(CriteriaTabModel.class::isInstance)
      .map(CriteriaTabModel.class::cast)
      .findFirst()
      .orElseThrow(IllegalArgumentException::new);
  }

  private CriteriaTabModelDto toCriteriaTabDto(final PropertyModel propertyModel) {
    return (CriteriaTabModelDto) this.getPropertyModelDtoFromEntity.execute(propertyModel);
  }

  private void collectProperties(
    final PropertyModel propertyModel,
    final Collection<PropertyModel> collectedProperties
  ) {
    if (propertyModel == null || !collectedProperties.add(propertyModel)) {
      return;
    }
    if (propertyModel instanceof TabModel) {
      final Set<PropertyModel> organizedProperties = ((TabModel) propertyModel).getOrganizedProperties();
      if (organizedProperties != null) {
        organizedProperties.forEach(property -> this.collectProperties(property, collectedProperties));
      }
    }
    if (propertyModel instanceof GroupModel) {
      final Set<PropertyModel> groupedProperties = ((GroupModel) propertyModel).getGroupedProperties();
      if (groupedProperties != null) {
        groupedProperties.forEach(property -> this.collectProperties(property, collectedProperties));
      }
    }
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
