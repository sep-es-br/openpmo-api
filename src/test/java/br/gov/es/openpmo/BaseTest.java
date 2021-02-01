package br.gov.es.openpmo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import br.gov.es.openpmo.dto.workpack.CurrencyDto;
import br.gov.es.openpmo.dto.workpack.DateDto;
import br.gov.es.openpmo.dto.workpack.IntegerDto;
import br.gov.es.openpmo.dto.workpack.LocalitySelectionDto;
import br.gov.es.openpmo.dto.workpack.NumberDto;
import br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto;
import br.gov.es.openpmo.dto.workpack.ProjectParamDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.SelectionDto;
import br.gov.es.openpmo.dto.workpack.TextAreaDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.ToggleDto;
import br.gov.es.openpmo.dto.workpack.UnitSelectionDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.CurrencyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.DateModelDto;
import br.gov.es.openpmo.dto.workpackmodel.IntegerModelDto;
import br.gov.es.openpmo.dto.workpackmodel.LocalitySelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.NumberModelDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizationSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ProjectModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.SelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TextAreaModelDto;
import br.gov.es.openpmo.dto.workpackmodel.TextModelDto;
import br.gov.es.openpmo.dto.workpackmodel.ToggleModelDto;
import br.gov.es.openpmo.dto.workpackmodel.UnitSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.domain.Session;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.service.TokenService;

public abstract class BaseTest {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TokenService tokenService;

    @Container
    protected static final Neo4jContainer databaseServer = new Neo4jContainer<>().withoutAuthentication();

    private Person getPerson(boolean administrator) {
        Person person = personRepository.findByEmail("user.test@openpmo.com").orElse(null);
        if (person == null) {
            person = new Person();
            person.setEmail("user.test@openpmo.com");
            person.setName("Person Teste");
        }
        person.setAdministrator(administrator);
        person = personRepository.save(person);
        return person;
    }

    protected String getToken(boolean administrator) {
        Person person = getPerson(administrator);
        return "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);
    }

    protected WorkpackModelParamDto getWorkpackModelParamProject(String modelName, Long idPlanModel, Long idDomain) {
        ProjectModelParamDto workpackModelParam = new ProjectModelParamDto();
        workpackModelParam.setIdPlanModel(idPlanModel);
        workpackModelParam.setModelName(modelName);
        workpackModelParam.setFontIcon("fa fa-edit");
        workpackModelParam.setModelNameInPlural("Models test");
        workpackModelParam.setOrganizationRoles(Arrays.asList("administrator","user"));
        workpackModelParam.setPersonRoles(Arrays.asList("user", "adm"));
        workpackModelParam.setProperties(getPropertyModels(idDomain));
        workpackModelParam.setSortBy("Name");
        return workpackModelParam;
    }

