package br.gov.es.openpmo;

import java.time.LocalDate;
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
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.plan.PlanUpdateDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;

@Testcontainers
@SpringBootTest
public class PlanTest extends BaseTest {

    @Autowired
    private PlanController planController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private PlanModelController planModelController;

    private Long idOffice;
    private Long idPlanModel;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void loadOffice() {
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
    }

    @Test
    public void shouldCreatePlan() {
        PlanStoreDto plan = new PlanStoreDto();
        plan.setName("Plan Test");
        plan.setFullName("Plan Test ADM ");
        plan.setIdOffice(idOffice);
        plan.setIdPlanModel(idPlanModel);
        plan.setStart(LocalDate.now().minusMonths(2));
        plan.setFinish(LocalDate.now().plusMonths(2));
        ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdatePlan() {
        PlanStoreDto plan = new PlanStoreDto();
        plan.setName("Plan Test update");
        plan.setFullName("Plan Test update ");
        plan.setIdOffice(idOffice);
        plan.setIdPlanModel(idPlanModel);
        plan.setStart(LocalDate.now().minusMonths(2));
        plan.setFinish(LocalDate.now().plusMonths(2));
        ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        PlanUpdateDto planUpdate = new PlanUpdateDto();
        planUpdate.setId(response.getBody().getData().getId());
        planUpdate.setName("Plan updated");
        response = planController.update(planUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        PlanStoreDto plan = new PlanStoreDto();
        plan.setName("Plan Test delete");
        plan.setFullName("Plan Test delete ");
        plan.setIdOffice(idOffice);
        plan.setIdPlanModel(idPlanModel);
        plan.setStart(LocalDate.now().minusMonths(2));
        plan.setFinish(LocalDate.now().plusMonths(2));
        ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = planController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        PlanStoreDto plan = new PlanStoreDto();
        plan.setName("Plan Test list");
        plan.setFullName("Plan Test list ");
        plan.setIdOffice(idOffice);
        plan.setIdPlanModel(idPlanModel);
        plan.setStart(LocalDate.now().minusMonths(2));
        plan.setFinish(LocalDate.now().plusMonths(2));
        ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<PlanDto>>> responseList = planController.indexBase(idOffice, getToken(true));
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        PlanStoreDto plan = new PlanStoreDto();
        plan.setName("Plan Test find");
        plan.setFullName("Plan Test find ");
        plan.setIdOffice(idOffice);
        plan.setIdPlanModel(idPlanModel);
        plan.setStart(LocalDate.now().minusMonths(2));
        plan.setFinish(LocalDate.now().plusMonths(2));
        ResponseEntity<ResponseBase<EntityDto>> response = planController.save(plan);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<PlanDto>> responseFind = planController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("Plan Test find", responseFind.getBody().getData().getName());
    }




}
