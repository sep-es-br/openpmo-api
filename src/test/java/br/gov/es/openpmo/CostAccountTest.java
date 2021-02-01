package br.gov.es.openpmo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.openpmo.controller.CostAccountController;
import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.WorkpackController;
import br.gov.es.openpmo.controller.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpack.CurrencyDto;
import br.gov.es.openpmo.dto.workpack.LocalitySelectionDto;
import br.gov.es.openpmo.dto.workpack.NumberDto;
import br.gov.es.openpmo.dto.workpack.DateDto;
import br.gov.es.openpmo.dto.workpack.IntegerDto;
import br.gov.es.openpmo.dto.workpack.OrganizationSelectionDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.SelectionDto;
import br.gov.es.openpmo.dto.workpack.TextAreaDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.ToggleDto;
import br.gov.es.openpmo.dto.workpack.UnitSelectionDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.domain.Session;

@Testcontainers
@SpringBootTest
public class CostAccountTest extends BaseTest {

    @Autowired
    private DomainController domainController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private WorkpackModelController workpackModelController;

    @Autowired
    private PlanController planController;

    @Autowired
    private PlanModelController planModelController;

    @Autowired
    private WorkpackController workpackController;

    @Autowired
    private CostAccountController costAccountController;

    private Long idOffice;
    private Long idPlanModel;
    private Long idDomain;
    private Long idPlan;
    private Long idWorkpack;
    private Long idWorkpackModel;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void loadOffice() {
        if (this.idOffice == null) {
            OfficeStoreDto office = new OfficeStoreDto();
            office.setName("Office Test WorkpackModel");
            office.setFullName("Office Test WorkpackModel ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
        if (this.idPlanModel == null) {
            PlanModelStoreDto planModel = new PlanModelStoreDto();
            planModel.setName("PlanModel Test list");
            planModel.setFullName("PlanModel Test list ");
            planModel.setIdOffice(idOffice);
            ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idPlanModel = response.getBody().getData().getId();
        }
        if (this.idDomain == null) {
            DomainStoreDto domain = new DomainStoreDto();
            domain.setName("Domain Test");
            domain.setFullName("Domain Test ADM ");
            domain.setIdOffice(idOffice);
            ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idDomain = response.getBody().getData().getId();
        }
        if (this.idPlan == null) {
            PlanStoreDto plan = new PlanStoreDto();
            plan.setName("Plan Test");
            plan.setFullName("Plan Test ADM ");
            plan.setIdOffice(idOffice);
            plan.setIdPlanModel(idPlanModel);
            plan.setStart(LocalDate.now().minusMonths(2));
            plan.setFinish(LocalDate.now().plusMonths(2));
            ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idPlan = response.getBody().getData().getId();
        }
        if (idWorkpack == null) {
            idWorkpack = getIdWorkpack();
        }
    }

    @Test
    public void shouldCreateCostAccount() {
        CostAccountStoreDto costAccount = new CostAccountStoreDto();
        costAccount.setIdWorkpack(idWorkpack);
        costAccount.setProperties(getProperties());
        ResponseEntity<ResponseBase<EntityDto>> response = costAccountController.save(costAccount);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    private List<PropertyDto> getProperties() {
        List<PropertyDto> properties = new ArrayList<>();
        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(this.idWorkpackModel);
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        responseFind.getBody().getData().getProperties().stream().filter(
            p -> Session.COST.equals(p.getSession())).forEach(property -> {
                switch (property.getClass().getTypeName()) {
                    case "br.gov.es.openpmo.dto.workpackmodel.TextModelDto":
                        TextDto textDto = new TextDto();
                        textDto.setIdPropertyModel(property.getId());
                        textDto.setValue("Text cost");
                        properties.add(textDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.DateModelDto":
                        DateDto dateDto = new DateDto();
                        dateDto.setIdPropertyModel(property.getId());
                        dateDto.setValue(LocalDateTime.now());
                        properties.add(dateDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.IntegerModelDto":
                        IntegerDto integerDto = new IntegerDto();
                        integerDto.setIdPropertyModel(property.getId());
                        integerDto.setValue(5l);
                        properties.add(integerDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.ToggleModelDto":
                        ToggleDto toggleDto = new ToggleDto();
                        toggleDto.setIdPropertyModel(property.getId());
                        toggleDto.setValue(true);
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
                        selectionDto.setValue("Pending");
                        properties.add(selectionDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.TextAreaModelDto":
                        TextAreaDto textAreaDto = new TextAreaDto();
                        textAreaDto.setIdPropertyModel(property.getId());
                        textAreaDto.setValue("Test Text Area");
                        properties.add(textAreaDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.NumberModelDto":
                        NumberDto numberDto = new NumberDto();
                        numberDto.setIdPropertyModel(property.getId());
                        numberDto.setValue(100.0);
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
                    default:
                        break;
                }
        });

        return properties;
    }

    @Test
    public void shouldDelete() {
        CostAccountStoreDto costAccount = new CostAccountStoreDto();
        costAccount.setIdWorkpack(idWorkpack);
        ResponseEntity<ResponseBase<EntityDto>> response = costAccountController.save(costAccount);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = costAccountController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());

    }

    @Test
    public void shouldListAll() {
        CostAccountStoreDto costAccount = new CostAccountStoreDto();
        costAccount.setIdWorkpack(idWorkpack);
        ResponseEntity<ResponseBase<EntityDto>> response = costAccountController.save(costAccount);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<List<CostAccountDto>>> responseList = costAccountController.indexBase(idWorkpack);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());

        ResponseEntity<ResponseBase<CostDto>> responseCost = costAccountController.getCostsByWorkpack(null, idWorkpack);
        Assertions.assertEquals(200, responseCost.getStatusCodeValue());
        Assertions.assertNotNull(responseCost.getBody());
        Assertions.assertNotNull(responseCost.getBody().getData());
    }

    @Test
    public void shouldFindOne() {
        CostAccountStoreDto costAccount = new CostAccountStoreDto();
        costAccount.setIdWorkpack(idWorkpack);
        costAccount.setProperties(getProperties());
        ResponseEntity<ResponseBase<EntityDto>> response = costAccountController.save(costAccount);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());

        ResponseEntity<ResponseBase<CostAccountDto>> responseFind = costAccountController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
    }

    private Long getIdWorkpack() {
        WorkpackModelParamDto workpackModelParam = getWorkpackModelParamProject("Project", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        this.idWorkpackModel = response.getBody().getData().getId();
        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(this.idWorkpackModel);
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());

        WorkpackParamDto workpackParam = getWorkpackParamProject(responseFind.getBody().getData());
        workpackParam.setIdPlan(idPlan);
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        return response.getBody().getData().getId();
    }

}
