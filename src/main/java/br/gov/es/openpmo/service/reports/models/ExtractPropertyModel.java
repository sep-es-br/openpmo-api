package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.GroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaGroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaListModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.SelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.SelectionOptionDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.TabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.LocalitySelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.OrganizationSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.UnitSelectionModelDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.relations.Accepts;
import br.gov.es.openpmo.service.actors.OrganizationService;
import br.gov.es.openpmo.service.office.DomainService;
import br.gov.es.openpmo.service.office.LocalityService;
import br.gov.es.openpmo.service.office.UnitMeasureService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class ExtractPropertyModel {

  private static final String PACKAGE_PROPERTIES_DTO = "br.gov.es.openpmo.dto.workpackmodel.params.properties";

  private final ModelMapper modelMapper;

  private final UnitMeasureService unitMeasureService;

  private final DomainService domainService;

  private final LocalityService localityService;

  private final OrganizationService organizationService;

  public ExtractPropertyModel(
    ModelMapper modelMapper,
    UnitMeasureService unitMeasureService,
    DomainService domainService,
    LocalityService localityService,
    OrganizationService organizationService
  ) {
    this.modelMapper = modelMapper;
    this.unitMeasureService = unitMeasureService;
    this.domainService = domainService;
    this.localityService = localityService;
    this.organizationService = organizationService;
  }

  public void execute(
    final Collection<? super PropertyModel> propertyModels,
    final PropertyModelDto property
  ) {
    switch (property.getClass().getTypeName()) {
      case PACKAGE_PROPERTIES_DTO + ".IntegerModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          IntegerModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".TextModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          TextModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".DateModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          DateModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".ToggleModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          ToggleModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".UnitSelectionModelDto":
        final UnitSelectionModel unitSelectionModel = this.modelMapper.map(
          property,
          UnitSelectionModel.class
        );
        final UnitSelectionModelDto unitSelectionDto = (UnitSelectionModelDto) property;
        if (unitSelectionDto.getDefaults() != null) {
          unitSelectionModel.setDefaultValue(this.unitMeasureService.findById(unitSelectionDto.getDefaults()));
        }
        propertyModels.add(unitSelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".SelectionModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          SelectionModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".TextAreaModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          TextAreaModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".NumberModelDto":
        final NumberModel numberModel = this.modelMapper.map(
          property,
          NumberModel.class
        );
        final Integer precision = numberModel.getPrecision();
        if (precision == null) {
          numberModel.setPrecision(3);
        } else if (precision < 1 || precision > 6) {
          throw new NegocioException(ApplicationMessage.PRECISION_OUT_OF_RANGE);
        }
        propertyModels.add(numberModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CurrencyModelDto":
        propertyModels.add(this.modelMapper.map(
          property,
          CurrencyModel.class
        ));
        break;
      case PACKAGE_PROPERTIES_DTO + ".LocalitySelectionModelDto":
        final LocalitySelectionModel localitySelectionModel = this.modelMapper.map(
          property,
          LocalitySelectionModel.class
        );
        final LocalitySelectionModelDto localityDto = (LocalitySelectionModelDto) property;
        if (localityDto.getIdDomain() != null) {
          localitySelectionModel.setDomain(this.domainService.findById(localityDto.getIdDomain()));
        }
        if (localityDto.getDefaults() != null && !(localityDto.getDefaults()).isEmpty()) {
          localitySelectionModel.setDefaultValue(new HashSet<>());
          localityDto.getDefaults().forEach(
            l -> localitySelectionModel.getDefaultValue().add(this.localityService.findById(l)));
        }
        propertyModels.add(localitySelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".OrganizationSelectionModelDto":
        final OrganizationSelectionModel organizationSelectionModel = this.modelMapper.map(
          property,
          OrganizationSelectionModel.class
        );
        final OrganizationSelectionModelDto organizationDto = (OrganizationSelectionModelDto) property;
        if (organizationDto.getDefaults() != null && !(organizationDto.getDefaults()).isEmpty()) {
          organizationSelectionModel.setDefaultValue(new HashSet<>());
          organizationDto.getDefaults().forEach(
            o -> organizationSelectionModel.getDefaultValue().add(this.organizationService.findById(o)));
        }
        propertyModels.add(organizationSelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".GroupModelDto":
        final GroupModel groupModel = this.modelMapper.map(
          property,
          GroupModel.class
        );
        final GroupModelDto groupModelDto = (GroupModelDto) property;

        final Set<PropertyModel> groupedProperties = new HashSet<>();

        groupModelDto.getGroupedProperties().forEach(p -> this.execute(
          groupedProperties,
          p
        ));
        groupModel.setGroupedProperties(groupedProperties);

        propertyModels.add(groupModel);

        break;
      case PACKAGE_PROPERTIES_DTO + ".TabModelDto":
        final TabModel tabModel = new TabModel();
        this.copyBaseFields(property, tabModel);
        tabModel.setOrganizedProperties(this.extractNestedProperties(((TabModelDto) property).getOrganizedProperties()));
        propertyModels.add(tabModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".ListModelDto":
        final ListModel listModel = new ListModel();
        this.copyBaseFields(property, listModel);
        propertyModels.add(listModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CriteriaTabModelDto":
        final CriteriaTabModelDto criteriaTabDto = (CriteriaTabModelDto) property;
        final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
        this.copyBaseFields(criteriaTabDto, criteriaTabModel);
        criteriaTabModel.setWeight(criteriaTabDto.getWeight());
        criteriaTabModel.setOperation(criteriaTabDto.getOperation());
        criteriaTabModel.setOrganizedProperties(this.extractNestedProperties(criteriaTabDto.getOrganizedProperties()));
        propertyModels.add(criteriaTabModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CriteriaGroupModelDto":
        final CriteriaGroupModelDto criteriaGroupDto = (CriteriaGroupModelDto) property;
        final CriteriaGroupModel criteriaGroupModel = new CriteriaGroupModel();
        this.copyBaseFields(criteriaGroupDto, criteriaGroupModel);
        criteriaGroupModel.setWeight(criteriaGroupDto.getWeight());
        criteriaGroupModel.setOperation(criteriaGroupDto.getOperation());
        criteriaGroupModel.setGroupedProperties(this.extractNestedProperties(criteriaGroupDto.getGroupedProperties()));
        propertyModels.add(criteriaGroupModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CriteriaSelectionModelDto":
        final CriteriaSelectionModelDto criteriaSelectionDto = (CriteriaSelectionModelDto) property;
        final CriteriaSelectionModel criteriaSelectionModel = new CriteriaSelectionModel();
        this.copyBaseFields(criteriaSelectionDto, criteriaSelectionModel);
        this.copySelectionFields(criteriaSelectionDto, criteriaSelectionModel);
        criteriaSelectionModel.setWeight(criteriaSelectionDto.getWeight());
        criteriaSelectionModel.setAcceptedOptions(this.extractAcceptedOptions(
          criteriaSelectionDto.getAcceptedOptions(),
          criteriaSelectionModel
        ));
        propertyModels.add(criteriaSelectionModel);
        break;
      case PACKAGE_PROPERTIES_DTO + ".CriteriaListModelDto":
        final CriteriaListModelDto criteriaListDto = (CriteriaListModelDto) property;
        final CriteriaListModel criteriaListModel = new CriteriaListModel();
        this.copyBaseFields(criteriaListDto, criteriaListModel);
        criteriaListModel.setWeight(criteriaListDto.getWeight());
        criteriaListModel.setItemValue(criteriaListDto.getItemValue());
        propertyModels.add(criteriaListModel);
        break;
    }
  }

  private Set<PropertyModel> extractNestedProperties(
    final Collection<? extends PropertyModelDto> propertyModelDtos
  ) {
    final Set<PropertyModel> nestedProperties = new HashSet<>();
    if (propertyModelDtos != null) {
      propertyModelDtos.forEach(property -> this.execute(nestedProperties, property));
    }
    return nestedProperties;
  }

  private Set<Accepts> extractAcceptedOptions(
    final Collection<SelectionOptionDto> optionDtos,
    final CriteriaSelectionModel criteriaSelectionModel
  ) {
    final Set<Accepts> acceptedOptions = new HashSet<>();
    if (optionDtos == null) {
      return acceptedOptions;
    }
    optionDtos.forEach(optionDto -> {
      final SelectionOption selectionOption = new SelectionOption();
      selectionOption.setId(optionDto.getId());
      selectionOption.setValue(optionDto.getValue());
      selectionOption.setLabel(optionDto.getLabel());
      selectionOption.setPosition(optionDto.getPosition());

      final Accepts accepts = new Accepts();
      accepts.setDefaultOption(optionDto.getDefaultOption());
      accepts.setCriteriaSelectionModel(criteriaSelectionModel);
      accepts.setSelectionOption(selectionOption);
      acceptedOptions.add(accepts);
    });
    return acceptedOptions;
  }

  private void copySelectionFields(final SelectionModelDto source, final SelectionModel target) {
    target.setDefaultValue(source.getDefaultValue());
    target.setPossibleValues(source.getPossibleValues());
    target.setMultipleSelection(source.isMultipleSelection());
  }

  private void copyBaseFields(final PropertyModelDto source, final PropertyModel target) {
    target.setId(source.getId());
    target.setSortIndex(source.getSortIndex());
    target.setName(source.getName());
    target.setLabel(source.getLabel());
    target.setHelpText(source.getHelpText());
    target.setActive(source.isActive());
    target.setFullLine(source.isFullLine());
    target.setRequired(source.isRequired());
  }

}
