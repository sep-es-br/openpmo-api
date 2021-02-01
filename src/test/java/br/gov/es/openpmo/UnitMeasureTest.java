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
import br.gov.es.openpmo.controller.UnitMeasureController;
import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.office.OfficeStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureStoreDto;
import br.gov.es.openpmo.dto.unitmeasure.UnitMeasureUpdateDto;

@Testcontainers
@SpringBootTest
public class UnitMeasureTest extends BaseTest {

    @Autowired
    private UnitMeasureController unitMeasureController;

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
            office.setName("Office Test UnitMeasure");
            office.setFullName("Office Test UnitMeasure ");
            ResponseEntity<ResponseBase<EntityDto>> response = officeController.save(office);
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getData());
            this.idOffice = response.getBody().getData().getId();
        }
    }

    @Test
    public void shouldCreateUnitMeasure() {
        UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
        unitMeasure.setName("UnitMeasure Test");
        unitMeasure.setFullName("UnitMeasure Test ADM ");
        unitMeasure.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = unitMeasureController.save(unitMeasure);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldUpdateUnitMeasure() {
        UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
        unitMeasure.setName("UnitMeasure Test update");
        unitMeasure.setFullName("UnitMeasure Test update ");
        unitMeasure.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = unitMeasureController.save(unitMeasure);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        UnitMeasureUpdateDto unitMeasureUpdate = new UnitMeasureUpdateDto();
        unitMeasureUpdate.setId(response.getBody().getData().getId());
        unitMeasureUpdate.setName("UnitMeasure updated");
        response = unitMeasureController.update(unitMeasureUpdate);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
    }

    @Test
    public void shouldDelete() {
        UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
        unitMeasure.setName("UnitMeasure Test delete");
        unitMeasure.setFullName("UnitMeasure Test delete ");
        unitMeasure.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = unitMeasureController.save(unitMeasure);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<Void> responseDelete = unitMeasureController.delete(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseDelete.getStatusCodeValue());
    }

    @Test
    public void shouldListAll() {
        UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
        unitMeasure.setName("UnitMeasure Test list");
        unitMeasure.setFullName("UnitMeasure Test list ");
        unitMeasure.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = unitMeasureController.save(unitMeasure);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<ResponseBase<List<UnitMeasureDto>>> responseList = unitMeasureController.indexBase(idOffice);
        Assertions.assertEquals(200, responseList.getStatusCodeValue());
        Assertions.assertNotNull(responseList.getBody());
        Assertions.assertNotNull(responseList.getBody().getData());
        Assertions.assertFalse(responseList.getBody().getData().isEmpty());
    }

    @Test
    public void shouldFindOne() {
        UnitMeasureStoreDto unitMeasure = new UnitMeasureStoreDto();
        unitMeasure.setName("UnitMeasure Test find");
        unitMeasure.setFullName("UnitMeasure Test find ");
        unitMeasure.setIdOffice(idOffice);
        ResponseEntity<ResponseBase<EntityDto>> response = unitMeasureController.save(unitMeasure);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        ResponseEntity<ResponseBase<UnitMeasureDto>> responseFind = unitMeasureController.findById(response.getBody().getData().getId());
        Assertions.assertEquals(200, responseFind.getStatusCodeValue());
        Assertions.assertNotNull(responseFind.getBody());
        Assertions.assertNotNull(responseFind.getBody().getData());
        Assertions.assertEquals("UnitMeasure Test find", responseFind.getBody().getData().getName());
    }




}