    private List<PropertyModelDto> getPropertyModels(Long idDomain) {
        List<PropertyModelDto> properties = new ArrayList<>();
        IntegerModelDto integerModel = new IntegerModelDto();
        integerModel.setActive(true);
        integerModel.setRequired(true);
        integerModel.setLabel("Score");
        integerModel.setName("Score");
        integerModel.setMin(0l);
        integerModel.setMax(10l);
        integerModel.setSortIndex(0L);
        integerModel.setSession(Session.PROPERTIES);
        properties.add(integerModel);

        TextModelDto textModel = new TextModelDto();
        textModel.setActive(true);
        textModel.setRequired(true);
        textModel.setLabel("Name");
        textModel.setName("name");
        textModel.setSortIndex(1L);
        textModel.setMin(3l);
        textModel.setMax(200l);
        textModel.setSession(Session.PROPERTIES);
        properties.add(textModel);

        DateModelDto dateModel = new DateModelDto();
        dateModel.setActive(true);
        dateModel.setRequired(true);
        dateModel.setLabel("Date");
        dateModel.setName("Date");
        dateModel.setSortIndex(2L);
        dateModel.setSession(Session.PROPERTIES);
        properties.add(dateModel);

        ToggleModelDto toggleModel = new ToggleModelDto();
        toggleModel.setActive(true);
        toggleModel.setRequired(true);
        toggleModel.setLabel("Is Active");
        toggleModel.setName("Is Active");
        toggleModel.setSortIndex(3L);
        toggleModel.setSession(Session.PROPERTIES);
        properties.add(toggleModel);

        UnitSelectionModelDto unitSelectionModel = new UnitSelectionModelDto();
        unitSelectionModel.setActive(true);
        unitSelectionModel.setLabel("Select Unit");
        unitSelectionModel.setName("Select Unit");
        unitSelectionModel.setSortIndex(4L);
        unitSelectionModel.setSession(Session.PROPERTIES);
        properties.add(unitSelectionModel);

        SelectionModelDto selectionModel = new SelectionModelDto();
        selectionModel.setActive(true);
        selectionModel.setRequired(true);
        selectionModel.setLabel("Type");
        selectionModel.setName("Type");
        selectionModel.setSortIndex(5L);
        selectionModel.setDefaultValue("Pending");
        selectionModel.setPossibleValues("Pending, Finished");
        selectionModel.setSession(Session.PROPERTIES);
        properties.add(selectionModel);

        TextAreaModelDto textAreaModel = new TextAreaModelDto();
        textAreaModel.setActive(true);
        textAreaModel.setRequired(true);
        textAreaModel.setLabel("Complement");
        textAreaModel.setName("Complement");
        textAreaModel.setSortIndex(6L);
        textAreaModel.setMin(3);
        textAreaModel.setMax(1000);
        textAreaModel.setSession(Session.PROPERTIES);
        properties.add(textAreaModel);

        NumberModelDto numberModelDto = new NumberModelDto();
        numberModelDto.setActive(true);
        numberModelDto.setRequired(true);
        numberModelDto.setLabel("Total Planed");
        numberModelDto.setName("Total Planed");
        numberModelDto.setSortIndex(7L);
        numberModelDto.setMin(10d);
        numberModelDto.setMax(10000d);
        numberModelDto.setSession(Session.PROPERTIES);
        properties.add(numberModelDto);

        CurrencyModelDto currencyModelDto = new CurrencyModelDto();
        currencyModelDto.setActive(true);
        currencyModelDto.setRequired(true);
        currencyModelDto.setLabel("Price");
        currencyModelDto.setName("Price");
        currencyModelDto.setSortIndex(9L);
        currencyModelDto.setSession(Session.PROPERTIES);
        properties.add(currencyModelDto);

        LocalitySelectionModelDto localitySelectionModel = new LocalitySelectionModelDto();
        localitySelectionModel.setActive(true);
        localitySelectionModel.setLabel("Select Locality");
        localitySelectionModel.setName("Select Locality");
        localitySelectionModel.setSortIndex(10L);
        localitySelectionModel.setIdDomain(idDomain);
        localitySelectionModel.setSession(Session.PROPERTIES);
        properties.add(localitySelectionModel);

        OrganizationSelectionModelDto organizationSelectionModel = new OrganizationSelectionModelDto();
        organizationSelectionModel.setActive(true);
        organizationSelectionModel.setLabel("Select Organization");
        organizationSelectionModel.setName("Select Organization");
        organizationSelectionModel.setSortIndex(11L);
        organizationSelectionModel.setSession(Session.PROPERTIES);
        properties.add(organizationSelectionModel);

        TextModelDto textModelCost = new TextModelDto();
        textModelCost.setActive(true);
        textModelCost.setRequired(true);
        textModelCost.setLabel("Name Cost");
        textModelCost.setName("Name Cost");
        textModelCost.setSortIndex(1L);
        textModelCost.setSession(Session.COST);
        properties.add(textModelCost);

        DateModelDto dateModelCost = new DateModelDto();
        dateModelCost.setActive(true);
        dateModelCost.setRequired(true);
        dateModelCost.setLabel("Date Cost");
        dateModelCost.setName("Date Cost");
        dateModelCost.setSortIndex(2L);
        dateModelCost.setSession(Session.COST);
        properties.add(dateModelCost);

        IntegerModelDto integerModelCost = new IntegerModelDto();
        integerModelCost.setActive(true);
        integerModelCost.setRequired(true);
        integerModelCost.setLabel("Score cost");
        integerModelCost.setName("Score cost");
        integerModelCost.setSortIndex(3L);
        integerModelCost.setSession(Session.COST);
        properties.add(integerModelCost);

        ToggleModelDto toggleModelCost = new ToggleModelDto();
        toggleModelCost.setActive(true);
        toggleModelCost.setRequired(true);
        toggleModelCost.setLabel("Is Active Cost");
        toggleModelCost.setName("Is Active Cost");
        toggleModelCost.setSortIndex(4L);
        toggleModelCost.setSession(Session.COST);
        properties.add(toggleModelCost);

        UnitSelectionModelDto unitSelectionModelCost = new UnitSelectionModelDto();
        unitSelectionModelCost.setActive(true);
        unitSelectionModelCost.setLabel("Select Unit Cost");
        unitSelectionModelCost.setName("Select Unit Cost");
        unitSelectionModelCost.setSortIndex(5L);
        unitSelectionModelCost.setSession(Session.COST);
        properties.add(unitSelectionModelCost);

        SelectionModelDto selectionModelCost = new SelectionModelDto();
        selectionModelCost.setActive(true);
        selectionModelCost.setRequired(true);
        selectionModelCost.setLabel("Type Cost");
        selectionModelCost.setName("Type Cost");
        selectionModelCost.setSortIndex(6L);
        selectionModelCost.setDefaultValue("Pending");
        selectionModelCost.setPossibleValues("Pending, Finished");
        selectionModelCost.setSession(Session.COST);
        properties.add(selectionModelCost);

        TextAreaModelDto textAreaModelCost = new TextAreaModelDto();
        textAreaModelCost.setActive(true);
        textAreaModelCost.setRequired(true);
        textAreaModelCost.setLabel("Complement Cost");
        textAreaModelCost.setName("Complement Cost");
        textAreaModelCost.setSortIndex(7L);
        textAreaModelCost.setSession(Session.COST);
        properties.add(textAreaModelCost);

        NumberModelDto numberModelDtoCost = new NumberModelDto();
        numberModelDtoCost.setActive(true);
        numberModelDtoCost.setRequired(true);
        numberModelDtoCost.setLabel("Total Planed");
        numberModelDtoCost.setName("Total Planed");
        numberModelDtoCost.setSortIndex(8L);
        numberModelDtoCost.setSession(Session.COST);
        properties.add(numberModelDtoCost);

        CurrencyModelDto currencyModelDtoCost = new CurrencyModelDto();
        currencyModelDtoCost.setActive(true);
        currencyModelDtoCost.setRequired(true);
        currencyModelDtoCost.setLabel("Price Cost");
        currencyModelDtoCost.setName("Price Cost");
        currencyModelDtoCost.setSortIndex(9L);
        currencyModelDtoCost.setSession(Session.COST);
        properties.add(currencyModelDtoCost);

        LocalitySelectionModelDto localitySelectionModelCost = new LocalitySelectionModelDto();
        localitySelectionModelCost.setActive(true);
        localitySelectionModelCost.setLabel("Select Locality Cost");
        localitySelectionModelCost.setName("Select Locality Cost");
        localitySelectionModelCost.setSortIndex(10L);
        localitySelectionModelCost.setIdDomain(idDomain);
        localitySelectionModelCost.setSession(Session.COST);
        properties.add(localitySelectionModelCost);

        OrganizationSelectionModelDto organizationSelectionModelCost = new OrganizationSelectionModelDto();
        organizationSelectionModelCost.setActive(true);
        organizationSelectionModelCost.setLabel("Select Organization Cost");
        organizationSelectionModelCost.setName("Select Organization Cost");
        organizationSelectionModelCost.setSortIndex(11L);
        organizationSelectionModelCost.setSession(Session.COST);
        properties.add(organizationSelectionModelCost);

        return properties;
    }

