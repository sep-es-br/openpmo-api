package br.gov.es.openpmo.service.preprojects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_MODEL_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PRE_PROJECT_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_REQUIRED_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR;

import br.gov.es.openpmo.dto.preprojects.CreatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaGroupValueDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectListDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationItemDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationCriterionDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationGroupDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectEvaluationRow;
import br.gov.es.openpmo.dto.preprojects.SavePreProjectCriteriaTabValuesRequest;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectRequest;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaListValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaSelectionValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectListItemDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.LinkedHashMap;
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

    final Set<Property> properties = this.instantiateGeneralProperties(model);
    properties.forEach(property -> property.setPreProject(preProject));
    preProject.setProperties(properties);

    return this.toDto(this.preProjectRepository.save(preProject));
  }

  @Transactional(readOnly = true)
  public PreProjectDto findById(final Long id) {
    return this.toDto(this.findByIdThin(id));
  }

  @Transactional(readOnly = true)
  public List<PreProjectListDto> findAllByOfficeId(final Long idOffice) {
    return this.preProjectRepository.findAllByOfficeId(idOffice);
  }

  @Transactional
  public PreProjectEvaluationDto findEvaluation(final Long idPreProject) {
    final PreProject preProject = this.findByIdThin(idPreProject);
    this.synchronizeEvaluationStructure(preProject);
    final CriteriaOperation operation = preProject.getInstance() == null ||
      preProject.getInstance().getId() == null
        ? CriteriaOperation.AVERAGE
        : this.preProjectModelRepository.findById(preProject.getInstance().getId())
          .map(PreProjectModel::getOperation)
          .orElse(CriteriaOperation.AVERAGE);
    final Map<Long, List<PreProjectEvaluationItemDto>> directItemsByCriterion = new LinkedHashMap<>();
    final Map<Long, Map<Long, List<PreProjectEvaluationItemDto>>> itemsByGroup = new LinkedHashMap<>();
    final Map<Long, PreProjectEvaluationRow> criterionRows = new LinkedHashMap<>();
    final Map<Long, PreProjectEvaluationRow> groupRows = new LinkedHashMap<>();
    this.preProjectRepository.findEvaluationRows(idPreProject).forEach(row -> {
      final PreProjectEvaluationItemDto item = new PreProjectEvaluationItemDto(
        row.getIdPropertyModel(),
        row.getName(),
        row.getLabel(),
        row.getNote(),
        row.getWeight(),
        row.getMaximumNote()
      );
      criterionRows.putIfAbsent(row.getIdCriteriaTabModel(), row);
      if (row.getIdGroup() == null) {
        directItemsByCriterion.computeIfAbsent(row.getIdCriteriaTabModel(), ignored -> new ArrayList<>())
          .add(item);
      } else {
        itemsByGroup
          .computeIfAbsent(row.getIdCriteriaTabModel(), ignored -> new LinkedHashMap<>())
          .computeIfAbsent(row.getIdGroup(), ignored -> new ArrayList<>())
          .add(item);
        groupRows.putIfAbsent(row.getIdGroup(), row);
      }
    });
    final List<PreProjectEvaluationCriterionDto> criteria = criterionRows.entrySet().stream()
      .map(entry -> {
        final Long criterionId = entry.getKey();
        final PreProjectEvaluationRow row = entry.getValue();
        final List<PreProjectEvaluationGroupDto> groups = itemsByGroup
          .getOrDefault(criterionId, Collections.emptyMap())
          .entrySet().stream()
          .map(groupEntry -> {
            final PreProjectEvaluationRow groupRow = groupRows.get(groupEntry.getKey());
            return new PreProjectEvaluationGroupDto(
              groupEntry.getKey(),
              groupRow.getGroupName(),
              groupRow.getGroupLabel(),
              groupRow.getGroupWeight(),
              groupRow.getGroupOperation(),
              groupRow.getGroupActive(),
              groupRow.getGroupDisabledValue(),
              groupEntry.getValue()
            );
          })
          .collect(Collectors.toList());
        return new PreProjectEvaluationCriterionDto(
          criterionId,
          row.getCriteriaTabName(),
          row.getCriteriaTabLabel(),
          row.getCriteriaTabWeight(),
          row.getCriteriaTabOperation(),
          directItemsByCriterion.getOrDefault(criterionId, Collections.emptyList()),
          groups
        );
      })
      .collect(Collectors.toList());
    return new PreProjectEvaluationDto(idPreProject, operation.name(), criteria);
  }

  private void synchronizeEvaluationStructure(final PreProject preProject) {
    if (preProject.getInstance() == null || preProject.getInstance().getId() == null) {
      return;
    }
    final PreProjectModel model = this.preProjectModelRepository
      .findById(preProject.getInstance().getId())
      .orElse(null);
    if (model == null || model.getProperties() == null) {
      return;
    }
    model.getProperties().stream()
      .filter(br.gov.es.openpmo.model.properties.models.CriteriaTabModel.class::isInstance)
      .map(PropertyModel::getId)
      .forEach(idCriteriaTabModel -> this.preProjectRepository
        .findCriteriaTabByModelId(preProject.getId(), idCriteriaTabModel)
        .ifPresent(criteriaTab -> this.synchronizeCriteriaTab(criteriaTab, idCriteriaTabModel)));
  }

  @Transactional
  public PreProjectCriteriaTabValuesDto findCriteriaTabValues(
    final Long id,
    final Long idCriteriaTabModel
  ) {
    this.findByIdThin(id);
    return this.preProjectRepository.findCriteriaTabByModelId(id, idCriteriaTabModel)
      .map(this.preProjectPropertyValueMapper::execute)
      .orElseGet(() -> this.preProjectPropertyValueMapper.preview(
        this.findCriteriaTabModel(idCriteriaTabModel)
      ));
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
    final Map<Long, Property> propertiesByModelId = this.propertiesByModelId(propertiesById.values());
    final Map<Long, CriteriaGroup> groupsById = new HashMap<>();
    this.collectGroups(Collections.singleton(criteriaTab), groupsById);
    final Map<Long, CriteriaGroup> groupsByModelId = this.groupsByModelId(groupsById.values());
    final Set<Property> updatedProperties = new HashSet<>();

    request.getValues().forEach(value -> {
      final Property property = this.findProperty(
        value.getId(),
        value.getIdPropertyModel(),
        propertiesById,
        propertiesByModelId
      );
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

    request.getGroups().forEach(groupValue -> {
      final CriteriaGroup group = this.findGroup(
        groupValue.getId(),
        groupValue.getIdPropertyModel(),
        groupsById,
        groupsByModelId
      );
      if (group == null || groupValue.getActive() == null) {
        throw new RegistroNaoEncontradoException(PROPERTY_NOT_FOUND);
      }
      group.setActive(groupValue.getActive());
      updatedProperties.add(group);
    });

    groupsById.values().stream()
      .filter(group -> !group.isActive())
      .forEach(group -> this.clearGroupValues(group, updatedProperties));
    this.validateRequiredCriteriaProperties(criteriaTab.getValue());

    this.propertyRepository.saveAll(updatedProperties);
    return this.preProjectPropertyValueMapper.execute(criteriaTab);
  }

  private Set<Property> instantiateGeneralProperties(final PreProjectModel model) {
    if (model.getProperties() == null) {
      return this.newIdentitySet();
    }
    final Set<Property> properties = this.newIdentitySet();
    model.getProperties().stream()
      .map(PropertyModel::getId)
      .filter(Objects::nonNull)
      .map(this.propertyModelService::findByIdWithChildren)
      // The relation returned by the model query can be hydrated as the base
      // PropertyModel. Only after loading it with its children is its concrete
      // type reliable, so the criterion filter must be applied here.
      .filter(propertyModel -> !(propertyModel instanceof br.gov.es.openpmo.model.properties.models.CriteriaTabModel))
      .map(this.instantiatePreProjectProperty::execute)
      .forEach(properties::add);
    return properties;
  }

  private void applyValue(final Property property, final PreProjectPropertyValueDto value) {
    if (property instanceof CriteriaGroup && value instanceof PreProjectCriteriaGroupValueDto) {
      final Boolean active = ((PreProjectCriteriaGroupValueDto) value).getActive();
      if (active == null) {
        throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
      }
      ((CriteriaGroup) property).setActive(active);
      return;
    }
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

  private void clearGroupValues(
    final CriteriaGroup group,
    final Set<Property> updatedProperties
  ) {
    if (group.getValue() == null) {
      return;
    }
    group.getValue().forEach(property -> {
      if (property instanceof CriteriaList) {
        this.propertyRepository.deleteCriteriaListItems(property.getId());
        ((CriteriaList) property).setValue(Collections.emptySet());
        updatedProperties.add(property);
      } else if (property instanceof CriteriaSelection) {
        this.propertyRepository.deleteCriteriaSelectionValues(property.getId());
        ((CriteriaSelection) property).setValue(Collections.emptySet());
        updatedProperties.add(property);
      } else if (property instanceof CriteriaGroup) {
        this.clearGroupValues((CriteriaGroup) property, updatedProperties);
      }
    });
  }

  private void validateRequiredCriteriaProperties(final Collection<? extends Property> properties) {
    if (properties == null) {
      return;
    }
    properties.forEach(property -> {
      if (property instanceof CriteriaGroup) {
        final CriteriaGroup group = (CriteriaGroup) property;
        if (group.isActive()) {
          this.validateRequiredCriteriaProperties(group.getValue());
        }
        return;
      }
      if (property.getPropertyModel() == null ||
        !property.getPropertyModel().isActive() ||
        !property.getPropertyModel().isRequired()) {
        return;
      }
      if (property instanceof CriteriaList &&
        (((CriteriaList) property).getValue() == null || ((CriteriaList) property).getValue().isEmpty())) {
        throw new NegocioException(PROPERTY_REQUIRED_NOT_FOUND);
      }
      if (property instanceof CriteriaSelection &&
        (((CriteriaSelection) property).getValue() == null || ((CriteriaSelection) property).getValue().isEmpty())) {
        throw new NegocioException(PROPERTY_REQUIRED_NOT_FOUND);
      }
    });
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

  private void collectGroups(
    final Collection<? extends Property> properties,
    final Map<Long, CriteriaGroup> collected
  ) {
    if (properties == null) {
      return;
    }
    properties.forEach(property -> {
      if (property instanceof CriteriaGroup) {
        final CriteriaGroup group = (CriteriaGroup) property;
        if (group.getId() != null) {
          collected.put(group.getId(), group);
        }
        this.collectGroups(group.getValue(), collected);
      } else if (property instanceof CriteriaTab) {
        this.collectGroups(((CriteriaTab) property).getValue(), collected);
      }
    });
  }

  private Map<Long, Property> propertiesByModelId(final Collection<Property> properties) {
    return properties.stream()
      .filter(property -> property.getPropertyModelId() != null)
      .collect(Collectors.toMap(
        Property::getPropertyModelId,
        property -> property,
        (first, ignored) -> first
      ));
  }

  private Map<Long, CriteriaGroup> groupsByModelId(final Collection<CriteriaGroup> groups) {
    return groups.stream()
      .filter(group -> group.getPropertyModelId() != null)
      .collect(Collectors.toMap(
        Property::getPropertyModelId,
        group -> group,
        (first, ignored) -> first
      ));
  }

  private Property findProperty(
    final Long id,
    final Long idPropertyModel,
    final Map<Long, Property> propertiesById,
    final Map<Long, Property> propertiesByModelId
  ) {
    final Property property = propertiesById.get(id);
    if (property != null && Objects.equals(property.getPropertyModelId(), idPropertyModel)) {
      return property;
    }
    return propertiesByModelId.get(idPropertyModel);
  }

  private CriteriaGroup findGroup(
    final Long id,
    final Long idPropertyModel,
    final Map<Long, CriteriaGroup> groupsById,
    final Map<Long, CriteriaGroup> groupsByModelId
  ) {
    final CriteriaGroup group = groupsById.get(id);
    if (group != null && Objects.equals(group.getPropertyModelId(), idPropertyModel)) {
      return group;
    }
    return groupsByModelId.get(idPropertyModel);
  }

  private PreProject findByIdThin(final Long id) {
    return this.preProjectRepository.findByIdThin(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PRE_PROJECT_NOT_FOUND));
  }

  private CriteriaTab getCriteriaTab(final Long id, final Long idCriteriaTabModel) {
    final CriteriaTab criteriaTab = this.preProjectRepository.findCriteriaTabByModelId(id, idCriteriaTabModel)
      .orElseGet(() -> this.createCriteriaTab(id, idCriteriaTabModel));
    this.synchronizeCriteriaTab(criteriaTab, idCriteriaTabModel);
    return criteriaTab;
  }

  private br.gov.es.openpmo.model.properties.models.CriteriaTabModel findCriteriaTabModel(
    final Long idCriteriaTabModel
  ) {
    final PropertyModel model = this.propertyModelService.findByIdWithChildren(idCriteriaTabModel);
    if (!(model instanceof br.gov.es.openpmo.model.properties.models.CriteriaTabModel)) {
      throw new RegistroNaoEncontradoException(PROPERTY_NOT_FOUND);
    }
    return (br.gov.es.openpmo.model.properties.models.CriteriaTabModel) model;
  }

  /** Creates the criterion structure only as part of its explicit save operation. */
  private CriteriaTab createCriteriaTab(final Long idPreProject, final Long idCriteriaTabModel) {
    final CriteriaTab criteriaTab = (CriteriaTab) this.instantiatePreProjectProperty.execute(
      this.findCriteriaTabModel(idCriteriaTabModel)
    );
    criteriaTab.setPreProject(this.findByIdThin(idPreProject));
    return this.propertyRepository.save(criteriaTab);
  }

  /** Add model children created after an existing pre-project was instantiated. */
  private void synchronizeCriteriaTab(final CriteriaTab criteriaTab, final Long idCriteriaTabModel) {
    final PropertyModel model = this.propertyModelService.findByIdWithChildren(idCriteriaTabModel);
    if (!(model instanceof br.gov.es.openpmo.model.properties.models.TabModel)) {
      return;
    }
    final boolean changed = this.synchronizeChildren(
      criteriaTab,
      ((br.gov.es.openpmo.model.properties.models.TabModel) model).getOrganizedProperties()
    );
    if (changed) {
      this.propertyRepository.save(criteriaTab);
    }
  }

  private boolean synchronizeChildren(
    final Property parent,
    final Set<PropertyModel> modelChildren
  ) {
    if (modelChildren == null || modelChildren.isEmpty()) {
      return false;
    }
    Set<Property> children = parent instanceof CriteriaTab
      ? ((CriteriaTab) parent).getValue()
      : ((CriteriaGroup) parent).getValue();
    if (children == null) {
      children = this.newIdentitySet();
      if (parent instanceof CriteriaTab) {
        ((CriteriaTab) parent).setValue(children);
      } else {
        ((CriteriaGroup) parent).setValue(children);
      }
    }
    boolean changed = false;
    for (PropertyModel modelChild : modelChildren) {
      Property child = children.stream()
        .filter(existing -> Objects.equals(existing.getPropertyModelId(), modelChild.getId()))
        .findFirst()
        .orElse(null);
      if (child == null) {
        child = this.instantiatePreProjectProperty.execute(modelChild);
        children.add(child);
        changed = true;
      }
      if (modelChild instanceof br.gov.es.openpmo.model.properties.models.GroupModel
        && child instanceof CriteriaGroup) {
        changed = this.synchronizeChildren(
          child,
          ((br.gov.es.openpmo.model.properties.models.GroupModel) modelChild).getGroupedProperties()
        ) || changed;
      }
    }
    return changed;
  }

  private PreProjectDto toDto(final PreProject preProject) {
    return new PreProjectDto(preProject);
  }

}
