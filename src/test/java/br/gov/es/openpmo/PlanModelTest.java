package br.gov.es.openpmo;

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
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;

@Testcontainers
@SpringBootTest
public class PlanModelTest extends BaseTest {

    @Autowired
    private PlanModelController planModelController;

    @Autowired
    private OfficeController officeController;

    private Long idOffice;

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
            office.setName("Office Test PlanModel");
            office.setFullName("Office Test PlanModel ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreatePlanModel() {
        PlanModelStoreDto planModel = new PlanModelStoreDto();
        planModel.setName("PlanModel Test");
        planModel.setFullName("PlanModel Test ADM ");
        planModel.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdatePlanModel() {
        PlanModelStoreDto planModel = new PlanModelStoreDto();
        planModel.setName("PlanModel Test update");
        planModel.setFullName("PlanModel Test update ");
        planModel.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        PlanModelUpdateDto planModelUpdate = new PlanModelUpdateDto();
        planModelUpdate.setId(response.getBody().getData().getId());
        planModelUpdate.setName("PlanModel updated");
        response = planModelController.update(planModelUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        PlanModelStoreDto planModel = new PlanModelStoreDto();
        planModel.setName("PlanModel Test delete");
        planModel.setFullName("PlanModel Test delete ");
        planModel.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = planModelController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        PlanModelStoreDto planModel = new PlanModelStoreDto();
        planModel.setName("PlanModel Test list");
        planModel.setFullName("PlanModel Test list ");
        planModel.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<PlanModelDto>>> responseList = planModelController.indexBase(idOffice);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        PlanModelStoreDto planModel = new PlanModelStoreDto();
        planModel.setName("PlanModel Test find");
        planModel.setFullName("PlanModel Test find ");
        planModel.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = planModelController.save(planModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<PlanModelDto>> responseFind = planModelController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("PlanModel Test find", responseFind.getBody().getData().getName());
    }

}
