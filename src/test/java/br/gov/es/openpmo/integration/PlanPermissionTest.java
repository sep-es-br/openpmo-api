package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.permissions.PlanPermissionController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionDto;
import br.gov.es.openpmo.dto.planpermission.PlanPermissionParamDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.Entity;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest class PlanPermissionTest extends BaseTest {

  @Autowired
  private PlanPermissionController planPermissionController;

  @Autowired
  private PlanController planController;

  @Autowired
  private OfficeController officeController;

  @Autowired
  private PlanModelController planModelController;

  private Long idOffice;
  private Long idPlanModel;
  private Long idPlan;

  @BeforeEach void loadPlan() {
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
      Assertions.assertEquals(200, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlanModel = response.getBody().getData().getId();
    }
    if(this.idPlan == null) {
      final PlanStoreDto plan = new PlanStoreDto();
      plan.setName("Plan Test PlanPermission");
      plan.setFullName("Plan Test PlanPermission ");
      plan.setIdOffice(this.idOffice);
      plan.setIdPlanModel(this.idPlanModel);
      plan.setStart(LocalDate.now().minusMonths(2));
      plan.setFinish(LocalDate.now().plusMonths(2));
      final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlan = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreatePlanPermission() {
    final PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
    planPermission.setEmail("plan.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    planPermission.setPermissions(new ArrayList<>());
    planPermission.getPermissions().add(permissionDto);
    planPermission.setIdPlan(this.idPlan);
    final ResponseEntity<ResponseBase<Entity>> response = this.planPermissionController.store(planPermission);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
  }

  @Test void shouldUpdatePlanPermission() {
    final PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
    planPermission.setEmail("plan.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    planPermission.setPermissions(new ArrayList<>());
    planPermission.getPermissions().add(permissionDto);
    planPermission.setIdPlan(this.idPlan);
    ResponseEntity<ResponseBase<Entity>> response = this.planPermissionController.store(planPermission);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    final PlanPermissionParamDto planPermissionUpdate = new PlanPermissionParamDto();
    planPermissionUpdate.setEmail("plan.test@openpmo.com");
    planPermissionUpdate.setIdPlan(this.idPlan);
    response = this.planPermissionController.update(planPermissionUpdate);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
  }

  @Test void shouldDelete() {
    final PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
    planPermission.setEmail("plan.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    planPermission.setPermissions(new ArrayList<>());
    planPermission.getPermissions().add(permissionDto);
    planPermission.setIdPlan(this.idPlan);
    final ResponseEntity<ResponseBase<Entity>> response = this.planPermissionController.store(planPermission);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    final ResponseEntity<Void> responseDelete = this.planPermissionController.delete(this.idPlan, "plan.test@openpmo.com");
    Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
    planPermission.setEmail("plan.test@openpmo.com");
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.EDIT);
    permissionDto.setRole("roleTest");
    planPermission.setPermissions(new ArrayList<>());
    planPermission.getPermissions().add(permissionDto);
    planPermission.setIdPlan(this.idPlan);
    final ResponseEntity<ResponseBase<Entity>> response = this.planPermissionController.store(planPermission);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    final ResponseEntity<ResponseBase<List<PlanPermissionDto>>> responseList = this.planPermissionController.indexBase(
      this.idPlan,
      null
    );
    Assertions.assertEquals(200, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());
    Assertions.assertNotNull(responseList.getBody().getData());
    Assertions.assertFalse(responseList.getBody().getData().isEmpty());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
