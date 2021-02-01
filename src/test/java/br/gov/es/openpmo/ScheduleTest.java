package br.gov.es.openpmo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
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

import br.gov.es.openpmo.controller.CostAccountController;
import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.ScheduleController;
import br.gov.es.openpmo.controller.WorkpackController;
import br.gov.es.openpmo.controller.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.costaccount.CostAccountStoreDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.schedule.CostSchedule;
import br.gov.es.openpmo.dto.schedule.ScheduleDto;
import br.gov.es.openpmo.dto.schedule.ScheduleParamDto;
import br.gov.es.openpmo.dto.workpack.DateDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.TextDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.domain.Session;

@Testcontainers
@SpringBootTest
public class ScheduleTest extends BaseTest {

    @Autowired
    private DomainController domainController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private WorkpackModelController workpackModelController;

    @Autowired
    private PlanController planController;

    @Autowired
    private PlanModelController planModelController;

    @Autowired
    private WorkpackController workpackController;

    @Autowired
    private ScheduleController scheduleController;

    @Autowired
    private CostAccountController costAccountController;

    private Long idOffice;
    private Long idPlanModel;
    private Long idDomain;
    private Long idPlan;
    private Long idWorkpack;
    private Long idWorkpackModel;
    private Long idCostAccount;

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
        if (idWorkpack == null) {
            idWorkpack = getIdWorkpack();
        }
        if(idCostAccount == null) {
            CostAccountStoreDto costAccount = new CostAccountStoreDto();
            costAccount.setIdWorkpack(idWorkpack);
            costAccount.setProperties(getProperties());
            ResponseEntity<ResponseBase<EntityDto>> response = costAccountController.save(costAccount);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            idCostAccount = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateSchedule() {
        ScheduleParamDto schedule = new ScheduleParamDto();
        schedule.setIdWorkpack(idWorkpack);
        schedule.setActualWork(BigDecimal.ZERO);
        schedule.setPlannedWork(new BigDecimal("120000"));
        schedule.setStart(LocalDate.of(2021,1,1));
        schedule.setEnd(LocalDate.of(2021,12,31));
        CostSchedule costSchedule = new CostSchedule();
        costSchedule.setId(idCostAccount);
        costSchedule.setPlannedCost(new BigDecimal("10000"));
        costSchedule.setActualCost(BigDecimal.ZERO);
        schedule.setCosts(new HashSet<>());
        schedule.getCosts().add(costSchedule);
        ResponseEntity<ResponseBase<EntityDto>> response = scheduleController.save(schedule);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    private List<PropertyDto> getProperties() {
        List<PropertyDto> properties = new ArrayList<>();
        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(this.idWorkpackModel);
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        responseFind.getBody().getData().getProperties().stream().filter(
            p -> Session.COST.equals(p.getSession())).forEach(property -> {
                switch (property.getClass().getTypeName()) {
                    case "br.gov.es.openpmo.dto.workpackmodel.TextModelDto":
                        TextDto textDto = new TextDto();
                        textDto.setIdPropertyModel(property.getId());
                        textDto.setValue("Text cost");
                        properties.add(textDto);
                        break;
                    case "br.gov.es.openpmo.dto.workpackmodel.DateModelDto":
                        DateDto dateDto = new DateDto();
                        dateDto.setIdPropertyModel(property.getId());
                        dateDto.setValue(LocalDateTime.now());
                        properties.add(dateDto);
                        break;
                    default:
                        break;
                }
        });

        return properties;
    }

    @Test
    public void shouldDelete() {
        ScheduleParamDto schedule = new ScheduleParamDto();
        schedule.setIdWorkpack(idWorkpack);
        schedule.setActualWork(BigDecimal.ZERO);
        schedule.setPlannedWork(new BigDecimal("120000"));
        schedule.setStart(LocalDate.of(2021,1,1));
        schedule.setEnd(LocalDate.of(2021,12,31));
        ResponseEntity<ResponseBase<EntityDto>> response = scheduleController.save(schedule);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = scheduleController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());

    }

    @Test
    public void shouldListAll() {
        ScheduleParamDto schedule = new ScheduleParamDto();
        schedule.setIdWorkpack(idWorkpack);
        schedule.setActualWork(BigDecimal.ZERO);
        schedule.setPlannedWork(new BigDecimal("120000"));
        schedule.setStart(LocalDate.of(2020,1,1));
        schedule.setEnd(LocalDate.of(2020,12,31));
        ResponseEntity<ResponseBase<EntityDto>> response = scheduleController.save(schedule);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<List<ScheduleDto>>> responseList = scheduleController.indexBase(idWorkpack);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
    }

    @Test
    public void shouldFindOne() {
        ScheduleParamDto schedule = new ScheduleParamDto();
        schedule.setIdWorkpack(idWorkpack);
        schedule.setActualWork(BigDecimal.ZERO);
        schedule.setPlannedWork(new BigDecimal("120000"));
        schedule.setStart(LocalDate.of(2022,1,1));
        schedule.setEnd(LocalDate.of(2022,12,31));
        ResponseEntity<ResponseBase<EntityDto>> response = scheduleController.save(schedule);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());

        ResponseEntity<ResponseBase<ScheduleDto>> responseFind = scheduleController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
    }

    private Long getIdWorkpack() {
        WorkpackModelParamDto workpackModelParam = getWorkpackModelParamProject("Project", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        this.idWorkpackModel = response.getBody().getData().getId();
        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(this.idWorkpackModel);
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
