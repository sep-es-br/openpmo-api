package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterPlanController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class PlanTest extends BaseTest {

  @Autowired
  private PlanController planController;

  @Autowired
  private FilterPlanController filterPlanController;
  @Autowired
  private OfficeController officeController;

  @Autowired
  private PlanModelController planModelController;

  private Long idOffice;
  private Long idPlanModel;
  private Long idFilter;

  @BeforeEach void load() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test Plan");
      office.setFullName("Office Test Plan ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
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
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlanModel = response.getBody().getData().getId();
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterPlanController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }

  }

  @Test void shouldCreatePlan() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test");
    plan.setFullName("Plan Test ADM ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdatePlan() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test update");
    plan.setFullName("Plan Test update ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final PlanUpdateDto planUpdate = new PlanUpdateDto();
    planUpdate.setId(response.getBody().getData().getId());
    planUpdate.setName("Plan updated");
    response = this.planController.update(planUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test delete");
    plan.setFullName("Plan Test delete ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.planController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test list");
    plan.setFullName("Plan Test list ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<PlanDto>>> responseList = this.planController.indexBase(
      this.idOffice,
      null,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test list");
    plan.setFullName("Plan Test list ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<PlanDto>>> responseList = this.planController.indexBase(
      this.idOffice,
      this.idFilter,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final PlanStoreDto plan = new PlanStoreDto();
    plan.setName("Plan Test find");
    plan.setFullName("Plan Test find ");
    plan.setIdOffice(this.idOffice);
    plan.setIdPlanModel(this.idPlanModel);
    plan.setStart(LocalDate.now().minusMonths(2));
    plan.setFinish(LocalDate.now().plusMonths(2));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<PlanDto>> responseFind = this.planController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("Plan Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }


}
