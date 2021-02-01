package br.gov.es.openpmo;

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

import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModel;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpackmodel.DeliverableModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizerModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.PortfolioModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.ProgramModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;

@Testcontainers
@SpringBootTest
public class WorkpackModelTest extends BaseTest {

    @Autowired
    private WorkpackModelController workpackModelController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private PlanModelController planModelController;

    @Autowired
    private DomainController domainController;

    private Long idOffice;
    private Long idDomain;
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
            office.setName("Office Test WorkpackModel");
            office.setFullName("Office Test WorkpackModel ");
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
        if (this.idDomain == null) {
            DomainStoreDto domain = new DomainStoreDto();
            domain.setName("Domain Test");
            domain.setFullName("Domain Test ADM ");
            domain.setIdOffice(idOffice);
            ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idDomain = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateWorkpackModel() {
        List<Long> ids = getListWorkpackModelId();
        Assertions.assertFalse(ids.isEmpty());
    }



    @Test
    public void shouldUpdateWorkpackModel() {
        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test update", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());

        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());

        workpackModel.setId(response.getBody().getData().getId());
        workpackModel.setModelName("Model test updated");
        workpackModel.setProperties(responseFind.getBody().getData().getProperties());
        response = workpackModelController.update(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldListAll() {
        ResponseEntity<ResponseBaseWorkpackModel> responseList = workpackModelController.indexBase(-1L);
        Assertions.assertEquals(204, responseList.getStatusCodeValue());
        Assertions.assertNull(responseList.getBody());

        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test list", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());

        responseList = workpackModelController.indexBase(idPlanModel);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());

    }

    @Test
    public void shouldDelete() {
        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test delete", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = workpackModelController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldFindOne() {
        getListWorkpackModelId().forEach(id -> {
            ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(id);
            Assertions.assertEquals(200, responseFind.getStatusCodeValue());
            Assertions.assertNotNull(responseFind.getBody());
            Assertions.assertNotNull(responseFind.getBody().getData());
        });
    }

    @Test
    public void shouldReturnTrue() {
        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test parent", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<Boolean>> responseIsParent = workpackModelController.parentProject(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseIsParent.getStatusCodeValue());
        Assertions.assertNotNull(responseIsParent.getBody());
        Assertions.assertTrue(responseIsParent.getBody().getData());
    }
    
    private List<Long> getListWorkpackModelId() {
        List<Long> ids = new ArrayList<>();
        WorkpackModelParamDto workpackModelParam = getWorkpackModelParamDto(null, new PortfolioModelParamDto(), "Portifolio", "fa-edit", idPlanModel);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        workpackModelParam = getWorkpackModelParamDto(response.getBody().getData().getId(), new OrganizerModelParamDto(), "Organizer", "fa-folder", idPlanModel);
        response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        workpackModelParam = getWorkpackModelParamDto(response.getBody().getData().getId(), new ProgramModelParamDto(), "Program", "fa-edit", idPlanModel);
        response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        workpackModelParam = getWorkpackModelParamDto(response.getBody().getData().getId(), new DeliverableModelParamDto(), "Deliverable", "fa", idPlanModel);
        response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        workpackModelParam = getWorkpackModelParamDto(response.getBody().getData().getId(), new MilestoneModelParamDto(), "Milestone", "fa", idPlanModel);
        response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test creat", idPlanModel, idDomain);
        response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());
        
        return ids;
    }

}