    protected WorkpackModelParamDto getWorkpackModelParamDto(Long idParent, WorkpackModelParamDto workpackModelParam,
                                                           String name, String icon, Long idPlanModel) {
        workpackModelParam.setModelName(name);
        workpackModelParam.setIdPlanModel(idPlanModel);
        workpackModelParam.setFontIcon(icon);
        if (idParent != null) {
            workpackModelParam.setIdParent(idParent);
        }
        return workpackModelParam;
    }

    protected WorkpackParamDto getWorkpackParamProject(WorkpackModelDetailDto model) {
        ProjectParamDto workpackParam = new ProjectParamDto();
        workpackParam.setIdWorkpackModel(model.getId());
        workpackParam.setProperties(getProperties(model));
        return workpackParam;
    }

    private List<PropertyDto> getProperties(WorkpackModelDetailDto model) {
        List<PropertyDto> properties = new ArrayList<>();
        if (!CollectionUtils.isEmpty(model.getProperties())){
            model.getProperties().forEach(property -> {
                switch (property.getClass().getTypeName()) {
                    case "br.gov.es.openpmo.dto.workpackmodel.IntegerModelDto":
                        IntegerDto integer = new IntegerDto();
                        integer.setIdPropertyModel(property.getId());
                        integer.setValue(10L);
                        properties.add(integer);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.TextModelDto":
                        TextDto textDto = new TextDto();
                        textDto.setIdPropertyModel(property.getId());
                        textDto.setValue("Text test");
                        properties.add(textDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.DateModelDto":
                        DateDto dateDto = new DateDto();
                        dateDto.setIdPropertyModel(property.getId());
                        dateDto.setValue(LocalDateTime.now());
                        properties.add(dateDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.ToggleModelDto":
                        ToggleDto toggleDto = new ToggleDto();
                        toggleDto.setIdPropertyModel(property.getId());
                        toggleDto.setValue(false);
                        properties.add(toggleDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.UnitSelectionModelDto":
                        UnitSelectionDto unitSelectionDto = new UnitSelectionDto();
                        unitSelectionDto.setIdPropertyModel(property.getId());
                        unitSelectionDto.setSelectedValue(null);
                        properties.add(unitSelectionDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.SelectionModelDto":
                        SelectionDto selectionDto = new SelectionDto();
                        selectionDto.setIdPropertyModel(property.getId());
                        selectionDto.setValue("Finished");
                        properties.add(selectionDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.TextAreaModelDto":
                        TextAreaDto textAreaDto = new TextAreaDto();
                        textAreaDto.setIdPropertyModel(property.getId());
                        textAreaDto.setValue("Test input textarea");
                        properties.add(textAreaDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.NumberModelDto":
                        NumberDto numberDto = new NumberDto();
                        numberDto.setIdPropertyModel(property.getId());
                        numberDto.setValue(10.0);
                        properties.add(numberDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.CurrencyModelDto":
                        CurrencyDto currencyDto = new CurrencyDto();
                        currencyDto.setIdPropertyModel(property.getId());
                        currencyDto.setValue(BigDecimal.TEN);
                        properties.add(currencyDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.LocalitySelectionModelDto":
                        LocalitySelectionDto localitySelectionDto = new LocalitySelectionDto();
                        localitySelectionDto.setIdPropertyModel(property.getId());
                        localitySelectionDto.setSelectedValues(null);
                        properties.add(localitySelectionDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.OrganizationSelectionModelDto":
                        OrganizationSelectionDto organizationSelectionDto = new OrganizationSelectionDto();
                        organizationSelectionDto.setIdPropertyModel(property.getId());
                        organizationSelectionDto.setSelectedValues(null);
                        properties.add(organizationSelectionDto);
                        break;
                }
            });
        }
        return properties;
    }
}
