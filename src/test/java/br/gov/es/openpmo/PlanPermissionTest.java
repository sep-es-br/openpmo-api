package br.gov.es.openpmo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.PlanPermissionController;
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

@Testcontainers
@SpringBootTest
public class PlanPermissionTest extends BaseTest {

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

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void loadPlan() {
        if (this.idOffice == null) {
            OfficeStoreDto office = new OfficeStoreDto();
            office.setName("Office Test Plan");
            office.setFullName("Office Test Plan ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
        if (this.idPlanModel == null) {
            PlanModelStoreDto planModel = new PlanModelStoreDto();
            planModel.setName("PlanModel Test list");
            planModel.setFullName("PlanModel Test list ");
            planModel.setIdOffice(idOffice);
            ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idPlanModel = response.getBody().getData().getId();
        }
        if (this.idPlan == null) {
            PlanStoreDto plan = new PlanStoreDto();
            plan.setName("Plan Test PlanPermission");
            plan.setFullName("Plan Test PlanPermission ");
            plan.setIdOffice(idOffice);
            plan.setIdPlanModel(idPlanModel);
            plan.setStart(LocalDate.now().minusMonths(2));
            plan.setFinish(LocalDate.now().plusMonths(2));
            ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idPlan = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreatePlanPermission() {
        PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
        planPermission.setEmail("plan.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        planPermission.setPermissions(new ArrayList<>());
        planPermission.getPermissions().add(permissionDto);
        planPermission.setIdPlan(idPlan);
        ResponseEntity<ResponseBase<Entity>> response = planPermissionController.store(planPermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void shouldUpdatePlanPermission() {
        PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
        planPermission.setEmail("plan.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        planPermission.setPermissions(new ArrayList<>());
        planPermission.getPermissions().add(permissionDto);
        planPermission.setIdPlan(idPlan);
        ResponseEntity<ResponseBase<Entity>> response = planPermissionController.store(planPermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        PlanPermissionParamDto planPermissionUpdate = new PlanPermissionParamDto();
        planPermissionUpdate.setEmail("plan.test@openpmo.com");
        planPermissionUpdate.setIdPlan(idPlan);
        response = planPermissionController.update(planPermissionUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    public void shouldDelete() {
        PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
        planPermission.setEmail("plan.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        planPermission.setPermissions(new ArrayList<>());
        planPermission.getPermissions().add(permissionDto);
        planPermission.setIdPlan(idPlan);
        ResponseEntity<ResponseBase<Entity>> response = planPermissionController.store(planPermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        ResponseEntity<Void> responseDelete = planPermissionController.delete(idPlan, "plan.test@openpmo.com");
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        PlanPermissionParamDto planPermission = new PlanPermissionParamDto();
        planPermission.setEmail("plan.test@openpmo.com");
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setLevel(PermissionLevelEnum.EDIT);
        permissionDto.setRole("roleTest");
        planPermission.setPermissions(new ArrayList<>());
        planPermission.getPermissions().add(permissionDto);
        planPermission.setIdPlan(idPlan);
        ResponseEntity<ResponseBase<Entity>> response = planPermissionController.store(planPermission);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<PlanPermissionDto>>> responseList = planPermissionController.indexBase(idPlan, null);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

}
