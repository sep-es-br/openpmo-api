package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.actor.OrganizationController;
import br.gov.es.openpmo.controller.filters.FilterStakeholderController;
import br.gov.es.openpmo.controller.office.DomainController;
import br.gov.es.openpmo.controller.office.OfficeController;
import br.gov.es.openpmo.controller.plan.PlanController;
import br.gov.es.openpmo.controller.plan.PlanModelController;
import br.gov.es.openpmo.controller.stakeholder.StakeholderController;
import br.gov.es.openpmo.controller.stakeholder.StakeholderOrganizationController;
import br.gov.es.openpmo.controller.stakeholder.StakeholderPersonController;
import br.gov.es.openpmo.controller.workpack.WorkpackController;
import br.gov.es.openpmo.controller.workpack.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.dto.filter.CustomFilterRulesDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.PersonStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.details.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.workpackmodel.params.WorkpackModelParamDto;
import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import br.gov.es.openpmo.repository.AuthServiceRepository;
import br.gov.es.openpmo.service.actors.PersonService;
import org.jetbrains.annotations.NotNull;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Testcontainers
@SpringBootTest class StakeholderTest extends BaseTest {

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
  private StakeholderController stakeholderController;

  @Autowired
  private StakeholderPersonController stakeholderPersonController;

  @Autowired
  private StakeholderOrganizationController stakeholderOrganizationController;

  @Autowired
  private OrganizationController organizationController;

  @Autowired
  private PersonService personService;

  @Autowired
  private AuthServiceRepository authServiceRepository;

  @Autowired
  private FilterStakeholderController filterStakeholderController;

