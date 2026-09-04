package br.gov.es.openpmo.service.preprojects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_MODEL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR;

import br.gov.es.openpmo.dto.preprojects.CreatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectDto;
import br.gov.es.openpmo.dto.preprojects.SavePreProjectCriteriaTabValuesRequest;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaListValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaSelectionValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectListItemDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.properties.CriteriaGroup;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.ListItem;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.relations.Accepts;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PreProjectService {

  private final PreProjectRepository preProjectRepository;

  private final PreProjectModelRepository preProjectModelRepository;

  private final PropertyRepository propertyRepository;

  private final PropertyModelService propertyModelService;

  private final OrganizationService organizationService;

  private final InstantiatePreProjectProperty instantiatePreProjectProperty;

  private final PreProjectPropertyValueMapper preProjectPropertyValueMapper;

  public PreProjectService(
    final PreProjectRepository preProjectRepository,
    final PreProjectModelRepository preProjectModelRepository,
    final PropertyRepository propertyRepository,
    final PropertyModelService propertyModelService,
    final OrganizationService organizationService,
    final InstantiatePreProjectProperty instantiatePreProjectProperty,
    final PreProjectPropertyValueMapper preProjectPropertyValueMapper
  ) {
    this.preProjectRepository = preProjectRepository;
    this.preProjectModelRepository = preProjectModelRepository;
    this.propertyRepository = propertyRepository;
    this.propertyModelService = propertyModelService;
    this.organizationService = organizationService;
    this.instantiatePreProjectProperty = instantiatePreProjectProperty;
    this.preProjectPropertyValueMapper = preProjectPropertyValueMapper;
  }

  @Transactional
  public PreProjectDto create(final CreatePreProjectRequest request) {
    final Long idPreProjectModel = this.preProjectModelRepository
      .findIdByOfficeId(request.getIdOffice())
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));
    final PreProjectModel model = this.preProjectModelRepository
      .findById(idPreProjectModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_MODEL_NOT_FOUND));

    final PreProject preProject = new PreProject();
    preProject.setName(request.getName());
    preProject.setFullName(request.getFullName());
    preProject.setExpectedCompletionDate(request.getExpectedCompletionDate());
    preProject.setExpectedDeliveries(request.getExpectedDeliveries());
    preProject.setOrganization(this.organizationService.findById(request.getIdOrganization()));
    preProject.setInstance(model);

    final Set<Property> properties = this.instantiateModelProperties(model);
    properties.forEach(property -> property.setPreProject(preProject));
    preProject.setProperties(properties);

    return this.toDto(this.preProjectRepository.save(preProject));
  }

  @Transactional(readOnly = true)
  public PreProjectDto findById(final Long id) {
    return this.toDto(this.findByIdThin(id));
  }

  @Transactional(readOnly = true)
  public PreProjectCriteriaTabValuesDto findCriteriaTabValues(
    final Long id,
    final Long idCriteriaTabModel
  ) {
    return this.preProjectPropertyValueMapper.execute(
      this.getCriteriaTab(id, idCriteriaTabModel)
    );
  }

  @Transactional
  public PreProjectDto update(final Long id, final UpdatePreProjectRequest request) {
    final PreProject preProject = this.findByIdThin(id);
    preProject.setName(request.getName());
    preProject.setFullName(request.getFullName());
    preProject.setExpectedCompletionDate(request.getExpectedCompletionDate());
    preProject.setExpectedDeliveries(request.getExpectedDeliveries());
    preProject.setOrganization(this.organizationService.findById(request.getIdOrganization()));
    return this.toDto(this.preProjectRepository.save(preProject));
  }

  @Transactional
  public PreProjectCriteriaTabValuesDto saveCriteriaTabValues(
    final Long id,
    final Long idCriteriaTabModel,
    final SavePreProjectCriteriaTabValuesRequest request
  ) {
    final CriteriaTab criteriaTab = this.getCriteriaTab(id, idCriteriaTabModel);
    final Map<Long, Property> propertiesById = new HashMap<>();
    this.collectProperties(Collections.singleton(criteriaTab), propertiesById);
    final Set<Property> updatedProperties = new HashSet<>();

    request.getValues().forEach(value -> {
      final Property property = propertiesById.get(value.getId());
      if (property == null) {
        throw new RegistroNaoEncontradoException(PROPERTY_NOT_FOUND);
      }
      if (property.getPropertyModel() == null ||
        !Objects.equals(property.getPropertyModel().getId(), value.getIdPropertyModel())) {
        throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
      }
      this.applyValue(property, value);
      updatedProperties.add(property);
    });

    this.propertyRepository.saveAll(updatedProperties);
    return this.preProjectPropertyValueMapper.execute(criteriaTab);
  }

  private Set<Property> instantiateModelProperties(final PreProjectModel model) {
    if (model.getProperties() == null) {
      return this.newIdentitySet();
    }
    final Set<Property> properties = this.newIdentitySet();
    model.getProperties().stream()
      .map(PropertyModel::getId)
      .filter(Objects::nonNull)
      .map(this.propertyModelService::findByIdWithChildren)
      .map(this.instantiatePreProjectProperty::execute)
      .forEach(properties::add);
    return properties;
  }

  private void applyValue(final Property property, final PreProjectPropertyValueDto value) {
    if (property instanceof CriteriaList && value instanceof PreProjectCriteriaListValueDto) {
      this.applyListValue((CriteriaList) property, (PreProjectCriteriaListValueDto) value);
      return;
    }
    if (property instanceof CriteriaSelection && value instanceof PreProjectCriteriaSelectionValueDto) {
      this.applySelectionValue(
        (CriteriaSelection) property,
        (PreProjectCriteriaSelectionValueDto) value
      );
      return;
    }
    throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
  }

  private void applyListValue(
    final CriteriaList property,
    final PreProjectCriteriaListValueDto value
  ) {
    this.propertyRepository.deleteCriteriaListItems(property.getId());
    final Set<ListItem> items = Collections.newSetFromMap(new IdentityHashMap<>());
    value.getItems().stream().map(this::newListItem).forEach(items::add);
    property.setValue(items);
  }

  private Set<Property> newIdentitySet() {
    return Collections.newSetFromMap(new IdentityHashMap<>());
  }

  private ListItem newListItem(final PreProjectListItemDto itemDto) {
    final ListItem item = new ListItem();
    item.setForeignKey(itemDto.getForeignKey());
    item.setLabel(itemDto.getLabel());
    return item;
  }

  private void applySelectionValue(
    final CriteriaSelection property,
    final PreProjectCriteriaSelectionValueDto value
  ) {
    final Map<Long, SelectionOption> allowedOptions = this.acceptedOptions(property).stream()
      .map(Accepts::getSelectionOption)
      .filter(Objects::nonNull)
      .filter(option -> option.getId() != null)
      .collect(Collectors.toMap(SelectionOption::getId, option -> option));
    final Set<SelectionOption> selectedOptions = value.getSelectedOptionIds().stream()
      .map(allowedOptions::get)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
    if (selectedOptions.size() != value.getSelectedOptionIds().stream().filter(Objects::nonNull).distinct().count()) {
      throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
    }
    this.propertyRepository.deleteCriteriaSelectionValues(property.getId());
    property.setValue(selectedOptions);
  }

  private Set<Accepts> acceptedOptions(final CriteriaSelection property) {
    if (property.getDriver() == null || property.getDriver().getAcceptedOptions() == null) {
      return Collections.emptySet();
    }
    return property.getDriver().getAcceptedOptions();
  }

  private void collectProperties(
    final Collection<? extends Property> properties,
    final Map<Long, Property> collected
  ) {
    if (properties == null) {
      return;
    }
    properties.forEach(property -> {
      if (property.getId() != null) {
        collected.put(property.getId(), property);
      }
      if (property instanceof CriteriaTab) {
        this.collectProperties(((CriteriaTab) property).getValue(), collected);
      } else if (property instanceof CriteriaGroup) {
        this.collectProperties(((CriteriaGroup) property).getValue(), collected);
      }
    });
  }

  private PreProject findByIdThin(final Long id) {
    return this.preProjectRepository.findByIdThin(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_NOT_FOUND));
  }

  private CriteriaTab getCriteriaTab(final Long id, final Long idCriteriaTabModel) {
    return this.preProjectRepository.findCriteriaTabByModelId(id, idCriteriaTabModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PROPERTY_NOT_FOUND));
  }

  private PreProjectDto toDto(final PreProject preProject) {
    return new PreProjectDto(preProject);
  }

}
