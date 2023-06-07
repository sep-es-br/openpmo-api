package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.GroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.LocalitySelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.OrganizationSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.UnitSelectionModelDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.*;
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
    }
  }

}
