package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterLocalityController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.LocalityController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityDetailDto;
import br.gov.es.openpmo.dto.domain.LocalityDto;
import br.gov.es.openpmo.dto.domain.LocalityPropertyDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
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
@SpringBootTest class LocalityTest extends BaseTest {

  @Autowired
  private LocalityController localityController;

  @Autowired
  private OfficeController officeController;

  @Autowired
  private DomainController domainController;

  @Autowired
  private FilterLocalityController filterLocalityController;

  private Long idOffice;
  private Long idDomain;
  private Long idFilter;

  @BeforeEach void loadDomain() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test Locality");
      office.setFullName("Office Test Locality ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idOffice = response.getBody().getData().getId();
    }
    if(this.idDomain == null) {
      final DomainStoreDto domain = new DomainStoreDto();
      domain.setName("Domain Test");
      domain.setFullName("Domain Test ADM ");
      domain.setIdOffice(this.idOffice);
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      Assertions.assertEquals(200, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idDomain = response.getBody().getData().getId();
    }
    if(this.idFilter == null) {
      final CustomFilterDto localityFilter = new CustomFilterDto();
      localityFilter.setName("Locality Filter");
      localityFilter.setFavorite(false);
      localityFilter.setSortBy(null);
      localityFilter.setSortByDirection(null);

      final CustomFilterRulesDto rule = new CustomFilterRulesDto();
      rule.setValue("a");
      rule.setLogicOperator(LogicOperatorEnum.OR);
      rule.setOperator(GeneralOperatorsEnum.MAIOR_IGUAL);
      rule.setPropertyName("name");

      localityFilter.setRules(singletonList(rule));
      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterLocalityController.save(localityFilter);
      Assertions.assertEquals(200, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateLocality() {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName("Locality Test");
    locality.setFullName("Locality Test ADM ");
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdDomain(this.idDomain);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldUpdateLocality() {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName("Locality Test update");
    locality.setFullName("Locality Test update ");
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdDomain(this.idDomain);
    ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final LocalityUpdateDto localityUpdate = new LocalityUpdateDto();
    localityUpdate.setId(response.getBody().getData().getId());
    localityUpdate.setName("Locality updated");
    localityUpdate.setFullName("Locality Test update ");
    localityUpdate.setType(LocalityTypesEnum.COUNTRY);
    localityUpdate.setIdDomain(this.idDomain);
    response = this.localityController.update(localityUpdate);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName("Locality Test delete");
    locality.setFullName("Locality Test delete ");
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdDomain(this.idDomain);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.localityController.delete(response.getBody().getData().getId());
    Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName("Locality Test list");
    locality.setFullName("Locality Test list ");
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdDomain(this.idDomain);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<LocalityDto>>> responseList = this.localityController.indexBase(this.idDomain);
    Assertions.assertEquals(200, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.localityController.indexBaseFirstLevel(this.idDomain, this.idFilter);
    Assertions.assertEquals(200, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());

    final ResponseEntity<ResponseBase<List<LocalityPropertyDto>>> responseProperty = this.localityController.listProperty(
      this.idDomain);
    Assertions.assertEquals(200, responseProperty.getStatusCodeValue());
    Assertions.assertNotNull(responseProperty.getBody());
    Assertions.assertNotNull(responseProperty.getBody().getData());
    Assertions.assertFalse(responseProperty.getBody().getData().isEmpty());
  }

  @Test void shouldFindOne() {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName("Locality Test find");
    locality.setFullName("Locality Test find ");
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdDomain(this.idDomain);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<LocalityDetailDto>> responseFind = this.localityController.findById(response.getBody().getData().getId());
    Assertions.assertEquals(200, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    Assertions.assertEquals("Locality Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
