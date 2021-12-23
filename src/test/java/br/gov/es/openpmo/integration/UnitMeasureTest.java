package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterUnitMeasuresController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.office.UnitMeasureController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureUpdateDto;
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

import java.util.List;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class UnitMeasureTest extends BaseTest {

  @Autowired
  private UnitMeasureController unitMeasureController;

  @Autowired
  private OfficeController officeController;

  @Autowired
  private FilterUnitMeasuresController filterUnitMeasuresController;

  private Long idOffice;
  private Long idFilter;

  @BeforeEach void load() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test UnitMeasure");
      office.setFullName("Office Test UnitMeasure ");
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterUnitMeasuresController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateUnitMeasure() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test");
    unitMeasure.setFullName("UnitMeasure Test ADM ");
    unitMeasure.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdateUnitMeasure() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test update");
    unitMeasure.setFullName("UnitMeasure Test update ");
    unitMeasure.setIdOffice(this.idOffice);
    ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final UnitMeasureUpdateDto unitMeasureUpdate = new UnitMeasureUpdateDto();
    unitMeasureUpdate.setId(response.getBody().getData().getId());
    unitMeasureUpdate.setName("UnitMeasure updated");
    response = this.unitMeasureController.update(unitMeasureUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test delete");
    unitMeasure.setFullName("UnitMeasure Test delete ");
    unitMeasure.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.unitMeasureController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test list");
    unitMeasure.setFullName("UnitMeasure Test list ");
    unitMeasure.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<UnitMeasureDto>>> responseList = this.unitMeasureController.indexBase(
      this.idOffice,
      null
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test list");
    unitMeasure.setFullName("UnitMeasure Test list ");
    unitMeasure.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<UnitMeasureDto>>> responseList = this.unitMeasureController.indexBase(
      this.idOffice,
      this.idFilter
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
    unitMeasure.setName("UnitMeasure Test find");
    unitMeasure.setFullName("UnitMeasure Test find ");
    unitMeasure.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.unitMeasureController.save(unitMeasure);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<UnitMeasureDto>> responseFind = this.unitMeasureController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("UnitMeasure Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }


}
