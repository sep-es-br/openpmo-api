package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterCostAccountController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.workpack.CostAccountController;
import br.gov.es.openpmo.controller.workpack.WorkpackController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.costaccount.CostDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.Configuration.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest class CostAccountTest extends BaseTest {

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

  @Autowired
  private FilterCostAccountController filterCostAccountController;

  private Long idOffice;
  private Long idPlanModel;
  private Long idDomain;
  private Long idPlan;
  private Long idWorkpack;
  private Long idWorkpackModel;
  private Long idFilter;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test WorkpackModel");
      office.setFullName("Office Test WorkpackModel ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      assertNotNull(response.getBody(), "Should have body not null");
      assertNotNull(response.getBody().getData(), "Should have data not null");
      this.idOffice = response.getBody().getData().getId();
    }
    if(this.idPlanModel == null) {
      final PlanModelStoreDto planModel = new PlanModelStoreDto(
        this.idOffice,
        "PlanModel Test list",
        "PlanModel Test list ",
        false,
        Collections.emptySet()
      );
      final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
      assertNotNull(response.getBody(), "Should have body not null");
      assertNotNull(response.getBody().getData(), "Should have data not null");
      this.idPlanModel = response.getBody().getData().getId();
    }
    if(this.idDomain == null) {
      final DomainStoreDto domain = this.getDomainStoreDto();
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
      assertNotNull(response.getBody(), "Should have body not null");
      assertNotNull(response.getBody().getData(), "Should have data not null");
      this.idDomain = response.getBody().getData().getId();
    }
    if(this.idPlan == null) {
      final PlanStoreDto plan = new PlanStoreDto();
      plan.setName("Plan Test");
      plan.setFullName("Plan Test ADM ");
      plan.setIdOffice(this.idOffice);
      plan.setIdPlanModel(this.idPlanModel);
      plan.setStart(LocalDate.now().minusMonths(2));
      plan.setFinish(LocalDate.now().plusMonths(2));
      final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
      assertNotNull(response.getBody(), "Should have body not null");
      assertNotNull(response.getBody().getData(), "Should have data not null");
      this.idPlan = response.getBody().getData().getId();
    }
    if(this.idWorkpack == null) {
      this.idWorkpack = this.getIdWorkpack();
    }
    if(this.idFilter == null) {
      final CustomFilterDto filter = new CustomFilterDto();
      filter.setName("Filter");
      filter.setFavorite(false);
      filter.setSortBy(null);
      filter.setSortByDirection(null);

      final CustomFilterRulesDto rule = new CustomFilterRulesDto();
      rule.setValue("a");
      rule.setLogicOperator(LogicOperatorEnum.OR);
      rule.setOperator(GeneralOperatorsEnum.MAIOR_IGUAL);
      rule.setPropertyName("name");

      filter.setRules(singletonList(rule));

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterCostAccountController.save(
        this.idWorkpackModel,
        filter
      );
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
      assertNotNull(response.getBody(), "Should have body not null");
      assertNotNull(response.getBody().getData(), "Should have data not null");

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @NotNull private DomainStoreDto getDomainStoreDto() {
    final DomainStoreDto domain = new DomainStoreDto();
    domain.setName("Domain Test");
    domain.setFullName("Domain Test ADM ");
    domain.setIdOffice(this.idOffice);
    final LocalityStoreDto localityRoot = new LocalityStoreDto();
    localityRoot.setFullName("Locality Root test");
    localityRoot.setType(LocalityTypesEnum.STATE);
    localityRoot.setName("Locality Root test");
    domain.setLocalityRoot(localityRoot);
    return domain;
  }

  private Long getIdWorkpack() {
    final WorkpackModelParamDto workpackModelParam = this.getWorkpackModelParamProject(
      "Project",
      this.idPlanModel,
      this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(response.getBody(), "Should have body not null");
    assertNotNull(response.getBody().getData(), "Should have data not null");
    this.idWorkpackModel = response.getBody().getData().getId();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(this.idWorkpackModel);
    assertNotNull(responseFind.getBody(), "Should have body not null");
    assertNotNull(responseFind.getBody().getData(), "Should have data not null");

    final WorkpackParamDto workpackParam = this.getWorkpackParamProject(responseFind.getBody().getData());
    workpackParam.setIdPlan(this.idPlan);
    response = this.save(workpackParam);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(response.getBody(), "Should have body not null");
    assertNotNull(response.getBody().getData(), "Should have data not null");
    return response.getBody().getData().getId();
  }

  private ResponseEntity<ResponseBase<EntityDto>> save(final WorkpackParamDto workpackParam) {
    return this.workpackController.save(workpackParam, this.getToken(true));
  }

  @Test void shouldCreateCostAccount() {
    final CostAccountStoreDto costAccount = this.createCostAccount();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.costAccountController.save(costAccount);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(response.getBody(), "Should have body not null");
    assertNotNull(response.getBody().getData(), "Should have data not null");
  }

  @NotNull private CostAccountStoreDto createCostAccount() {
    final CostAccountStoreDto costAccount = new CostAccountStoreDto(this.idWorkpack, this.getProperties());
    return costAccount;
  }

  private List<PropertyDto> getProperties() {
    final List<PropertyDto> properties = new ArrayList<>();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(this.idWorkpackModel);
    assertNotNull(responseFind.getBody(), "Should have body not null");
    assertNotNull(responseFind.getBody().getData(), "Should have data not null");
    responseFind.getBody().getData().getProperties().stream().filter(
      p -> Session.COST.equals(p.getSession())).forEach(property -> {
      switch(property.getClass().getTypeName()) {
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.TextModelDto":
          final TextDto textDto = new TextDto();
          textDto.setIdPropertyModel(property.getId());
          textDto.setValue("Text cost");
          properties.add(textDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.DateModelDto":
          final DateDto dateDto = new DateDto();
          dateDto.setIdPropertyModel(property.getId());
          dateDto.setValue(LocalDateTime.now());
          properties.add(dateDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.IntegerModelDto":
          final IntegerDto integerDto = new IntegerDto();
          integerDto.setIdPropertyModel(property.getId());
          integerDto.setValue(5l);
          properties.add(integerDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.ToggleModelDto":
          final ToggleDto toggleDto = new ToggleDto();
          toggleDto.setIdPropertyModel(property.getId());
          toggleDto.setValue(true);
          properties.add(toggleDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.UnitSelectionModelDto":
          final UnitSelectionDto unitSelectionDto = new UnitSelectionDto();
          unitSelectionDto.setIdPropertyModel(property.getId());
          unitSelectionDto.setSelectedValue(null);
          properties.add(unitSelectionDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.SelectionModelDto":
          final SelectionDto selectionDto = new SelectionDto();
          selectionDto.setIdPropertyModel(property.getId());
          selectionDto.setValue("Pending");
          properties.add(selectionDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.TextAreaModelDto":
          final TextAreaDto textAreaDto = new TextAreaDto();
          textAreaDto.setIdPropertyModel(property.getId());
          textAreaDto.setValue("Test Text Area");
          properties.add(textAreaDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.NumberModelDto":
          final NumberDto numberDto = new NumberDto();
          numberDto.setIdPropertyModel(property.getId());
          numberDto.setValue(100.0);
          properties.add(numberDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.CurrencyModelDto":
          final CurrencyDto currencyDto = new CurrencyDto();
          currencyDto.setIdPropertyModel(property.getId());
          currencyDto.setValue(BigDecimal.TEN);
          properties.add(currencyDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.LocalitySelectionModelDto":
          final LocalitySelectionDto localitySelectionDto = new LocalitySelectionDto();
          localitySelectionDto.setIdPropertyModel(property.getId());
          localitySelectionDto.setSelectedValues(null);
          properties.add(localitySelectionDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.OrganizationSelectionModelDto":
          final OrganizationSelectionDto organizationSelectionDto = new OrganizationSelectionDto();
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

  @Test void shouldDelete() {
    final CostAccountStoreDto costAccount = new CostAccountStoreDto(this.idWorkpack, null);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.costAccountController.save(costAccount);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(response.getBody(), "Should have body not null");
    assertNotNull(response.getBody().getData(), "Should have data not null");
    final ResponseEntity<Void> responseDelete = this.costAccountController.delete(response.getBody().getData().getId());
    assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue(), "Should have status code OK");

  }

    @Disabled
    @Test void shouldListAll() {
        final CostAccountStoreDto costAccount = new CostAccountStoreDto(this.idWorkpack, null);
        final ResponseEntity<ResponseBase<EntityDto>> response = this.costAccountController.save(costAccount);
        assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
        assertNotNull(response.getBody(), "Should have body not null");
        assertNotNull(response.getBody().getData(), "Should have data not null");
        final ResponseEntity<ResponseBaseItens<CostAccountDto>> responseList = this.costAccountController.indexBase(
                this.idWorkpack,
                null
        );
        assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue(), "Should have status code OK");
        assertNotNull(responseList.getBody(), "Should have body not null");
        assertNotNull(responseList.getBody().getData(), "Should have data not null");

        final ResponseEntity<ResponseBase<CostDto>> responseCost = this.costAccountController.getCostsByWorkpack(
                null,
                this.idWorkpack
        );
        assertEquals(HTTP_STATUS_OK, responseCost.getStatusCodeValue(), "Should have status code OK");
        assertNotNull(responseCost.getBody(), "Should have body not null");
        assertNotNull(responseCost.getBody().getData(), "Should have data not null");
    }

  @Test void shouldFindOne() {
    final CostAccountStoreDto costAccount = this.createCostAccount();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.costAccountController.save(costAccount);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(response.getBody(), "Should have body not null");
    assertNotNull(response.getBody().getData(), "Should have data not null");

    final ResponseEntity<ResponseBase<CostAccountDto>> responseFind = this.costAccountController.findById(response.getBody().getData().getId());
    assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue(), "Should have status code OK");
    assertNotNull(responseFind.getBody(), "Should have body not null");
    assertNotNull(responseFind.getBody().getData(), "Should have data not null");
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
