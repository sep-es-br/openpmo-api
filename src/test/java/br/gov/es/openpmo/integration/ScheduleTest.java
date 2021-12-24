package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.schedule.ScheduleController;
import br.gov.es.openpmo.controller.workpack.CostAccountController;
import br.gov.es.openpmo.controller.workpack.WorkpackController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
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
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.enumerator.Session;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Testcontainers
@SpringBootTest class ScheduleTest extends BaseTest {

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

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test WorkpackModel");
      office.setFullName("Office Test WorkpackModel ");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.officeController.save(office);
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
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
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idPlanModel = response.getBody().getData().getId();
    }
    if(this.idDomain == null) {
      final DomainStoreDto domain = new DomainStoreDto();
      domain.setName("Domain Test");
      domain.setFullName("Domain Test ADM ");
      domain.setIdOffice(this.idOffice);
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idDomain = response.getBody().getData().getId();
    }
    if(this.idPlan == null) {
      final PlanStoreDto plan = new PlanStoreDto();
      plan.setName("Plan Test");
      plan.setFullName("Plan Test ADM ");
      plan.setIdOffice(this.idOffice);
      plan.setIdPlanModel(this.idPlanModel);
      plan.setStart(LocalDate.now().minusMonths(2));
      plan.setFinish(LocalDate.now().plusMonths(2));
      final ResponseEntity<ResponseBase<EntityDto>> response = this.planController.save(plan);
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idPlan = response.getBody().getData().getId();
    }
    if(this.idWorkpack == null) {
      this.idWorkpack = this.getIdWorkpack();
    }
    if(this.idCostAccount == null) {
      final CostAccountStoreDto costAccount = new CostAccountStoreDto(this.idWorkpack, this.getProperties());
      final ResponseEntity<ResponseBase<EntityDto>> response = this.costAccountController.save(costAccount);
      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idCostAccount = response.getBody().getData().getId();
    }
  }

  private List<PropertyDto> getProperties() {
    final List<PropertyDto> properties = new ArrayList<>();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(this.idWorkpackModel);
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());
    responseFind.getBody().getData().getProperties().stream().filter(
      p -> Session.COST.equals(p.getSession())).forEach(property -> {
      switch(property.getClass().getTypeName()) {
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.TextModelDto":
          final TextDto textDto = new TextDto();
          textDto.setIdPropertyModel(property.getId());
          textDto.setValue("Text cost");
          properties.add(textDto);
          break;
        case "br.gov.es.openpmo.dto.workpackmodel.params.properties.DateModelDto":
          final DateDto dateDto = new DateDto();
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

  private Long getIdWorkpack() {
    final WorkpackModelParamDto workpackModelParam = this.getWorkpackModelParamProject("Project",
                                                                                       this.idPlanModel, this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    this.idWorkpackModel = response.getBody().getData().getId();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(this.idWorkpackModel);
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());

    final WorkpackParamDto workpackParam = this.getWorkpackParamProject(responseFind.getBody().getData());
    workpackParam.setIdPlan(this.idPlan);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    return response.getBody().getData().getId();
  }

  @Test void shouldCreateSchedule() {
    final ScheduleParamDto schedule = new ScheduleParamDto();
    schedule.setIdWorkpack(this.idWorkpack);
    schedule.setActualWork(BigDecimal.ZERO);
    schedule.setPlannedWork(new BigDecimal("120000"));
    schedule.setStart(LocalDate.of(2021, 1, 1));
    schedule.setEnd(LocalDate.of(2021, 12, 31));
    final CostSchedule costSchedule = new CostSchedule();
    costSchedule.setId(this.idCostAccount);
    costSchedule.setPlannedCost(new BigDecimal("10000"));
    costSchedule.setActualCost(BigDecimal.ZERO);
    schedule.setCosts(new HashSet<>());
    schedule.getCosts().add(costSchedule);
    final ResponseEntity<ResponseBase<EntityDto>> response = this.scheduleController.save(schedule);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @Test void shouldDelete() {
    final ScheduleParamDto schedule = new ScheduleParamDto();
    schedule.setIdWorkpack(this.idWorkpack);
    schedule.setActualWork(BigDecimal.ZERO);
    schedule.setPlannedWork(new BigDecimal("120000"));
    schedule.setStart(LocalDate.of(2021, 1, 1));
    schedule.setEnd(LocalDate.of(2021, 12, 31));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.scheduleController.save(schedule);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.scheduleController.delete(response.getBody().getData().getId());
    assertEquals(200, responseDelete.getStatusCodeValue());

  }

  @Test void shouldListAll() {
    final ScheduleParamDto schedule = new ScheduleParamDto();
    schedule.setIdWorkpack(this.idWorkpack);
    schedule.setActualWork(BigDecimal.ZERO);
    schedule.setPlannedWork(new BigDecimal("120000"));
    schedule.setStart(LocalDate.of(2020, 1, 1));
    schedule.setEnd(LocalDate.of(2020, 12, 31));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.scheduleController.save(schedule);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<List<ScheduleDto>>> responseList = this.scheduleController.findAll(this.idWorkpack);
    assertEquals(200, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
  }

  @Test void shouldFindOne() {
    final ScheduleParamDto schedule = new ScheduleParamDto();
    schedule.setIdWorkpack(this.idWorkpack);
    schedule.setActualWork(BigDecimal.ZERO);
    schedule.setPlannedWork(new BigDecimal("120000"));
    schedule.setStart(LocalDate.of(2022, 1, 1));
    schedule.setEnd(LocalDate.of(2022, 12, 31));
    final ResponseEntity<ResponseBase<EntityDto>> response = this.scheduleController.save(schedule);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());

    final ResponseEntity<ResponseBase<ScheduleDto>> responseFind = this.scheduleController.findById(response.getBody().getData().getId());
    assertEquals(200, responseFind.getStatusCodeValue());
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
