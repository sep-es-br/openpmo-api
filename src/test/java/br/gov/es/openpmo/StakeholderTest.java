package br.gov.es.openpmo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import br.gov.es.openpmo.controller.OrganizationController;
import br.gov.es.openpmo.controller.StakeholderController;
import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.controller.PlanController;
import br.gov.es.openpmo.controller.PlanModelController;
import br.gov.es.openpmo.controller.StakeholderOrganizationController;
import br.gov.es.openpmo.controller.StakeholderPersonController;
import br.gov.es.openpmo.controller.WorkpackController;
import br.gov.es.openpmo.controller.WorkpackModelController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.ResponseBaseWorkpackModelDetail;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.plan.PlanStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.stakeholder.OrganizationStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.PersonStakeholderParamDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderOrganizationDto;
import br.gov.es.openpmo.dto.stakeholder.StakeholderPersonDto;
import br.gov.es.openpmo.dto.workpack.WorkpackParamDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelParamDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.service.PersonService;

@Testcontainers
@SpringBootTest
public class StakeholderTest extends BaseTest {

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

    private Long idOffice;
    private Long idPlanModel;
    private Long idDomain;
    private Long idPlan;
    private Long idWorkpack;
    private Long idOrganization;

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
        if (this.idOrganization == null) {
            OrganizationStoreDto organization = new OrganizationStoreDto();
            organization.setName("Organization Test");
            organization.setFullName("Organization Test ADM ");
            organization.setEmail("organization@openpmo.com");
            organization.setIdOffice(idOffice);
            ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
            Assertions.assertEquals(200, response.getStatusCodeValue());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOrganization = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateStakeholder() {
        PersonStakeholderParamDto personStakeholder = new PersonStakeholderParamDto();
        personStakeholder.setIdWorkpack(idWorkpack);
        personStakeholder.setEmail("person.stakeholder@openpmo.com");
        personStakeholder.setFullName("Stakeholder Test");
        personStakeholder.setContactEmail("person.stakeholder@openpmo.com");
        personStakeholder.setRoles(new ArrayList<>());
        ResponseEntity<ResponseBase<Entity>> response = stakeholderPersonController.storePerson(personStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
        organizationStakeholder.setIdWorkpack(idWorkpack);
        organizationStakeholder.setIdOrganization(idOrganization);
        organizationStakeholder.setRoles(new ArrayList<>());
        response = stakeholderOrganizationController.storeOrganization(organizationStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());

    }

    @Test
    public void shouldDelete() {
        getToken(true);
        PersonStakeholderParamDto personStakeholder = new PersonStakeholderParamDto();
        personStakeholder.setIdWorkpack(idWorkpack);
        personStakeholder.setEmail("user.test@openpmo.com");
        personStakeholder.setFullName("Stakeholder Test");
        personStakeholder.setContactEmail("user.test@openpmo.com");
        personStakeholder.setRoles(new ArrayList<>());
        ResponseEntity<ResponseBase<Entity>> response = stakeholderPersonController.storePerson(personStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Optional<Person> person = personService.findByEmail("user.test@openpmo.com");
        Assertions.assertTrue(person.isPresent());
        ResponseEntity<Void> responseDelete = stakeholderPersonController.deletePerson(idWorkpack,
                                                                                       person.get().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());

        OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
        organizationStakeholder.setIdWorkpack(idWorkpack);
        organizationStakeholder.setIdOrganization(idOrganization);
        organizationStakeholder.setRoles(new ArrayList<>());
        response = stakeholderOrganizationController.storeOrganization(organizationStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        responseDelete = stakeholderOrganizationController.deleteOrganization(idWorkpack, idOrganization);
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        getToken(true);
        PersonStakeholderParamDto personStakeholder = new PersonStakeholderParamDto();
        personStakeholder.setIdWorkpack(idWorkpack);
        personStakeholder.setEmail("user.test@openpmo.com");
        personStakeholder.setFullName("Stakeholder Test");
        personStakeholder.setContactEmail("user.test@openpmo.com");
        personStakeholder.setRoles(new ArrayList<>());
        ResponseEntity<ResponseBase<Entity>> response = stakeholderPersonController.storePerson(personStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<ResponseBase<StakeholderPersonDto>> responsePerson = stakeholderPersonController.index(
            idWorkpack, "user.test@openpmo.com");
        Assertions.assertEquals(200, responsePerson.getStatusCodeValue());
        Assertions.assertNotNull(responsePerson.getBody());
        Assertions.assertNotNull(responsePerson.getBody().getData());

        OrganizationStakeholderParamDto organizationStakeholder = new OrganizationStakeholderParamDto();
        organizationStakeholder.setIdWorkpack(idWorkpack);
        organizationStakeholder.setIdOrganization(idOrganization);
        organizationStakeholder.setRoles(new ArrayList<>());
        response = stakeholderOrganizationController.storeOrganization(organizationStakeholder);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<ResponseBase<StakeholderOrganizationDto>> responseOrganization = stakeholderOrganizationController.index(
            idWorkpack, idOrganization);
        Assertions.assertEquals(200, responseOrganization.getStatusCodeValue());
        Assertions.assertNotNull(responseOrganization.getBody());
        Assertions.assertNotNull(responseOrganization.getBody().getData());

        ResponseEntity<ResponseBase<List<StakeholderDto>>> responseList = stakeholderController.index(idWorkpack);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
    }

    private Long getIdWorkpack() {
        WorkpackModelParamDto workpackModelParam = getWorkpackModelParamProject("Project", idPlanModel, idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = workpackModelController.save(workpackModelParam);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBaseWorkpackModelDetail> responseFind = workpackModelController.find(
            response.getBody().getData().getId());
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
