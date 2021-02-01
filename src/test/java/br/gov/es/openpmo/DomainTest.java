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

import br.gov.es.openpmo.controller.DomainController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainDto;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.DomainUpdateDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;

@Testcontainers
@SpringBootTest
public class DomainTest extends BaseTest {

    @Autowired
    private DomainController domainController;

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
            office.setName("Office Test Domain");
            office.setFullName("Office Test Domain ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateDomain() {
        DomainStoreDto domain = new DomainStoreDto();
        domain.setName("Domain Test");
        domain.setFullName("Domain Test ADM ");
        domain.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdateDomain() {
        DomainStoreDto domain = new DomainStoreDto();
        domain.setName("Domain Test update");
        domain.setFullName("Domain Test update ");
        domain.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        DomainUpdateDto domainUpdate = new DomainUpdateDto();
        domainUpdate.setId(response.getBody().getData().getId());
        domainUpdate.setName("Domain updated");
        response = domainController.update(domainUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        DomainStoreDto domain = new DomainStoreDto();
        domain.setName("Domain Test delete");
        domain.setFullName("Domain Test delete ");
        domain.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = domainController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        DomainStoreDto domain = new DomainStoreDto();
        domain.setName("Domain Test list");
        domain.setFullName("Domain Test list ");
        domain.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<DomainDto>>> responseList = domainController.indexBase(idOffice);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        DomainStoreDto domain = new DomainStoreDto();
        domain.setName("Domain Test find");
        domain.setFullName("Domain Test find ");
        domain.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = domainController.save(domain);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<DomainDto>> responseFind = domainController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("Domain Test find", responseFind.getBody().getData().getName());
    }

}
