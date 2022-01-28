package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.filters.FilterWorkpackController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.workpack.WorkpackController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.DeliverableModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.MilestoneModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.OrganizerModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.PortfolioModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.ProgramModelParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.enumerator.Session;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest
class WorkpackTest extends BaseTest {

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
  private FilterWorkpackController filterWorkpackController;

  @Autowired
  private ModelMapper modelMapper;

  private Long idOffice;
  private Long idPlanModel;
  private Long idDomain;
  private Long idPlan;
  private Long idFilter;

  @BeforeEach void load() {
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
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlanModel = response.getBody().getData().getId();
    }
    if(this.idDomain == null) {
      final DomainStoreDto domain = new DomainStoreDto();
      domain.setName("Domain Test");
      domain.setFullName("Domain Test ADM ");
      domain.setIdOffice(this.idOffice);
      final LocalityStoreDto localityRoot = new LocalityStoreDto();
      domain.setLocalityRoot(localityRoot);
      localityRoot.setName("Locality Root");
      localityRoot.setType(LocalityTypesEnum.STATE);
      localityRoot.setFullName("Locality Root");
      final ResponseEntity<ResponseBase<EntityDto>> response = this.domainController.save(domain);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
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
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());
      this.idPlan = response.getBody().getData().getId();
    }
    if(this.idFilter == null) {
      final CustomFilterDto filter = new CustomFilterDto();
      filter.setName("Filter");
      filter.setFavorite(false);
      filter.setSortBy(null);
      filter.setSortByDirection(null);

      final CustomFilterRulesDto rule = new CustomFilterRulesDto();
      rule.setValue("a");
      rule.setLogicOperator(LogicOperatorEnum.OR);
      rule.setOperator(GeneralOperatorsEnum.MAIOR_IGUAL);
      rule.setPropertyName("name");

      filter.setRules(singletonList(rule));

      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterWorkpackController.save(null, filter);
      Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      Assertions.assertNotNull(response.getBody());
      Assertions.assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  @Test void shouldCreateWorkpack() {
    final List<Long> ids = this.getListIdsWorkpacks();
    Assertions.assertFalse(ids.isEmpty());
  }

  private List<Long> getListIdsWorkpacks() {
    final List<Long> ids = new ArrayList<>();
    Long idModel = this.getIdWorkpackModel(new OrganizerModelParamDto(), null);
    WorkpackParamDto workpackParam = new OrganizerParamDto();
    workpackParam.setIdPlan(this.idPlan);
    workpackParam.setIdWorkpackModel(idModel);
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    idModel = this.getIdWorkpackModel(new PortfolioModelParamDto(), idModel);
    workpackParam = new PortfolioParamDto();
    workpackParam.setIdWorkpackModel(idModel);
    workpackParam.setIdPlan(this.idPlan);
    workpackParam.setIdParent(response.getBody().getData().getId());
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    idModel = this.getIdWorkpackModel(new ProgramModelParamDto(), null);
    workpackParam = new ProgramParamDto();
    workpackParam.setIdPlan(this.idPlan);
    workpackParam.setIdWorkpackModel(idModel);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    idModel = this.getIdWorkpackModel(new DeliverableModelParamDto(), null);
    workpackParam = new DeliverableParamDto();
    workpackParam.setIdPlan(this.idPlan);
    workpackParam.setIdWorkpackModel(idModel);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    idModel = this.getIdWorkpackModel(new MilestoneModelParamDto(), null);
    workpackParam = new MilestoneParamDto();
    workpackParam.setIdPlan(this.idPlan);
    workpackParam.setIdWorkpackModel(idModel);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    ids.add(response.getBody().getData().getId());

    ids.add(this.getWorkpackProjectId());
    return ids;
  }

  private Long getIdWorkpackModel(WorkpackModelParamDto workpackModelParam, final Long idParent) {
    workpackModelParam = this.getWorkpackModelParamDto(
      idParent,
      workpackModelParam,
      "Portifolio",
      "fa",
      this.idPlanModel
    );
    final ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    return response.getBody().getData().getId();
  }

  private Long getWorkpackProjectId() {
    final WorkpackModelParamDto workpackModel = this.getWorkpackModelParamProject(
      "Model test plan",
      this.idPlanModel,
      this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModel);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    final Long idModel = response.getBody().getData().getId();

    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(idModel);
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

    final WorkpackParamDto workpackParam = this.getWorkpackParamProject(responseFind.getBody().getData());
    workpackParam.setIdPlan(this.idPlan);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    return response.getBody().getData().getId();
  }

  @Test void shouldUpdateWorkpack() {
    final Long idWorkpackProject = this.getWorkpackProjectId();
    final ResponseEntity<ResponseBaseWorkpackDetail> responseFind = this.workpackController.find(
      idWorkpackProject,
      this.idPlan,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    final WorkpackDetailDto detailDto = responseFind.getBody().getData();
    final WorkpackParamDto paramDto = new ProjectParamDto();
    paramDto.setId(detailDto.getId());
    paramDto.setIdPlan(this.idPlan);
    paramDto.setIdWorkpackModel(detailDto.getModel().getId());
    paramDto.setProperties(detailDto.getProperties());
    final PropertyDto property = paramDto.getProperties().stream().filter(
      p -> p.getClass().getTypeName().equals("br.gov.es.openpmo.dto.workpack.TextDto")).findFirst().orElse(null);
    Assertions.assertNotNull(property);
    ((TextDto) property).setValue("New value");
    final ResponseEntity<ResponseBase<EntityDto>> response = this.workpackController.update(paramDto, this.getToken(true));
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
  }

  @Test void shouldDelete() {
    final Long idWorkpackProject = this.getWorkpackProjectId();
    final ResponseEntity<Void> response = this.workpackController.delete(idWorkpackProject);
    Assertions.assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    final List<Long> ids = this.getListIdsWorkpacks();

    ResponseEntity<ResponseBaseWorkpack> responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      null,
      this.getToken(false)
    );
    Assertions.assertEquals(204, responseFind.getStatusCodeValue());
    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      null,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      ids.get(0),
      null,
      this.getToken(false)
    );
    Assertions.assertEquals(204, responseFind.getStatusCodeValue());
    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      ids.get(0),
      null,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

  }

  public void shouldListAllUsingCustomFilter() {
    final List<Long> ids = this.getListIdsWorkpacks();

    ResponseEntity<ResponseBaseWorkpack> responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      this.idFilter,
      this.getToken(false)
    );
    Assertions.assertEquals(204, responseFind.getStatusCodeValue());
    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      this.idFilter,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      ids.get(0),
      this.idFilter,
      this.getToken(false)
    );
    Assertions.assertEquals(204, responseFind.getStatusCodeValue());
    responseFind = this.workpackController.indexBase(
      this.idPlan,
      this.idPlanModel,
      null,
      ids.get(0),
      this.idFilter,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

  }

  @Test void shouldFindOne() {
    final Long idWorkpackProject = this.getWorkpackProjectId();
    final ResponseEntity<ResponseBaseWorkpackDetail> responseFind = this.workpackController.find(
      idWorkpackProject,
      this.idPlan,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());

  }

  @Test void shouldFindProperty() {
    final Long idWorkpackProject = this.getWorkpackProjectId();
    final ResponseEntity<ResponseBaseWorkpackDetail> responseFind = this.workpackController.find(
      idWorkpackProject,
      this.idPlan,
      this.getToken(true)
    );
    Assertions.assertEquals(HTTP_STATUS_OK, responseFind.getStatusCodeValue());
    Assertions.assertNotNull(responseFind.getBody());
    Assertions.assertNotNull(responseFind.getBody().getData());
    final WorkpackDetailDto detailDto = responseFind.getBody().getData();
    final Workpack workpack = this.workpackService.findById(detailDto.getId());
    detailDto.getModel().getProperties().stream().filter(
      p -> Session.PROPERTIES.equals(p.getSession()) && !p.getClass().getSimpleName().contains("LocalitySelectio")
           && !p.getClass().getSimpleName().contains("OrganizationSelection")
           && !p.getClass().getSimpleName().contains("UnitSelection")).forEach(p -> {
      final PropertyModel propertyModel = this.modelMapper.map(p, PropertyModel.class);
      final Object property = WorkpackService.getValueProperty(workpack, propertyModel);
      Assertions.assertNotNull(property);
    });

  }

  @Test void shouldFindModel() {
    final List<Long> ids = this.getListIdsWorkpacks();
    ids.forEach(id -> {
      final WorkpackModel workpackModel = this.workpackService.getWorkpackModelByParent(id);
      Assertions.assertNotNull(workpackModel);
    });
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
