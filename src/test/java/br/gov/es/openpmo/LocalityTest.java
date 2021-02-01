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
import br.gov.es.openpmo.controller.LocalityController;
import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.domain.DomainStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityDetailDto;
import br.gov.es.openpmo.dto.domain.LocalityDto;
import br.gov.es.openpmo.dto.domain.LocalityPropertyDto;
import br.gov.es.openpmo.dto.domain.LocalityStoreDto;
import br.gov.es.openpmo.dto.domain.LocalityUpdateDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.enumerator.LocalityTypesEnum;

@Testcontainers
@SpringBootTest
public class LocalityTest extends BaseTest {

    @Autowired
    private LocalityController localityController;

    @Autowired
    private OfficeController officeController;

    @Autowired
    private DomainController domainController;

    private Long idOffice;
    private Long idDomain;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @BeforeEach
    public void loadDomain() {
        if (this.idOffice == null) {
            OfficeStoreDto office = new OfficeStoreDto();
            office.setName("Office Test Locality");
            office.setFullName("Office Test Locality ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
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
    }

    @Test
    public void shouldCreateLocality() {
        LocalityStoreDto locality = new LocalityStoreDto();
        locality.setName("Locality Test");
        locality.setFullName("Locality Test ADM ");
        locality.setType(LocalityTypesEnum.COUNTRY);
        locality.setIdDomain(idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = localityController.save(locality);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdateLocality() {
        LocalityStoreDto locality = new LocalityStoreDto();
        locality.setName("Locality Test update");
        locality.setFullName("Locality Test update ");
        locality.setType(LocalityTypesEnum.COUNTRY);
        locality.setIdDomain(idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = localityController.save(locality);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        LocalityUpdateDto localityUpdate = new LocalityUpdateDto();
        localityUpdate.setId(response.getBody().getData().getId());
        localityUpdate.setName("Locality updated");
        localityUpdate.setFullName("Locality Test update ");
        localityUpdate.setType(LocalityTypesEnum.COUNTRY);
        localityUpdate.setIdDomain(idDomain);
        response = localityController.update(localityUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        LocalityStoreDto locality = new LocalityStoreDto();
        locality.setName("Locality Test delete");
        locality.setFullName("Locality Test delete ");
        locality.setType(LocalityTypesEnum.COUNTRY);
        locality.setIdDomain(idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = localityController.save(locality);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = localityController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        LocalityStoreDto locality = new LocalityStoreDto();
        locality.setName("Locality Test list");
        locality.setFullName("Locality Test list ");
        locality.setType(LocalityTypesEnum.COUNTRY);
        locality.setIdDomain(idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = localityController.save(locality);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<LocalityDto>>> responseList = localityController.indexBase(idDomain);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());

        responseList = localityController.indexBaseFirstLevel(idDomain);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());

        ResponseEntity<ResponseBase<List<LocalityPropertyDto>>> responseProperty = localityController.listProperty(idDomain);
        Assertions.assertEquals(200, responseProperty.getStatusCodeValue());
        Assertions.assertNotNull(responseProperty.getBody());
        Assertions.assertNotNull(responseProperty.getBody().getData());
        Assertions.assertFalse(responseProperty.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        LocalityStoreDto locality = new LocalityStoreDto();
        locality.setName("Locality Test find");
        locality.setFullName("Locality Test find ");
        locality.setType(LocalityTypesEnum.COUNTRY);
        locality.setIdDomain(idDomain);
        ResponseEntity<ResponseBase<EntityDto>> response = localityController.save(locality);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<LocalityDetailDto>> responseFind = localityController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("Locality Test find", responseFind.getBody().getData().getName());
    }

}
