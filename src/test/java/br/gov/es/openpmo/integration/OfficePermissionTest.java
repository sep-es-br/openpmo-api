package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterOfficePermissionController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.permissions.OfficePermissionController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionDto;
import br.gov.es.openpmo.dto.officepermission.OfficePermissionParamDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import br.gov.es.openpmo.repository.AuthServiceRepository;
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

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class OfficePermissionTest extends BaseTest {

  @Autowired
  private OfficePermissionController officePermissionController;

  @Autowired
  private OfficeController officeController;

  @Autowired
  private FilterOfficePermissionController filterOfficePermissionController;

  @Autowired
  private AuthServiceRepository authServiceRepository;

  private Long idOffice;
  private Long idFilter;
  private AuthService authService;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test OfficePermission");
      office.setFullName("Office Test OfficePermission");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idOffice = response.getBody().getData().getId();
    }
    if(this.authService == null) {
      this.authService = new AuthService();
      this.authService.setServer("AcessoCidadao");
      this.authService.setEndPoint("");
      this.authServiceRepository.save(this.authService);
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

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterOfficePermissionController.save(filter);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateOfficePermission() {
    final OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
    officePermission.setEmail("office.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    officePermission.setPermissions(new ArrayList<>());
    officePermission.getPermissions().add(permissionDto);
    officePermission.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<Entity>> response = this.officePermissionController.store(officePermission);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
  }

  @Test void shouldUpdateOfficePermission() {
    final OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
    officePermission.setEmail("office.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    officePermission.setPermissions(new ArrayList<>());
    officePermission.getPermissions().add(permissionDto);
    officePermission.setIdOffice(this.idOffice);
    ResponseEntity<ResponseBase<Entity>> response = this.officePermissionController.store(officePermission);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    final OfficePermissionParamDto officePermissionUpdate = new OfficePermissionParamDto();
    officePermissionUpdate.setEmail("office.test@openpmo.com");
    officePermissionUpdate.setIdOffice(this.idOffice);
    response = this.officePermissionController.update(officePermissionUpdate);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
  }

  @Test void shouldDelete() {
    final OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
    officePermission.setEmail("office.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    final PersonDto personDto = new PersonDto();
    personDto.setName("office.test");
    personDto.setFullName("office.test");
    personDto.setContactEmail("office.test@openpmo.com");
    officePermission.setPerson(personDto);
    officePermission.setPermissions(new ArrayList<>());
    officePermission.getPermissions().add(permissionDto);
    officePermission.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<Entity>> response = this.officePermissionController.store(officePermission);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    final ResponseEntity<Void> responseDelete = this.officePermissionController.delete(
      this.idOffice,
      "office.test@openpmo.com"
    );
    assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
    officePermission.setEmail("office.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    officePermission.setPermissions(new ArrayList<>());
    officePermission.getPermissions().add(permissionDto);
    officePermission.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<Entity>> response = this.officePermissionController.store(officePermission);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficePermissionDto>>> responseList = this.officePermissionController.indexBase(
      this.idOffice,
      null,
      null
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officePermissionController.indexBase(
      this.idOffice,
      null,
      "office.test@openpmo.com"
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());
  }

  @Test void shouldListAllUsingCustomFilter() {
    final OfficePermissionParamDto officePermission = new OfficePermissionParamDto();
    officePermission.setEmail("office.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    officePermission.setPermissions(new ArrayList<>());
    officePermission.getPermissions().add(permissionDto);
    officePermission.setIdOffice(this.idOffice);
    final ResponseEntity<ResponseBase<Entity>> response = this.officePermissionController.store(officePermission);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    ResponseEntity<ResponseBase<List<OfficePermissionDto>>> responseList = this.officePermissionController.indexBase(
      this.idOffice,
      this.idFilter,
      null
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());

    responseList = this.officePermissionController.indexBase(
      this.idOffice,
      this.idFilter,
      "office.test@openpmo.com"
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
    assertFalse(responseList.getBody().getData().isEmpty());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
