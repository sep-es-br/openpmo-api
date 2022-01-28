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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import static br.gov.es.openpmo.enumerator.GeneralOperatorsEnum.DIFERENTE;
import static br.gov.es.openpmo.model.filter.LogicOperatorEnum.OR;
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
      rule.setLogicOperator(OR);
      rule.setOperator(DIFERENTE);
      rule.setPropertyName("name");

      filter.setRules(singletonList(rule));

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterOfficeController.save(filter);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateOffice() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test");
    office.setFullName("Office Test ADM ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdateOffice() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test update");
    office.setFullName("Office Test update ");
    ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final OfficeUpdateDto officeUpdate = new OfficeUpdateDto();
    officeUpdate.setId(response.getBody().getData().getId());
    officeUpdate.setName("Office updated");
    response = this.officeController.update(officeUpdate);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test delete");
    office.setFullName("Office Test delete ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.officeController.delete(response.getBody().getData().getId());
    assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test list");
    office.setFullName("Office Test list ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficeDto>>> responseList = this.officeController.indexBase(
      this.getToken(true),
      null
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officeController.indexBase(this.getToken(false), null);
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertTrue(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test list");
    office.setFullName("Office Test list ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficeDto>>> responseList = this.officeController.indexBase(
      this.getToken(true),
      this.idFilter
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officeController.indexBase(this.getToken(false), this.idFilter);
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertTrue(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final OfficeStoreDto office = new OfficeStoreDto();
    office.setName("Office Test find");
    office.setFullName("Office Test find ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<OfficeDto>> responseFind = this.officeController.findById(response.getBody().getData().getId());
    assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());
    assertEquals("Office Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }


}
