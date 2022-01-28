package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.actor.OrganizationController;
import br.gov.es.openpmo.controller.filters.FilterOrganizationsController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;
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

import java.util.List;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class OrganizationTest extends BaseTest {

  @Autowired
  private OrganizationController organizationController;

  @Autowired
  private FilterOrganizationsController filterOrganizationController;

  @Autowired
  private OfficeController officeController;

  private Long idOffice;
  private Long idFilter;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test Organization");
      office.setFullName("Office Test Organization ");
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterOrganizationController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateOrganization() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test");
    organization.setFullName("Organization Test ADM ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdateOrganization() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test update");
    organization.setFullName("Organization Test update ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final OrganizationUpdateDto organizationUpdate = new OrganizationUpdateDto();
    organizationUpdate.setId(response.getBody().getData().getId());
    organizationUpdate.setName("Organization updated");
    response = this.organizationController.update(organizationUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test delete");
    organization.setFullName("Organization Test delete ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.organizationController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test list");
    organization.setFullName("Organization Test list ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<OrganizationDto>>> responseList = this.organizationController.indexBase(
      this.idOffice,
      null
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test list");
    organization.setFullName("Organization Test list ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<OrganizationDto>>> responseList = this.organizationController.indexBase(
      this.idOffice,
      this.idFilter
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final OrganizationStoreDto organization = new OrganizationStoreDto();
    organization.setName("Organization Test find");
    organization.setFullName("Organization Test find ");
    organization.setEmail("organization@openpmo.com");
    organization.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<OrganizationDto>> responseFind = this.organizationController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("Organization Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }


}