  private Long idOffice;
  private Long idPlanModel;
  private Long idDomain;
  private Long idPlan;
  private Long idWorkpack;
  private Long idOrganization;
  private Long idFilter;
  private Long idWorkpackModel;
  private AuthService authService;

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
    if(this.authService == null) {
      this.authService = new AuthService();
      this.authService.setServer("AcessoCidadao");
      this.authService.setEndPoint("");
      this.authServiceRepository.save(this.authService);
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
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
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
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
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
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idPlan = response.getBody().getData().getId();
    }
    if(this.idWorkpack == null) {
      this.idWorkpack = this.getIdWorkpack();
    }
    if(this.idOrganization == null) {
      final OrganizationStoreDto organization = new OrganizationStoreDto();
      organization.setName("Organization Test");
      organization.setFullName("Organization Test ADM ");
      organization.setEmail("organization@openpmo.com");
      organization.setIdOffice(this.idOffice);
      final ResponseEntity<ResponseBase<EntityDto>> response = this.organizationController.save(organization);
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());
      this.idOrganization = response.getBody().getData().getId();
    }
    if(this.idFilter == null) {
      final CustomFilterDto filter = new CustomFilterDto();
      filter.setName("Filter");
      filter.setFavorite(false);
      filter.setSortBy(null);
      filter.setSortByDirection(null);

      final CustomFilterRulesDto rule = new CustomFilterRulesDto();
      rule.setValue("");
      rule.setLogicOperator(LogicOperatorEnum.OR);
      rule.setOperator(GeneralOperatorsEnum.DIFERENTE);
      rule.setPropertyName("name");
      filter.setRules(singletonList(rule));
      final ResponseEntity<ResponseBase<CustomFilterDto>> response = this.filterStakeholderController.save(
        this.idWorkpackModel,
        filter
      );
      assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertNotNull(response.getBody().getData());

      this.idFilter = response.getBody().getData().getId();
    }
  }

  private Long getIdWorkpack() {
    final WorkpackModelParamDto workpackModelParam = this.getWorkpackModelParamProject(
      "Project",
      this.idPlanModel,
      this.idDomain
    );
    ResponseEntity<ResponseBase<EntityDto>> response = this.workpackModelController.save(workpackModelParam);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    this.idWorkpackModel = response.getBody().getData().getId();
    final ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = this.workpackModelController.find(
      this.idWorkpackModel
    );
    assertNotNull(responseFind.getBody());
    assertNotNull(responseFind.getBody().getData());

    final WorkpackParamDto workpackParam = this.getWorkpackParamProject(responseFind.getBody().getData());
    workpackParam.setIdPlan(this.idPlan);
    response = this.workpackController.save(workpackParam, this.getToken(true));
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
    return response.getBody().getData().getId();
  }

  @Test void shouldCreateStakeholder() {
    final StakeholderParamDto personStakeholder = new StakeholderParamDto();
    personStakeholder.setIdWorkpack(this.idWorkpack);
    personStakeholder.setIdPlan(this.idPlan);
    personStakeholder.setPerson(this.getPersonStakeholderParamDto(true, "user.test@openpmo.com"));
    personStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<EntityDto>> response = this.stakeholderPersonController.storePerson(
      personStakeholder
    );
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());

    final OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
    organizationStakeholder.setIdWorkpack(this.idWorkpack);
    organizationStakeholder.setIdOrganization(this.idOrganization);
    organizationStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<Entity>> storeOrganizationResponse = this.stakeholderOrganizationController.storeOrganization(
      organizationStakeholder
    );
    assertEquals(HTTP_STATUS_OK, storeOrganizationResponse.getStatusCodeValue());

  }

  @NotNull private PersonStakeholderParamDto getPersonStakeholderParamDto(final boolean isUser, String email) {
    final PersonStakeholderParamDto stakeholderParamDto = new PersonStakeholderParamDto(
      "Stakeholder Test",
      "user.test@openpmo.com"
    );
    stakeholderParamDto.setIsUser(isUser);
    stakeholderParamDto.setEmail(email);
    stakeholderParamDto.setName("Stakeholder Test");
    return stakeholderParamDto;
  }

  @Test void shouldDelete() {
    this.getToken(true);

    final StakeholderParamDto personStakeholder = new StakeholderParamDto();
    personStakeholder.setIdWorkpack(this.idWorkpack);
    personStakeholder.setIdPlan(this.idPlan);
    personStakeholder.setPerson(this.getPersonStakeholderParamDto(true, "user.test@openpmo.com"));
    personStakeholder.setRoles(new ArrayList<>());

    final ResponseEntity<ResponseBase<EntityDto>> response = this.stakeholderPersonController.storePerson(
      personStakeholder
    );
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());

    final Optional<Person> person = this.personService.findByEmail("user.test@openpmo.com");

    assertTrue(person.isPresent());

    ResponseEntity<Void> responseDelete = this.stakeholderPersonController.deletePerson(
      this.idWorkpack,
      person.get().getId(),
      this.idPlan
    );
    assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());

    final OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
    organizationStakeholder.setIdWorkpack(this.idWorkpack);
    organizationStakeholder.setIdOrganization(this.idOrganization);
    organizationStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<Entity>> storeOrganizationResponse = this.stakeholderOrganizationController.storeOrganization(
      organizationStakeholder);
    assertEquals(HTTP_STATUS_OK, storeOrganizationResponse.getStatusCodeValue());
    responseDelete = this.stakeholderOrganizationController.deleteOrganization(
      this.idWorkpack,
      this.idOrganization
    );
    assertEquals(HTTP_STATUS_OK, responseDelete.getStatusCodeValue());
  }

  @Test void shouldListAll() {
    this.getToken(true);
    final StakeholderParamDto personStakeholder = new StakeholderParamDto();
    personStakeholder.setIdWorkpack(this.idWorkpack);
    personStakeholder.setIdPlan(this.idPlan);
    personStakeholder.setPerson(this.getPersonStakeholderParamDto(true, "user.test2@openpmo.com"));
    personStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<EntityDto>> response = this.stakeholderPersonController.storePerson(
      personStakeholder);
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());

    final ResponseEntity<ResponseBase<StakeholderPersonDto>> responsePerson = this.stakeholderPersonController.index(
      this.idWorkpack,
      response.getBody().getData().getId()
    );
    assertEquals(HTTP_STATUS_OK, responsePerson.getStatusCodeValue());
    assertNotNull(responsePerson.getBody());
    assertNotNull(responsePerson.getBody().getData());

    final OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
    organizationStakeholder.setIdWorkpack(this.idWorkpack);
    organizationStakeholder.setIdOrganization(this.idOrganization);
    organizationStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<Entity>> storeOrganizationResponse = this.stakeholderOrganizationController.storeOrganization(
      organizationStakeholder
    );
    assertEquals(HTTP_STATUS_OK, storeOrganizationResponse.getStatusCodeValue());

    final ResponseEntity<ResponseBase<StakeholderOrganizationDto>> responseOrganization = this.stakeholderOrganizationController.index(
      this.idWorkpack, this.idOrganization
    );
    assertEquals(HTTP_STATUS_OK, responseOrganization.getStatusCodeValue());
    assertNotNull(responseOrganization.getBody());
    assertNotNull(responseOrganization.getBody().getData());

    final ResponseEntity<ResponseBase<List<StakeholderDto>>> responseList = this.stakeholderController.index(
      this.idWorkpack,
      null
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
  }

  @Test void shouldListAllUsingCustomFilter() {
    this.getToken(false);
    final StakeholderParamDto personStakeholder = new StakeholderParamDto();
    personStakeholder.setIdWorkpack(this.idWorkpack);
    personStakeholder.setIdPlan(this.idPlan);
    personStakeholder.setPerson(this.getPersonStakeholderParamDto(true, "user.test@openpmo.com"));
    personStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<EntityDto>> response = this.stakeholderPersonController.storePerson(
      personStakeholder
    );
    assertEquals(HTTP_STATUS_OK, response.getStatusCodeValue());

    final ResponseEntity<ResponseBase<StakeholderPersonDto>> responsePerson = this.stakeholderPersonController.index(
      this.idWorkpack,
      response.getBody().getData().getId()
    );
    assertEquals(HTTP_STATUS_OK, responsePerson.getStatusCodeValue());
    assertNotNull(responsePerson.getBody());
    assertNotNull(responsePerson.getBody().getData());

    final OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
    organizationStakeholder.setIdWorkpack(this.idWorkpack);
    organizationStakeholder.setIdOrganization(this.idOrganization);
    organizationStakeholder.setRoles(new ArrayList<>());
    final ResponseEntity<ResponseBase<Entity>> storeOrganizationResponse = this.stakeholderOrganizationController.storeOrganization(
      organizationStakeholder);
    assertEquals(HTTP_STATUS_OK, storeOrganizationResponse.getStatusCodeValue());

    final ResponseEntity<ResponseBase<StakeholderOrganizationDto>> responseOrganization = this.stakeholderOrganizationController.index(
      this.idWorkpack,
      this.idOrganization
    );
    assertEquals(HTTP_STATUS_OK, responseOrganization.getStatusCodeValue());
    assertNotNull(responseOrganization.getBody());
    assertNotNull(responseOrganization.getBody().getData());

    final ResponseEntity<ResponseBase<List<StakeholderDto>>> responseList = this.stakeholderController.index(
      this.idWorkpack,
      this.idFilter
    );
    assertEquals(HTTP_STATUS_OK, responseList.getStatusCodeValue());
    assertNotNull(responseList.getBody());
    assertNotNull(responseList.getBody().getData());
  }

  @TestConfiguration
  static class Config {
    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }

}
