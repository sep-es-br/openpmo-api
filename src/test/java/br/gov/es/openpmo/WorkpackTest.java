package br.gov.es.openpmo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.WorkpackController;
import br.gov.es.openpmo.controller.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpack;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackDetail;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpack.DeliverableParamDto;
import br.gov.es.openpmo.dto.workpack.MilestoneParamDto;
import br.gov.es.openpmo.dto.workpack.OrganizerParamDto;
import br.gov.es.openpmo.dto.workpack.PortfolioParamDto;
import br.gov.es.openpmo.dto.workpack.ProgramParamDto;
import br.gov.es.openpmo.dto.workpack.ProjectParamDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.DeliverableModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.MilestoneModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.OrganizerModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.PortfolioModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.ProgramModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.PropertyModel;
import br.gov.es.openpmo.model.Workpack;
import br.gov.es.openpmo.model.WorkpackModel;
import br.gov.es.openpmo.model.domain.Session;
import br.gov.es.openpmo.service.WorkpackService;

@Testcontainers
@SpringBootTest
public class WorkpackTest extends BaseTest {

    @Autowired
    private WorkpackModelController workpackModelController;

    @Autowired
    private PlanController planController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private PlanModelController planModelController;

    @Autowired
    private DomainController domainController;

    @Autowired
    private WorkpackController workpackController;

    @Autowired
    private WorkpackService workpackService;

    @Autowired
    private ModelMapper modelMapper;

    private Long idOffice;
    private Long idPlanModel;
    private Long idDomain;
    private Long idPlan;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void load() {
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
        if (this.idPlan == null) {
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
            this.idPlan = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateWorkpack() {
        List<Long> ids = getListIdsWorkpacks();
        Assertions.assertFalse(ids.isEmpty());
    }

    @Test
    public void shouldUpdateWorkpack() {
        Long idWorkpackProject = getWorkpackProjectId();
        ResponseEntity<ResponseBaseWorkpackDetail> responseFind = workpackController.find(idWorkpackProject,
                                                                                          getToken(true));
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        WorkpackDetailDto detailDto = responseFind.getBody().getData();
        WorkpackParamDto paramDto = new ProjectParamDto();
        paramDto.setId(detailDto.getId());
        paramDto.setIdPlan(idPlan);
        paramDto.setIdWorkpackModel(detailDto.getModel().getId());
        paramDto.setProperties(detailDto.getProperties());
        PropertyDto property = paramDto.getProperties().stream().filter(
            p -> p.getClass().getTypeName().equals("br.gov.es.openpmo.dto.workpack.TextDto")).findFirst().orElse(null);
        Assertions.assertNotNull(property);
        ((TextDto) property).setValue("New value");
        ResponseEntity<ResponseBase<EntityDto>> response = workpackController.update(paramDto);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldDelete() {
        Long idWorkpackProject = getWorkpackProjectId();
        ResponseEntity<Void> response = workpackController.delete(idWorkpackProject);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        List<Long> ids = getListIdsWorkpacks();

        ResponseEntity<ResponseBaseWorkpack> responseFind = workpackController.indexBase(idPlan, idPlanModel, null,getToken(false));
        Assertions.assertEquals(204, responseFind.getStatusCodeValue());
        responseFind = workpackController.indexBase(idPlan, idPlanModel, null,getToken(true));
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());

        responseFind = workpackController.indexBase(idPlan, idPlanModel, null, ids.get(0), getToken(false));
        Assertions.assertEquals(204, responseFind.getStatusCodeValue());
        responseFind = workpackController.indexBase(idPlan, idPlanModel, null, ids.get(0), getToken(true));
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());

    }

    @Test
    public void shouldFindOne() {
        Long idWorkpackProject = getWorkpackProjectId();
        ResponseEntity<ResponseBaseWorkpackDetail> responseFind = workpackController.find(idWorkpackProject,
                                                                                          getToken(true));
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());

    }

    @Test
    public void shouldFindProperty() {
        Long idWorkpackProject = getWorkpackProjectId();
        ResponseEntity<ResponseBaseWorkpackDetail> responseFind = workpackController.find(idWorkpackProject,
                                                                                          getToken(true));
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        WorkpackDetailDto detailDto = responseFind.getBody().getData();
        Workpack workpack = workpackService.findById(detailDto.getId());
        detailDto.getModel().getProperties().stream().filter(
            p -> Session.PROPERTIES.equals(p.getSession()) && !p.getClass().getSimpleName().contains("LocalitySelectio")
                && !p.getClass().getSimpleName().contains("OrganizationSelection")
                && !p.getClass().getSimpleName().contains("UnitSelection")).forEach(p -> {
            PropertyModel propertyModel = modelMapper.map(p, PropertyModel.class);
            Object property = workpackService.getValueProperty(workpack, propertyModel);
            Assertions.assertNotNull(property);
        });

    }

    @Test
    public void shouldFindModel() {
        List<Long> ids = getListIdsWorkpacks();
        ids.forEach(id -> {
            WorkpackModel workpackModel = workpackService.getWorkpackModelByParent(id);
            Assertions.assertNotNull(workpackModel);
        });
    }

    private Long getIdWorkpackModel(WorkpackModelParamDto workpackModelParam, Long idParent) {
        workpackModelParam = getWorkpackModelParamDto(idParent, workpackModelParam, "Portifolio", "fa", idPlanModel);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        return response.getBody().getData().getId();
    }

    private List<Long> getListIdsWorkpacks() {
        List<Long> ids = new ArrayList<>();
        Long idModel = getIdWorkpackModel(new OrganizerModelParamDto(), null);
        WorkpackParamDto workpackParam = new OrganizerParamDto();
        workpackParam.setIdPlan(idPlan);
        workpackParam.setIdWorkpackModel(idModel);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        idModel = getIdWorkpackModel(new PortfolioModelParamDto(), idModel);
        workpackParam = new PortfolioParamDto();
        workpackParam.setIdWorkpackModel(idModel);
        workpackParam.setIdPlan(idPlan);
        workpackParam.setIdParent(response.getBody().getData().getId());
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        idModel = getIdWorkpackModel(new ProgramModelParamDto(), null);
        workpackParam = new ProgramParamDto();
        workpackParam.setIdPlan(idPlan);
        workpackParam.setIdWorkpackModel(idModel);
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        idModel = getIdWorkpackModel(new DeliverableModelParamDto(), null);
        workpackParam = new DeliverableParamDto();
        workpackParam.setIdPlan(idPlan);
        workpackParam.setIdWorkpackModel(idModel);
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        idModel = getIdWorkpackModel(new MilestoneModelParamDto(), null);
        workpackParam = new MilestoneParamDto();
        workpackParam.setIdPlan(idPlan);
        workpackParam.setIdWorkpackModel(idModel);
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ids.add(response.getBody().getData().getId());

        ids.add(getWorkpackProjectId());
        return ids;
    }

    private Long getWorkpackProjectId() {
        WorkpackModelParamDto workpackModel = getWorkpackModelParamProject("Model test plan", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModel);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        Long idModel = response.getBody().getData().getId();

        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(idModel);
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());

        WorkpackParamDto workpackParam = getWorkpackParamProject(responseFind.getBody().getData());
        workpackParam.setIdPlan(idPlan);
        response = workpackController.save(workpackParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        return response.getBody().getData().getId();
    }

}
