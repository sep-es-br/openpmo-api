package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterOfficeController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.office.OfficeUpdateDto;
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
@SpringBootTest class OfficeTest extends BaseTest {

  @Autowired
  private OfficeController officeController;
  @Autowired
  private FilterOfficeController filterOfficeController;

  private Long idFilter;

  @BeforeEach void loadOffice() {
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterOfficeController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateOffice() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test");
    office.setFullName("Office Test ADM ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdateOffice() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test update");
    office.setFullName("Office Test update ");
    ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final OfficeUpdateDto officeUpdate = new OfficeUpdateDto();
    officeUpdate.setId(response.getBody().getData().getId());
    officeUpdate.setName("Office updated");
    response = this.officeController.update(officeUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test delete");
    office.setFullName("Office Test delete ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.officeController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test list");
    office.setFullName("Office Test list ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficeDto>>> responseList = this.officeController.indexBase(
      this.getToken(true),
      null
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officeController.indexBase(this.getToken(false), null);
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertTrue(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test list");
    office.setFullName("Office Test list ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficeDto>>> responseList = this.officeController.indexBase(
      this.getToken(true),
      this.idFilter
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officeController.indexBase(this.getToken(false), this.idFilter);
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertTrue(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test find");
    office.setFullName("Office Test find ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<OfficeDto>> responseFind = this.officeController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("Office Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }


}
