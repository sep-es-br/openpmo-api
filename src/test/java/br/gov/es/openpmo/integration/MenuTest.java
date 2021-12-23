package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.ui.MenuController;
import br.gov.es.openpmo.controller.workpack.WorkpackController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseItens;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
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
import java.util.Collections;

@Testcontainers
@SpringBootTest class MenuTest extends BaseTest {

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
  private MenuController menuController;

  private Long idOffice;
  private Long idPlanModel;
  private Long idDomain;
  private Long idPlan;
  private Long idWorkpack;
  private Long idWorkpackModel;

  @BeforeEach void loadOffice() {
    if(this.idOffice == null) {
      final OfficeStoreDto office = new OfficeStoreDto();
      office.setName("Office Test WorkpackModel");
      office.setFullName("Office Test WorkpackModel ");
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
    if(this.idDomain == null) {
      final DomainStoreDto domain = new DomainStoreDto();
      domain.setName("Domain Test");
      domain.setFullName("Domain Test ADM ");
      domain.setIdOffice(this.idOffice);
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      Assertions.assertEquals(200, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
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
      Assertions.assertEquals(200, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlan = response.getBody().getData().getId();
    }
    if(this.idWorkpack == null) {
      this.idWorkpack = this.getIdWorkpack();
    }
  }

  private Long getIdWorkpack() {
    final WorkpackModelParamDto workpackModelParam = this.getWorkpackModelParamProject("Project",
                                                                                       this.idPlanModel, this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    this.idWorkpackModel = response.getBody().getData().getId();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(this.idWorkpackModel);
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

    final WorkpackParamDto workpackParam = this.getWorkpackParamProject(responseFind.getBody().getData());
    workpackParam.setIdPlan(this.idPlan);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    return response.getBody().getData().getId();
  }

  @Test void shouldListAll() {
    final String token = this.getToken(true);
    final ResponseEntity<ResponseBaseItens<MenuOfficeDto>> responseOffice = this.menuController.indexOffice(token);
    Assertions.assertEquals(200, responseOffice.getStatusCodeValue());
    Assertions.assertNotNull(responseOffice.getBody());
    Assertions.assertNotNull(responseOffice.getBody().getData());

    final ResponseEntity<ResponseBaseItens<WorkpackMenuDto>> responsePortfolio = this.menuController.indexPortfolio(
      this.idOffice,
      token
    );
    Assertions.assertEquals(200, responsePortfolio.getStatusCodeValue());
    Assertions.assertNotNull(responsePortfolio.getBody());
    Assertions.assertNotNull(responsePortfolio.getBody().getData());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
