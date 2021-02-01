package br.gov.es.openpmo;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.openpmo.controller.OfficeController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.office.OfficeUpdateDto;

@Testcontainers
@SpringBootTest
public class OfficeTest extends BaseTest {

    @Autowired
    private OfficeController officeController;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @Test
    public void shouldCreateOffice() {
        OfficeStoreDto office = new OfficeStoreDto();
        office.setName("Office Test");
        office.setFullName("Office Test ADM ");
        ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdateOffice() {
        OfficeStoreDto office = new OfficeStoreDto();
        office.setName("Office Test update");
        office.setFullName("Office Test update ");
        ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        OfficeUpdateDto officeUpdate = new OfficeUpdateDto();
        officeUpdate.setId(response.getBody().getData().getId());
        officeUpdate.setName("Office updated");
        response = officeController.update(officeUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        OfficeStoreDto office = new OfficeStoreDto();
        office.setName("Office Test delete");
        office.setFullName("Office Test delete ");
        ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = officeController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        OfficeStoreDto office = new OfficeStoreDto();
        office.setName("Office Test list");
        office.setFullName("Office Test list ");
        ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<OfficeDto>>> responseList = officeController.indexBase(getToken(true));
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());

        responseList = officeController.indexBase(getToken(false));
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertTrue(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        OfficeStoreDto office = new OfficeStoreDto();
        office.setName("Office Test find");
        office.setFullName("Office Test find ");
        ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<OfficeDto>> responseFind = officeController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("Office Test find", responseFind.getBody().getData().getName());
    }


}
