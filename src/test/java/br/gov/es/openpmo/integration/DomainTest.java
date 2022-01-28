package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterDomainController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.DomainUpdateDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import org.jetbrains.annotations.NotNull;
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
@SpringBootTest class DomainTest extends BaseTest {

  @Autowired
  private DomainController domainController;

  @Autowired
  private OfficeController officeController;

  @Autowired
  private FilterDomainController filterDomainController;

  private Long idOffice;
  private Long idFilter;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test Domain");
      office.setFullName("Office Test Domain ");
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterDomainController.save(filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateDomain() {
    final DomainStoreDto domain = this.getDomainStoreDto();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @NotNull private DomainStoreDto getDomainStoreDto() {
    final DomainStoreDto domain = new DomainStoreDto();
    domain.setName("Domain Test");
    domain.setFullName("Domain Test");
    domain.setIdOffice(this.idOffice);
    final LocalityStoreDto localityRoot = new LocalityStoreDto();
    domain.setLocalityRoot(localityRoot);
    localityRoot.setName("Locality Root");
    localityRoot.setType(LocalityTypesEnum.STATE);
    localityRoot.setFullName("Locality Root");
    return domain;
  }

  @Test void shouldUpdateDomain() {
    final DomainStoreDto domain = this.getDomainStoreDto();
    ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final DomainUpdateDto domainUpdate = new DomainUpdateDto();
    domainUpdate.setId(response.getBody().getData().getId());
    domainUpdate.setName("Domain updated");
    response = this.domainController.update(domainUpdate);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final DomainStoreDto domain = this.getDomainStoreDto();

    final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.domainController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final DomainStoreDto domain = this.getDomainStoreDto();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<DomainDto>>> responseList = this.domainController.indexBase(
      this.idOffice,
      null
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final DomainStoreDto domain = this.getDomainStoreDto();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<DomainDto>>> responseList = this.domainController.indexBase(
      this.idOffice,
      this.idFilter
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final DomainStoreDto domain = this.getDomainStoreDto();
    final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<DomainDto>> responseFind = this.domainController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("Domain Test", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
