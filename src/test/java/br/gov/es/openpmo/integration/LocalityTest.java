package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterLocalityController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.LocalityController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainDto;
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
import org.jetbrains.annotations.NotNull;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
  private Long localityRootId;

  @BeforeEach void loadDomain() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test Locality");
      office.setFullName("Office Test Locality ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idOffice = response.getBody().getData().getId();
    }
    if(this.idDomain == null) {
      final DomainStoreDto domain = this.getDomainStoreDto();
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idDomain = response.getBody().getData().getId();
      final ResponseEntity<ResponseBase<DomainDto>> storedDomain = this.domainController.findById(this.idDomain);
      this.localityRootId = storedDomain.getBody().getData().getLocalityRoot().getId();
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
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @NotNull private DomainStoreDto getDomainStoreDto() {
    final DomainStoreDto domain = new DomainStoreDto();
    domain.setName("Domain Test");
    domain.setFullName("Domain Test ADM");
    domain.setIdOffice(this.idOffice);
    final LocalityStoreDto localityRoot = new LocalityStoreDto();
    localityRoot.setName("Locality Root");
    localityRoot.setFullName("Locality Root");
    localityRoot.setType(LocalityTypesEnum.STATE);
    domain.setLocalityRoot(localityRoot);
    return domain;
  }

  @Test void shouldCreateLocality() {
    final LocalityStoreDto locality = this.getLocalityStoreDto("Locality Test", "Locality Test");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @NotNull private LocalityStoreDto getLocalityStoreDto(final String name, final String fullName) {
    final LocalityStoreDto locality = new LocalityStoreDto();
    locality.setName(name);
    locality.setFullName(fullName);
    locality.setType(LocalityTypesEnum.COUNTRY);
    locality.setIdParent(this.localityRootId);
    locality.setIdDomain(this.idDomain);
    return locality;
  }

  @Test void shouldUpdateLocality() {
    final LocalityStoreDto locality = this.getLocalityStoreDto("Locality Test update", "Locality Test update");
    ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final LocalityUpdateDto localityUpdate = new LocalityUpdateDto();
    localityUpdate.setId(response.getBody().getData().getId());
    localityUpdate.setName("Locality updated");
    localityUpdate.setFullName("Locality Test update");
    localityUpdate.setType(LocalityTypesEnum.COUNTRY);
    localityUpdate.setIdDomain(this.idDomain);
    localityUpdate.setIdParent(this.localityRootId);
    response = this.localityController.update(localityUpdate);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final LocalityStoreDto locality = this.getLocalityStoreDto("Locality Test delete", "Locality Test delete ");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.localityController.delete(response.getBody().getData().getId());
    assertEquals(200, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final LocalityStoreDto locality = this.getLocalityStoreDto("Locality Test list", "Locality Test list");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    assertEquals(200, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<LocalityDto>>> responseList = this.localityController.indexBase(this.idDomain);
    assertEquals(200, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.localityController.indexBaseFirstLevel(this.idDomain, null);
    assertEquals(200, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    final ResponseEntity<ResponseBase<List<LocalityPropertyDto>>> responseProperty = this.localityController.listProperty(
      this.idDomain);
    assertEquals(204, responseProperty.getStatusCodeValue());
    assertNull(responseProperty.getBody());
  }

  @Test void shouldFindOne() {
    final LocalityStoreDto locality = this.getLocalityStoreDto("Locality Test find", "Locality Test find");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.localityController.save(locality);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<LocalityDetailDto>> responseFind = this.localityController.findById(response.getBody().getData().getId());
    assertEquals(200, responseFind.getStatusCodeValue());
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());
    assertEquals("Locality Test find", responseFind.getBody().getData().getName());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}