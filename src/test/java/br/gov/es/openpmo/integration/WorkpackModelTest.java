package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpackmodel.ResponseBaseWorkpackModel;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.DeliverableModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.MilestoneModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.OrganizerModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.PortfolioModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.ProgramModelParamDto;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest class WorkpackModelTest extends BaseTest {

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
  }

  @Test void shouldCreateWorkpackModel() {
    final List<Long> ids = this.getListWorkpackModelId();
    Assertions.assertFalse(ids.isEmpty());
  }

  private List<Long> getListWorkpackModelId() {
    final List<Long> ids = new ArrayList<>();
    WorkpackModelParamDto workpackModelParam = this.getWorkpackModelParamDto(
      null,
      new PortfolioModelParamDto(),
      "Portifolio",
      "fa-edit",
      this.idPlanModel
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    workpackModelParam = this.getWorkpackModelParamDto(
      response.getBody().getData().getId(),
      new OrganizerModelParamDto(),
      "Organizer",
      "fa-folder",
      this.idPlanModel
    );
    response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    workpackModelParam = this.getWorkpackModelParamDto(
      response.getBody().getData().getId(),
      new ProgramModelParamDto(),
      "Program",
      "fa-edit",
      this.idPlanModel
    );
    response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    workpackModelParam = this.getWorkpackModelParamDto(
      response.getBody().getData().getId(),
      new DeliverableModelParamDto(),
      "Deliverable",
      "fa",
      this.idPlanModel
    );
    response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    workpackModelParam = this.getWorkpackModelParamDto(
      response.getBody().getData().getId(),
      new MilestoneModelParamDto(),
      "Milestone",
      "fa",
      this.idPlanModel
    );
    response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject("Model test creat", this.idPlanModel,
                                                                                  this.idDomain
    );
    response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    return ids;
  }

  @Test void shouldUpdateWorkpackModel() {
    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject("Model test update", this.idPlanModel,
                                                                                  this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());

    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(response.getBody().getData().getId());
    Assertions.assertEquals(200, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());

    workpackModel.setId(response.getBody().getData().getId());
    workpackModel.setModelName("Model test updated");
    workpackModel.setProperties(responseFind.getBody().getData().getProperties());
    response = this.workpackModelController.update(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
  }

  @Test void shouldListAll() {
    ResponseEntity<ResponseBaseWorkpackModel> responseList = this.workpackModelController.indexBase(-1L);
    Assertions.assertEquals(204, responseList.getStatusCodeValue());
    Assertions.assertNull(responseList.getBody());

    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject("Model test list", this.idPlanModel,
                                                                                  this.idDomain
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());

    responseList = this.workpackModelController.indexBase(this.idPlanModel);
    Assertions.assertEquals(200, responseList.getStatusCodeValue());
    Assertions.assertNotNull(responseList.getBody());

  }

  @Test void shouldDelete() {
    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject("Model test delete", this.idPlanModel,
                                                                                  this.idDomain
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<Void> responseDelete = this.workpackModelController.delete(response.getBody().getData().getId(), null);
    Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
  }

  @Test void shouldFindOne() {
    this.getListWorkpackModelId().forEach(id -> {
      final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(id);
      Assertions.assertEquals(200, responseFind.getStatusCodeValue());
      Assertions.assertNotNull(responseFind.getBody());
      Assertions.assertNotNull(responseFind.getBody().getData());
    });
  }

  @Test void shouldReturnTrue() {
    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject("Model test parent", this.idPlanModel,
                                                                                  this.idDomain
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final ResponseEntity<ResponseBase<Boolean>> responseIsParent = this.workpackModelController.parentProject(response.getBody().getData().getId());
    Assertions.assertEquals(200, responseIsParent.getStatusCodeValue());
    Assertions.assertNotNull(responseIsParent.getBody());
    Assertions.assertTrue(responseIsParent.getBody().getData());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
