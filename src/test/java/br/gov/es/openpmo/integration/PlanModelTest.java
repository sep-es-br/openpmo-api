package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterPlanModelController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.Configuration.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class PlanModelTest extends BaseTest {

  @Autowired
  private PlanModelController planModelController;

  @Autowired
  private FilterPlanModelController filterPlanModelController;

  @Autowired
  private OfficeController officeController;

  private Long idOffice;
  private Long idFilter;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test PlanModel");
      office.setFullName("Office Test PlanModel ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idOffice = response.getBody().getData().getId();
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterPlanModelController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreatePlanModel() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdatePlanModel() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final PlanModelUpdateDto planModelUpdate = new PlanModelUpdateDto();
    planModelUpdate.setId(response.getBody().getData().getId());
    planModelUpdate.setName("PlanModel updated");
    response = this.planModelController.update(planModelUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.planModelController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<PlanModelDto>>> responseList = this.planModelController.indexBase(
      this.idOffice,
      null
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<PlanModelDto>>> responseList = this.planModelController.indexBase(
      this.idOffice,
      this.idFilter
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final PlanModelStoreDto planModel = new PlanModelStoreDto(
      this.idOffice,
      "PlanModel Test list",
      "PlanModel Test list ",
      false,
      Collections.emptySet()
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planModelController.save(planModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<PlanModelDto>> responseFind = this.planModelController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("PlanModel Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
