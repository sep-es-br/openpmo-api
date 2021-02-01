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
import br.gov.es.openpmo.controller.OrganizationController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.organization.OrganizationStoreDto;
import br.gov.es.openpmo.dto.organization.OrganizationUpdateDto;

@Testcontainers
@SpringBootTest
public class OrganizationTest extends BaseTest {

    @Autowired
    private OrganizationController organizationController;

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
            office.setName("Office Test Organization");
            office.setFullName("Office Test Organization ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateOrganization() {
        OrganizationStoreDto organization = new OrganizationStoreDto();
        organization.setName("Organization Test");
        organization.setFullName("Organization Test ADM ");
        organization.setEmail("organization@openpmo.com");
        organization.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdateOrganization() {
        OrganizationStoreDto organization = new OrganizationStoreDto();
        organization.setName("Organization Test update");
        organization.setFullName("Organization Test update ");
        organization.setEmail("organization@openpmo.com");
        organization.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        OrganizationUpdateDto organizationUpdate = new OrganizationUpdateDto();
        organizationUpdate.setId(response.getBody().getData().getId());
        organizationUpdate.setName("Organization updated");
        response = organizationController.update(organizationUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        OrganizationStoreDto organization = new OrganizationStoreDto();
        organization.setName("Organization Test delete");
        organization.setFullName("Organization Test delete ");
        organization.setEmail("organization@openpmo.com");
        organization.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = organizationController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        OrganizationStoreDto organization = new OrganizationStoreDto();
        organization.setName("Organization Test list");
        organization.setFullName("Organization Test list ");
        organization.setEmail("organization@openpmo.com");
        organization.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<OrganizationDto>>> responseList = organizationController.indexBase(idOffice);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        OrganizationStoreDto organization = new OrganizationStoreDto();
        organization.setName("Organization Test find");
        organization.setFullName("Organization Test find ");
        organization.setEmail("organization@openpmo.com");
        organization.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = organizationController.save(organization);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<OrganizationDto>> responseFind = organizationController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("Organization Test find", responseFind.getBody().getData().getName());
    }




}
